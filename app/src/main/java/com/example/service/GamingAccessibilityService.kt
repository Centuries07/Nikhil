package com.example.service

import android.accessibilityservice.AccessibilityService
import android.os.Build
import android.util.DisplayMetrics
import android.view.accessibility.AccessibilityEvent

class GamingAccessibilityService : AccessibilityService() {

    companion object {
        var instance: GamingAccessibilityService? = null
            private set

        fun isConnected(): Boolean = instance != null

        fun zoomIn(scale: Float, centerX: Float? = null, centerY: Float? = null): Boolean {
            val service = instance ?: return false
            return try {
                val controller = service.magnificationController
                val metrics: DisplayMetrics = service.resources.displayMetrics
                val cx = centerX ?: (metrics.widthPixels / 2f)
                val cy = centerY ?: (metrics.heightPixels / 2f)

                controller.setScale(scale, true)
                controller.setCenter(cx, cy, true)
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }

        fun resetZoom(): Boolean {
            val service = instance ?: return false
            return try {
                service.magnificationController.reset(true)
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {}

    override fun onInterrupt() {}

    override fun onDestroy() {
        super.onDestroy()
        if (instance == this) {
            instance = null
        }
    }
}
