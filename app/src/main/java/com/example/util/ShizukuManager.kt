package com.example.util

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import rikka.shizuku.Shizuku

enum class ShizukuConnectionState(val title: String, val description: String) {
    CONNECTED(
        title = "Authorized & Active",
        description = "Binder connection established. GameTurbo is authorized in Shizuku's application list."
    ),
    PERMISSION_NEEDED(
        title = "Running (Authorization Required)",
        description = "Shizuku service is running on your device! Tap 'Authorize' to grant access like ZArchiver."
    ),
    SERVICE_STOPPED(
        title = "Installed (Service Stopped)",
        description = "Shizuku is installed on your Vivo T4 / iQOO Z10, but Wireless Debugging service is offline."
    ),
    NOT_INSTALLED(
        title = "Not Installed",
        description = "Shizuku application is not installed on this device."
    )
}

data class ShizukuOptimizationTask(
    val id: String,
    val title: String,
    val command: String,
    val targetComponent: String,
    val isApplied: Boolean = false,
    val explanation: String
)

class ShizukuManager(private val context: Context) {

    val shizukuPackageName = "moe.shizuku.privileged.api"
    val permissionRequestCode = 1001

    @Volatile
    private var isBinderReceived = false

    private val binderReceivedListener = Shizuku.OnBinderReceivedListener {
        isBinderReceived = true
    }

    private val binderDeadListener = Shizuku.OnBinderDeadListener {
        isBinderReceived = false
    }

    init {
        try {
            Shizuku.addBinderReceivedListenerSticky(binderReceivedListener)
            Shizuku.addBinderDeadListener(binderDeadListener)
        } catch (e: Throwable) {
            // Safe fallback
        }
    }

    fun isShizukuInstalled(): Boolean {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                context.packageManager.getPackageInfo(
                    shizukuPackageName,
                    PackageManager.PackageInfoFlags.of(0)
                )
            } else {
                @Suppress("DEPRECATION")
                context.packageManager.getPackageInfo(shizukuPackageName, 0)
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun checkBinderStatus(simulatedConnected: Boolean): ShizukuConnectionState = withContext(Dispatchers.IO) {
        // Priority 1: Direct Binder check (if Shizuku is running, pingBinder is true regardless of package visibility)
        val isBinderAlive = try {
            Shizuku.pingBinder() || isBinderReceived
        } catch (e: Throwable) {
            isBinderReceived
        }

        if (isBinderAlive || simulatedConnected) {
            val hasPermission = try {
                if (Shizuku.isPreV11()) {
                    context.checkSelfPermission("moe.shizuku.manager.permission.API_V23") == PackageManager.PERMISSION_GRANTED
                } else {
                    Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED
                }
            } catch (e: Throwable) {
                simulatedConnected
            }

            return@withContext if (hasPermission || simulatedConnected) {
                ShizukuConnectionState.CONNECTED
            } else {
                ShizukuConnectionState.PERMISSION_NEEDED
            }
        }

        // Priority 2: If binder not alive, check if Shizuku app is installed on device
        if (isShizukuInstalled()) {
            ShizukuConnectionState.SERVICE_STOPPED
        } else {
            ShizukuConnectionState.NOT_INSTALLED
        }
    }

    fun requestShizukuPermission() {
        try {
            if (Shizuku.pingBinder()) {
                if (Shizuku.isPreV11()) {
                    val intent = Intent("moe.shizuku.manager.action.REQUEST_AUTHORIZATION")
                    intent.setPackage(shizukuPackageName)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(intent)
                } else {
                    Shizuku.requestPermission(permissionRequestCode)
                }
            } else {
                // If pingBinder is false but service is running, launch Shizuku's authorization intent
                val intent = Intent("moe.shizuku.manager.action.REQUEST_AUTHORIZATION")
                intent.setPackage(shizukuPackageName)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
            }
        } catch (e: Throwable) {
            openShizukuOrStore()
        }
    }

    fun openShizukuOrStore() {
        val launchIntent = context.packageManager.getLaunchIntentForPackage(shizukuPackageName)
        if (launchIntent != null) {
            launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(launchIntent)
        } else {
            try {
                val storeIntent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$shizukuPackageName"))
                storeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(storeIntent)
            } catch (e: Exception) {
                val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://shizuku.rikka.app/download/"))
                webIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(webIntent)
            }
        }
    }

    fun getStandardOptimizationTasks(): List<ShizukuOptimizationTask> {
        return listOf(
            ShizukuOptimizationTask(
                id = "anim_speed",
                title = "0.5x Window Animations (Zero-Lag UI)",
                command = "settings put global window_animation_scale 0.5 && settings put global transition_animation_scale 0.5",
                targetComponent = "Vivo T4 / iQOO Z10 System Compositor",
                explanation = "Halves the system animation delay for instantaneous weapon switching and peek-and-fire."
            ),
            ShizukuOptimizationTask(
                id = "lock_120hz",
                title = "Lock 120Hz/144Hz AMOLED Refresh Rate",
                command = "settings put system peak_refresh_rate 120.0 && settings put system min_refresh_rate 120.0",
                targetComponent = "AMOLED Display Controller",
                explanation = "Forces the panel to stay locked at 120Hz/144Hz, preventing OriginOS dynamic drops to 60Hz."
            ),
            ShizukuOptimizationTask(
                id = "bgmi_doze_bypass",
                title = "Disable Android 16 Doze Throttling for BGMI",
                command = "dumpsys deviceidle whitelist +com.pubg.imobile",
                targetComponent = "DeviceIdleController (Doze Mode)",
                explanation = "Stops Android 16 from putting network and CPU threads to sleep during classic matches."
            ),
            ShizukuOptimizationTask(
                id = "bgmi_run_bg",
                title = "Grant Unrestricted Background CPU Execution",
                command = "cmd appops set com.pubg.imobile RUN_IN_BACKGROUND allow",
                targetComponent = "AppOps Service",
                explanation = "Overrides aggressive FuntouchOS / OriginOS memory freezer when returning to game."
            ),
            ShizukuOptimizationTask(
                id = "funtouch_touch_priority",
                title = "Boost Touch Sampling Rate (240Hz / 360Hz)",
                command = "settings put system game_touch_sampling_high 1",
                targetComponent = "Touch Digitizer Driver",
                explanation = "Activates ultra-low latency touch sampling for faster aim flicks and recoil pull."
            )
        )
    }
}
