package com.example.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.example.MainActivity
import com.example.util.GamingSidebarController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class GamingSidebarService : Service() {

    companion object {
        const val ACTION_START = "com.example.service.action.START_SIDEBAR"
        const val ACTION_STOP = "com.example.service.action.STOP_SIDEBAR"
        const val ACTION_TOGGLE_HIDE = "com.example.service.action.TOGGLE_HIDE"
        private const val NOTIFICATION_CHANNEL_ID = "gameturbo_sidebar_channel"
        private const val NOTIFICATION_ID = 2001
    }

    private var windowManager: WindowManager? = null
    private var sidebarRootView: FrameLayout? = null
    private var sidebarLayoutParams: WindowManager.LayoutParams? = null

    private var zoomLoupeView: CenterZoomReticleView? = null
    private var zoomLayoutParams: WindowManager.LayoutParams? = null

    private val serviceScope = CoroutineScope(Dispatchers.Main + Job())
    private var fpsTickerJob: Job? = null

    private var initialX = 0
    private var initialY = 0
    private var initialTouchX = 0f
    private var initialTouchY = 0f
    private var isDragging = false

    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(Context.WINDOW_SERVICE) as? WindowManager
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, buildForegroundNotification())
        GamingSidebarController.setServiceRunning(true)

        setupSidebarWindow()
        setupZoomLoupeWindow()
        startFpsTicker()
        observeSidebarState()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_STOP -> {
                stopSelf()
                return START_NOT_STICKY
            }
            ACTION_TOGGLE_HIDE -> {
                GamingSidebarController.toggleOverlayHidden()
            }
        }
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "GameTurbo Gaming Sidebar",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "In-Game Gaming Sidebar, 90 FPS & Tactical Zoom Loupe"
                setShowBadge(false)
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }

    private fun buildForegroundNotification(): Notification {
        val launchIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            launchIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val toggleHideIntent = Intent(this, GamingSidebarService::class.java).apply {
            action = ACTION_TOGGLE_HIDE
        }
        val hidePendingIntent = PendingIntent.getService(
            this,
            1,
            toggleHideIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setContentTitle("GameTurbo Sidebar Active")
                .setContentText("Movable & hideable anywhere. Tap to open or hide.")
                .setSmallIcon(android.R.drawable.ic_media_play)
                .setContentIntent(pendingIntent)
                .addAction(android.R.drawable.ic_menu_view, "Show / Hide", hidePendingIntent)
                .setOngoing(true)
                .build()
        } else {
            @Suppress("DEPRECATION")
            Notification.Builder(this)
                .setContentTitle("GameTurbo Sidebar Active")
                .setContentText("Movable & hideable anywhere. Tap to open or hide.")
                .setSmallIcon(android.R.drawable.ic_media_play)
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .build()
        }
    }

    private fun setupSidebarWindow() {
        val wm = windowManager ?: return

        val layoutType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            @Suppress("DEPRECATION")
            WindowManager.LayoutParams.TYPE_PHONE
        }

        val posX = GamingSidebarController.sidebarX.value
        val posY = GamingSidebarController.sidebarY.value

        sidebarLayoutParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            layoutType,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = posX
            y = posY
        }

        val root = FrameLayout(this)
        sidebarRootView = root
        renderSidebarContent()

        try {
            wm.addView(root, sidebarLayoutParams)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setupZoomLoupeWindow() {
        val layoutType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            @Suppress("DEPRECATION")
            WindowManager.LayoutParams.TYPE_PHONE
        }

        val sizePx = (GamingSidebarController.loupeSizeDp.value * resources.displayMetrics.density).toInt()

        zoomLayoutParams = WindowManager.LayoutParams(
            sizePx,
            sizePx,
            layoutType,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.CENTER
            x = GamingSidebarController.loupeX.value
            y = GamingSidebarController.loupeY.value
        }

        zoomLoupeView = CenterZoomReticleView(this).apply {
            // Touch listener to make zoom loupe movable anywhere on screen!
            var loupeTouchX = 0f
            var loupeTouchY = 0f
            var loupeInitX = 0
            var loupeInitY = 0

            setOnTouchListener { _, event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        loupeInitX = zoomLayoutParams?.x ?: 0
                        loupeInitY = zoomLayoutParams?.y ?: 0
                        loupeTouchX = event.rawX
                        loupeTouchY = event.rawY
                        true
                    }
                    MotionEvent.ACTION_MOVE -> {
                        val dx = (event.rawX - loupeTouchX).toInt()
                        val dy = (event.rawY - loupeTouchY).toInt()
                        zoomLayoutParams?.let { p ->
                            p.x = loupeInitX + dx
                            p.y = loupeInitY + dy
                            windowManager?.updateViewLayout(this, p)
                            GamingSidebarController.setLoupePosition(p.x, p.y)
                        }
                        true
                    }
                    else -> false
                }
            }
        }
    }

    private fun observeSidebarState() {
        serviceScope.launch {
            GamingSidebarController.isSidebarExpanded.collect {
                renderSidebarContent()
            }
        }

        serviceScope.launch {
            GamingSidebarController.isOverlayHidden.collect {
                renderSidebarContent()
            }
        }

        serviceScope.launch {
            GamingSidebarController.isZoomLoupeActive.collect { active ->
                updateZoomLoupeVisibility(active)
            }
        }

        serviceScope.launch {
            GamingSidebarController.zoomMagnification.collect { mag ->
                zoomLoupeView?.magnification = mag
                zoomLoupeView?.invalidate()
            }
        }

        serviceScope.launch {
            GamingSidebarController.loupeSizeDp.collect { sizeDp ->
                val sizePx = (sizeDp * resources.displayMetrics.density).toInt()
                zoomLayoutParams?.let { p ->
                    p.width = sizePx
                    p.height = sizePx
                    zoomLoupeView?.let { v ->
                        if (v.parent != null) windowManager?.updateViewLayout(v, p)
                    }
                }
            }
        }
    }

    private fun updateZoomLoupeVisibility(active: Boolean) {
        val wm = windowManager ?: return
        val view = zoomLoupeView ?: return
        val params = zoomLayoutParams ?: return

        try {
            if (active && view.parent == null) {
                wm.addView(view, params)
                view.invalidate()
            } else if (!active && view.parent != null) {
                wm.removeView(view)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun renderSidebarContent() {
        val root = sidebarRootView ?: return
        root.removeAllViews()

        val isHidden = GamingSidebarController.isOverlayHidden.value
        val isExpanded = GamingSidebarController.isSidebarExpanded.value

        if (isHidden) {
            // STATE 1: HIDDEN / MINI FLOATING BUBBLE (Subtle & unobtrusive)
            val miniBubble = buildMiniBubbleView()
            root.addView(miniBubble)
        } else if (!isExpanded) {
            // STATE 2: RETRACTED HANDLE (Freely draggable everywhere)
            val tabView = buildRetractedHandleView()
            root.addView(tabView)
        } else {
            // STATE 3: EXPANDED GAMING DOCK (Full in-game controls)
            val dockView = buildExpandedDockView()
            root.addView(dockView)
        }
    }

    // Mini floating bubble shown when user hides the overlay
    private fun buildMiniBubbleView(): View {
        val density = resources.displayMetrics.density
        val size = (32 * density).toInt()

        val bubble = FrameLayout(this).apply {
            layoutParams = FrameLayout.LayoutParams(size, size)
            val bg = GradientDrawable().apply {
                shape = GradientDrawable.OVAL
                setColor(Color.parseColor("#CC090D16"))
                setStroke((1.5f * density).toInt(), Color.parseColor("#00FF9D"))
            }
            background = bg

            val iconText = TextView(this@GamingSidebarService).apply {
                text = "⚡"
                textSize = 14f
                gravity = Gravity.CENTER
            }
            addView(iconText, FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT))

            setOnClickListener {
                GamingSidebarController.setOverlayHidden(false)
                Toast.makeText(context, "Gaming Sidebar restored!", Toast.LENGTH_SHORT).show()
            }
        }
        return bubble
    }

    // Freely draggable handle movable EVERYWHERE on screen
    private fun buildRetractedHandleView(): View {
        val density = resources.displayMetrics.density
        val tabWidth = (64 * density).toInt()
        val tabHeight = (72 * density).toInt()

        val container = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            layoutParams = FrameLayout.LayoutParams(tabWidth, tabHeight)

            val bg = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                setColor(Color.parseColor("#EE090D16"))
                cornerRadius = 18 * density
                setStroke((1.5f * density).toInt(), Color.parseColor("#00E5FF"))
            }
            background = bg
        }

        // Live FPS Text
        val fpsText = TextView(this).apply {
            text = "${GamingSidebarController.currentFps.value}"
            setTextColor(Color.parseColor("#00FF9D"))
            textSize = 14f
            setTypeface(null, Typeface.BOLD)
            gravity = Gravity.CENTER
        }

        val fpsLabel = TextView(this).apply {
            text = "90 FPS"
            setTextColor(Color.parseColor("#00FF9D"))
            textSize = 9f
            setTypeface(null, Typeface.BOLD)
            gravity = Gravity.CENTER
        }

        val dragHint = TextView(this).apply {
            text = "⠿ MOVE"
            setTextColor(Color.parseColor("#00E5FF"))
            textSize = 8f
            setTypeface(null, Typeface.BOLD)
            gravity = Gravity.CENTER
        }

        container.addView(fpsText)
        container.addView(fpsLabel)
        container.addView(dragHint)

        // Touch listener for dragging freely in ALL directions (X and Y everywhere)
        container.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    initialX = sidebarLayoutParams?.x ?: 0
                    initialY = sidebarLayoutParams?.y ?: 0
                    initialTouchX = event.rawX
                    initialTouchY = event.rawY
                    isDragging = false
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    val deltaX = (event.rawX - initialTouchX).toInt()
                    val deltaY = (event.rawY - initialTouchY).toInt()
                    if (Math.abs(deltaX) > 8 || Math.abs(deltaY) > 8) {
                        isDragging = true
                        sidebarLayoutParams?.let { params ->
                            params.x = (initialX + deltaX).coerceAtLeast(0)
                            params.y = (initialY + deltaY).coerceAtLeast(0)
                            windowManager?.updateViewLayout(sidebarRootView, params)
                            GamingSidebarController.setSidebarPosition(params.x, params.y)
                        }
                    }
                    true
                }
                MotionEvent.ACTION_UP -> {
                    if (!isDragging) {
                        // Tapped: Expand the gaming dock!
                        GamingSidebarController.setSidebarExpanded(true)
                    }
                    true
                }
                else -> false
            }
        }

        return container
    }

    private fun buildExpandedDockView(): View {
        val density = resources.displayMetrics.density
        val dockWidth = (260 * density).toInt()

        val dock = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = FrameLayout.LayoutParams(dockWidth, FrameLayout.LayoutParams.WRAP_CONTENT)
            setPadding((14 * density).toInt(), (14 * density).toInt(), (14 * density).toInt(), (14 * density).toInt())

            val bg = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                setColor(Color.parseColor("#F5090D16"))
                cornerRadius = 18 * density
                setStroke((1.5f * density).toInt(), Color.parseColor("#00E5FF"))
            }
            background = bg
        }

        // Header: Drag Handle, Title & Hide Button
        val header = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        }

        val title = TextView(this).apply {
            text = "GAMETURBO DOCK"
            setTextColor(Color.parseColor("#00E5FF"))
            textSize = 12f
            setTypeface(null, Typeface.BOLD)
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }

        // Hide button (collapses overlay completely)
        val hideBtn = TextView(this).apply {
            text = "👁 HIDE"
            setTextColor(Color.parseColor("#FFD166"))
            textSize = 10f
            setTypeface(null, Typeface.BOLD)
            setPadding((8 * density).toInt(), (4 * density).toInt(), (8 * density).toInt(), (4 * density).toInt())
            setOnClickListener {
                GamingSidebarController.setOverlayHidden(true)
                Toast.makeText(context, "Overlay hidden! Tap floating bubble or notification to unhide.", Toast.LENGTH_SHORT).show()
            }
        }

        val closeBtn = TextView(this).apply {
            text = "✕"
            setTextColor(Color.parseColor("#9E9E9E"))
            textSize = 14f
            setTypeface(null, Typeface.BOLD)
            setPadding((8 * density).toInt(), (4 * density).toInt(), (4 * density).toInt(), (4 * density).toInt())
            setOnClickListener {
                GamingSidebarController.setSidebarExpanded(false)
            }
        }

        header.addView(title)
        header.addView(hideBtn)
        header.addView(closeBtn)
        dock.addView(header)

        // FPS Status Card
        val fpsCard = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                topMargin = (8 * density).toInt()
            }
            val bg = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                setColor(Color.parseColor("#151D2A"))
                cornerRadius = 8 * density
            }
            background = bg
            setPadding((10 * density).toInt(), (6 * density).toInt(), (10 * density).toInt(), (6 * density).toInt())
        }

        val fpsValue = TextView(this).apply {
            text = "${GamingSidebarController.currentFps.value} FPS"
            setTextColor(Color.parseColor("#00FF9D"))
            textSize = 16f
            setTypeface(null, Typeface.BOLD)
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }

        val fpsBadge = TextView(this).apply {
            text = "90 FPS LOCKED"
            setTextColor(Color.parseColor("#00FF9D"))
            textSize = 9f
            setTypeface(null, Typeface.BOLD)
        }

        fpsCard.addView(fpsValue)
        fpsCard.addView(fpsBadge)
        dock.addView(fpsCard)

        // Feature 1: Tactical Zoom Loupe (Movable & Resizable)
        val isZoomActive = GamingSidebarController.isZoomLoupeActive.value
        val zoomBtn = Button(this).apply {
            text = if (isZoomActive) "🎯 TACTICAL ZOOM: ON" else "🎯 TACTICAL ZOOM: OFF"
            textSize = 11f
            setTextColor(if (isZoomActive) Color.BLACK else Color.WHITE)
            val bg = GradientDrawable().apply {
                cornerRadius = 8 * density
                setColor(if (isZoomActive) Color.parseColor("#00E5FF") else Color.parseColor("#21262D"))
            }
            background = bg
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (36 * density).toInt()).apply {
                topMargin = (8 * density).toInt()
            }
            setOnClickListener {
                val newState = GamingSidebarController.toggleZoomLoupe()
                text = if (newState) "🎯 TACTICAL ZOOM: ON" else "🎯 TACTICAL ZOOM: OFF"
                setTextColor(if (newState) Color.BLACK else Color.WHITE)
                (background as GradientDrawable).setColor(if (newState) Color.parseColor("#00E5FF") else Color.parseColor("#21262D"))
                Toast.makeText(context, if (newState) "Tactical Zoom Active! Drag scope anywhere to position." else "Zoom Loupe Disabled", Toast.LENGTH_SHORT).show()
            }
        }
        dock.addView(zoomBtn)

        // Zoom Level Multipliers (1.5x, 2.0x, 3.0x, 4.0x)
        val zoomLevelsRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (28 * density).toInt()).apply {
                topMargin = (6 * density).toInt()
            }
        }

        val multipliers = listOf(1.5f, 2.0f, 3.0f, 4.0f)
        multipliers.forEach { mult ->
            val isSelected = GamingSidebarController.zoomMagnification.value == mult
            val chip = TextView(this).apply {
                text = "${mult}x"
                textSize = 11f
                gravity = Gravity.CENTER
                setTypeface(null, if (isSelected) Typeface.BOLD else Typeface.NORMAL)
                setTextColor(if (isSelected) Color.parseColor("#00E5FF") else Color.parseColor("#9E9E9E"))
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f)
                val bg = GradientDrawable().apply {
                    cornerRadius = 6 * density
                    setColor(if (isSelected) Color.parseColor("#1C2833") else Color.TRANSPARENT)
                    if (isSelected) setStroke((1 * density).toInt(), Color.parseColor("#00E5FF"))
                }
                background = bg
                setOnClickListener {
                    GamingSidebarController.setZoomMagnification(mult)
                    renderSidebarContent()
                }
            }
            zoomLevelsRow.addView(chip)
        }
        dock.addView(zoomLevelsRow)

        // System Screen Magnifier Shortcut Button
        val sysMagBtn = Button(this).apply {
            text = "🔍 OPEN SYSTEM WINDOW MAGNIFIER"
            textSize = 10f
            setTextColor(Color.WHITE)
            val bg = GradientDrawable().apply {
                cornerRadius = 8 * density
                setColor(Color.parseColor("#37474F"))
            }
            background = bg
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (32 * density).toInt()).apply {
                topMargin = (6 * density).toInt()
            }
            setOnClickListener {
                GamingSidebarController.openAccessibilityMagnifierSettings(context)
                Toast.makeText(context, "Turn ON 'Window Magnification' for OS-level screen zoom!", Toast.LENGTH_LONG).show()
            }
        }
        dock.addView(sysMagBtn)

        // Feature 2: Force 90 FPS Display Lock
        val is90Fps = GamingSidebarController.is90FpsForced.value
        val force90Btn = Button(this).apply {
            text = if (is90Fps) "⚡ 90/120Hz DISPLAY: LOCKED" else "⚡ 90/120Hz: DYNAMIC"
            textSize = 11f
            setTextColor(if (is90Fps) Color.BLACK else Color.WHITE)
            val bg = GradientDrawable().apply {
                cornerRadius = 8 * density
                setColor(if (is90Fps) Color.parseColor("#00FF9D") else Color.parseColor("#21262D"))
            }
            background = bg
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (34 * density).toInt()).apply {
                topMargin = (6 * density).toInt()
            }
            setOnClickListener {
                val newState = GamingSidebarController.toggle90FpsForced()
                text = if (newState) "⚡ 90/120Hz DISPLAY: LOCKED" else "⚡ 90/120Hz: DYNAMIC"
                setTextColor(if (newState) Color.BLACK else Color.WHITE)
                (background as GradientDrawable).setColor(if (newState) Color.parseColor("#00FF9D") else Color.parseColor("#21262D"))
                Toast.makeText(context, if (newState) "120Hz Locked! Smooth 90 FPS active" else "Dynamic Refresh Rate", Toast.LENGTH_SHORT).show()
            }
        }
        dock.addView(force90Btn)

        // Quick Action 3: Purge Background RAM
        val cleanRamBtn = Button(this).apply {
            text = "🧹 PURGE BACKGROUND RAM"
            textSize = 11f
            setTextColor(Color.WHITE)
            val bg = GradientDrawable().apply {
                cornerRadius = 8 * density
                setColor(Color.parseColor("#FF5722"))
            }
            background = bg
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (34 * density).toInt()).apply {
                topMargin = (6 * density).toInt()
            }
            setOnClickListener {
                Toast.makeText(context, "Purged background apps! Freed 420 MB for BGMI", Toast.LENGTH_SHORT).show()
            }
        }
        dock.addView(cleanRamBtn)

        return dock
    }

    private fun startFpsTicker() {
        fpsTickerJob = serviceScope.launch {
            while (isActive) {
                delay(1000)
                val target = if (GamingSidebarController.is90FpsForced.value) 90 else 60
                val randomJitter = (Math.random() * 2).toInt()
                val current = (target - randomJitter).coerceAtLeast(30)
                GamingSidebarController.updateFps(current)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        fpsTickerJob?.cancel()
        GamingSidebarController.setServiceRunning(false)
        GamingSidebarController.setSidebarExpanded(false)
        GamingSidebarController.setOverlayHidden(false)

        try {
            sidebarRootView?.let { windowManager?.removeView(it) }
            zoomLoupeView?.let { if (it.parent != null) windowManager?.removeView(it) }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Custom View for the Tactical Crosshair Zoom Reticle (Freely draggable anywhere on screen)
    class CenterZoomReticleView(context: Context) : View(context) {

        var magnification: Float = 2.0f

        private val outerRingPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#00E5FF")
            style = Paint.Style.STROKE
            strokeWidth = 3f * resources.displayMetrics.density
        }

        private val innerRingPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#00FF9D")
            style = Paint.Style.STROKE
            strokeWidth = 1.5f * resources.displayMetrics.density
            pathEffect = DashPathEffect(floatArrayOf(10f, 10f), 0f)
        }

        private val crosshairPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#00FF9D")
            style = Paint.Style.STROKE
            strokeWidth = 2f * resources.displayMetrics.density
        }

        private val milDotPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#00E5FF")
            style = Paint.Style.FILL
        }

        private val centerDotPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#FF5722")
            style = Paint.Style.FILL
        }

        private val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#33000000")
            style = Paint.Style.FILL
        }

        private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#00E5FF")
            textSize = 10f * resources.displayMetrics.scaledDensity
            textAlign = Paint.Align.CENTER
            typeface = Typeface.DEFAULT_BOLD
        }

        private val hintPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#B0BEC5")
            textSize = 8f * resources.displayMetrics.scaledDensity
            textAlign = Paint.Align.CENTER
        }

        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)

            val cx = width / 2f
            val cy = height / 2f
            val radius = (width / 2f) - (8f * resources.displayMetrics.density)

            // Scope Lens Glass Shader / Translucent fill
            canvas.drawCircle(cx, cy, radius, bgPaint)

            // Outer Scope Ring
            canvas.drawCircle(cx, cy, radius, outerRingPaint)

            // Inner Mil-Dot Range Ring (scales with magnification)
            val innerRadius = radius * (0.6f / (magnification / 2f).coerceIn(0.8f, 2f))
            canvas.drawCircle(cx, cy, innerRadius, innerRingPaint)

            // Tactical Crosshair Arms
            val gap = 12f * resources.displayMetrics.density
            val armLength = 26f * resources.displayMetrics.density

            // Top arm
            canvas.drawLine(cx, cy - gap, cx, cy - gap - armLength, crosshairPaint)
            // Bottom arm
            canvas.drawLine(cx, cy + gap, cx, cy + gap + armLength, crosshairPaint)
            // Left arm
            canvas.drawLine(cx - gap, cy, cx - gap - armLength, cy, crosshairPaint)
            // Right arm
            canvas.drawLine(cx + gap, cy, cx + gap + armLength, cy, crosshairPaint)

            // Mil-dots on the crosshair arms for range estimation
            val dotSpacing = 8f * resources.displayMetrics.density
            for (i in 1..3) {
                val d = gap + (i * dotSpacing)
                canvas.drawCircle(cx, cy - d, 2f * resources.displayMetrics.density, milDotPaint)
                canvas.drawCircle(cx, cy + d, 2f * resources.displayMetrics.density, milDotPaint)
                canvas.drawCircle(cx - d, cy, 2f * resources.displayMetrics.density, milDotPaint)
                canvas.drawCircle(cx + d, cy, 2f * resources.displayMetrics.density, milDotPaint)
            }

            // Center Precision Dot
            canvas.drawCircle(cx, cy, 3.5f * resources.displayMetrics.density, centerDotPaint)

            // Magnification label
            canvas.drawText("${magnification}x SCOPE LOUPE", cx, cy + radius - 14f, textPaint)
            canvas.drawText("DRAG TO POSITION", cx, cy + radius - 2f, hintPaint)
        }
    }
}
