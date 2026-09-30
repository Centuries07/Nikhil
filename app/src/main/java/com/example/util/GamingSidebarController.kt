package com.example.util

import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import com.example.service.GamingSidebarService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object GamingSidebarController {

    private val _isServiceRunning = MutableStateFlow(false)
    val isServiceRunning: StateFlow<Boolean> = _isServiceRunning.asStateFlow()

    private val _isSidebarExpanded = MutableStateFlow(false)
    val isSidebarExpanded: StateFlow<Boolean> = _isSidebarExpanded.asStateFlow()

    private val _isOverlayHidden = MutableStateFlow(false)
    val isOverlayHidden: StateFlow<Boolean> = _isOverlayHidden.asStateFlow()

    private val _isZoomLoupeActive = MutableStateFlow(false)
    val isZoomLoupeActive: StateFlow<Boolean> = _isZoomLoupeActive.asStateFlow()

    private val _zoomMagnification = MutableStateFlow(2.0f)
    val zoomMagnification: StateFlow<Float> = _zoomMagnification.asStateFlow()

    private val _loupeSizeDp = MutableStateFlow(160)
    val loupeSizeDp: StateFlow<Int> = _loupeSizeDp.asStateFlow()

    private val _loupeColorHex = MutableStateFlow(0xFF00E5FF)
    val loupeColorHex: StateFlow<Long> = _loupeColorHex.asStateFlow()

    private val _is90FpsForced = MutableStateFlow(true)
    val is90FpsForced: StateFlow<Boolean> = _is90FpsForced.asStateFlow()

    private val _isThermalBypassActive = MutableStateFlow(true)
    val isThermalBypassActive: StateFlow<Boolean> = _isThermalBypassActive.asStateFlow()

    private val _currentFps = MutableStateFlow(90)
    val currentFps: StateFlow<Int> = _currentFps.asStateFlow()

    private val _isRightSideDocked = MutableStateFlow(false)
    val isRightSideDocked: StateFlow<Boolean> = _isRightSideDocked.asStateFlow()

    private val _sidebarX = MutableStateFlow(0)
    val sidebarX: StateFlow<Int> = _sidebarX.asStateFlow()

    private val _sidebarY = MutableStateFlow(320)
    val sidebarY: StateFlow<Int> = _sidebarY.asStateFlow()

    private val _loupeX = MutableStateFlow(0)
    val loupeX: StateFlow<Int> = _loupeX.asStateFlow()

    private val _loupeY = MutableStateFlow(0)
    val loupeY: StateFlow<Int> = _loupeY.asStateFlow()

    fun setServiceRunning(running: Boolean) {
        _isServiceRunning.value = running
    }

    fun toggleSidebarExpanded() {
        _isSidebarExpanded.value = !_isSidebarExpanded.value
    }

    fun setSidebarExpanded(expanded: Boolean) {
        _isSidebarExpanded.value = expanded
    }

    fun toggleOverlayHidden(): Boolean {
        _isOverlayHidden.value = !_isOverlayHidden.value
        return _isOverlayHidden.value
    }

    fun setOverlayHidden(hidden: Boolean) {
        _isOverlayHidden.value = hidden
    }

    fun toggleZoomLoupe(): Boolean {
        val next = !_isZoomLoupeActive.value
        setZoomLoupeActive(next)
        return next
    }

    fun setZoomLoupeActive(active: Boolean) {
        _isZoomLoupeActive.value = active
        if (active) {
            val scale = _zoomMagnification.value
            com.example.service.GamingAccessibilityService.zoomIn(scale)
            try {
                Runtime.getRuntime().exec(
                    arrayOf(
                        "sh",
                        "-c",
                        "settings put secure accessibility_display_magnification_scale $scale; settings put secure accessibility_magnification_mode 2; settings put secure accessibility_display_magnification_enabled 1; cmd accessibility set-magnification-activated 1; cmd accessibility set-magnification-scale $scale"
                    )
                )
            } catch (e: Exception) {
                // Ignored if shell restricted
            }
        } else {
            com.example.service.GamingAccessibilityService.resetZoom()
            try {
                Runtime.getRuntime().exec(
                    arrayOf("sh", "-c", "cmd accessibility set-magnification-activated 0")
                )
            } catch (e: Exception) {
                // Ignored
            }
        }
    }

    fun setZoomMagnification(mag: Float) {
        _zoomMagnification.value = mag
        if (_isZoomLoupeActive.value) {
            com.example.service.GamingAccessibilityService.zoomIn(mag)
        }
    }

    fun setLoupeSize(sizeDp: Int) {
        _loupeSizeDp.value = sizeDp.coerceIn(100, 260)
    }

    fun setLoupeColor(colorHex: Long) {
        _loupeColorHex.value = colorHex
    }

    fun setSidebarPosition(x: Int, y: Int) {
        _sidebarX.value = x
        _sidebarY.value = y
    }

    fun setLoupePosition(x: Int, y: Int) {
        _loupeX.value = x
        _loupeY.value = y
    }

    fun toggle90FpsForced(): Boolean {
        _is90FpsForced.value = !_is90FpsForced.value
        return _is90FpsForced.value
    }

    fun toggleThermalBypass(): Boolean {
        _isThermalBypassActive.value = !_isThermalBypassActive.value
        return _isThermalBypassActive.value
    }

    fun toggleDockSide(): Boolean {
        _isRightSideDocked.value = !_isRightSideDocked.value
        return _isRightSideDocked.value
    }

    fun updateFps(fps: Int) {
        _currentFps.value = fps
    }

    fun canDrawOverlays(context: Context): Boolean {
        return Settings.canDrawOverlays(context)
    }

    fun openOverlaySettings(context: Context) {
        try {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                android.net.Uri.parse("package:${context.packageName}")
            )
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        } catch (e: Exception) {
            val fallback = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
            fallback.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(fallback)
        }
    }

    fun openAccessibilityMagnifierSettings(context: Context) {
        try {
            val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        } catch (e: Exception) {
            // Safe fallback
        }
    }

    fun startSidebar(context: Context) {
        if (!canDrawOverlays(context)) {
            openOverlaySettings(context)
            return
        }

        val intent = Intent(context, GamingSidebarService::class.java).apply {
            action = GamingSidebarService.ACTION_START
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent)
        } else {
            context.startService(intent)
        }
        _isServiceRunning.value = true
        _isOverlayHidden.value = false
    }

    fun stopSidebar(context: Context) {
        val intent = Intent(context, GamingSidebarService::class.java).apply {
            action = GamingSidebarService.ACTION_STOP
        }
        context.startService(intent)
        _isServiceRunning.value = false
        _isSidebarExpanded.value = false
        _isZoomLoupeActive.value = false
        _isOverlayHidden.value = false
    }
}
