package com.example.util

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build
import com.example.model.DeviceHardwareState
import com.example.model.TargetDeviceType

class DeviceHardwareInfo(private val context: Context) {

    private val memoryManager = MemoryManager(context)
    private val networkPingEngine = NetworkPingEngine(context)

    fun detectDeviceType(): TargetDeviceType {
        val model = Build.MODEL.uppercase()
        val product = Build.PRODUCT.uppercase()
        val manufacturer = Build.MANUFACTURER.uppercase()
        val brand = Build.BRAND.uppercase()

        val isIqoo = manufacturer.contains("IQOO") ||
                     brand.contains("IQOO") ||
                     model.contains("IQOO") ||
                     model.contains("Z10") ||
                     product.contains("Z10") ||
                     model.startsWith("I23") ||
                     model.startsWith("I24")

        return if (isIqoo) {
            TargetDeviceType.IQOO_Z10
        } else {
            TargetDeviceType.VIVO_T4
        }
    }

    fun getDeviceState(forcedDevice: TargetDeviceType? = null): DeviceHardwareState {
        val (networkType, wifiFreq) = networkPingEngine.getNetworkDetails()
        val (batteryLevel, batteryTempC, batteryHealth) = getBatteryInfo()
        val detected = forcedDevice ?: detectDeviceType()

        val isRealVivoOrIqoo = Build.MANUFACTURER.contains("vivo", ignoreCase = true) ||
                               Build.BRAND.contains("vivo", ignoreCase = true) ||
                               Build.MANUFACTURER.contains("iqoo", ignoreCase = true) ||
                               Build.BRAND.contains("iqoo", ignoreCase = true)

        val displayModel = if (isRealVivoOrIqoo) {
            Build.MODEL
        } else {
            detected.modelName
        }

        val displayManufacturer = if (isRealVivoOrIqoo) Build.MANUFACTURER.uppercase() else detected.brand
        val displayAndroidVer = if (Build.VERSION.SDK_INT >= 36) "Android 16 (Preview / Baklava)" else detected.osFlavor

        return DeviceHardwareState(
            manufacturer = displayManufacturer,
            model = displayModel,
            androidVersion = displayAndroidVer,
            sdkInt = Build.VERSION.SDK_INT,
            totalRamMb = memoryManager.getTotalRamMb(),
            usedRamMb = memoryManager.getUsedRamMb(),
            availableRamMb = memoryManager.getAvailRamMb(),
            ramUsagePercent = memoryManager.getRamUsagePercent(),
            cpuCores = Runtime.getRuntime().availableProcessors(),
            batteryTempC = batteryTempC,
            batteryLevel = batteryLevel,
            batteryHealth = batteryHealth,
            isVivoDevice = true,
            networkType = networkType,
            wifiFrequencyGhz = wifiFreq,
            detectedDevice = detected
        )
    }

    private fun getBatteryInfo(): Triple<Int, Float, String> {
        val ifilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        val batteryStatus: Intent? = context.registerReceiver(null, ifilter)

        val level = batteryStatus?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: 82
        val scale = batteryStatus?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: 100
        val batteryPct = if (level >= 0 && scale > 0) (level * 100 / scale) else 82

        val temp = batteryStatus?.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0) ?: 320
        val tempC = if (temp > 0) (temp / 10f) else 34.5f

        val health = when (batteryStatus?.getIntExtra(BatteryManager.EXTRA_HEALTH, BatteryManager.BATTERY_HEALTH_GOOD)) {
            BatteryManager.BATTERY_HEALTH_GOOD -> "Optimal / Good"
            BatteryManager.BATTERY_HEALTH_OVERHEAT -> "Overheated"
            BatteryManager.BATTERY_HEALTH_DEAD -> "Critical"
            BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE -> "Over Voltage"
            else -> "Healthy"
        }

        return Triple(batteryPct, tempC, health)
    }
}
