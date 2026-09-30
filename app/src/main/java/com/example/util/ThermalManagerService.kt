package com.example.util

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import com.example.model.ThermalLiveTelemetry
import com.example.model.ThermalProfileConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ThermalManagerService(private val context: Context) {

    private val _telemetry = MutableStateFlow(ThermalLiveTelemetry())
    val telemetry: StateFlow<ThermalLiveTelemetry> = _telemetry.asStateFlow()

    fun getBatteryTemperature(): Float {
        val ifilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        val batteryStatus: Intent? = context.registerReceiver(null, ifilter)
        val temp = batteryStatus?.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0) ?: 335
        return if (temp > 0) temp / 10f else 33.5f
    }

    fun updateThermalReading() {
        val currentTemp = getBatteryTemperature()
        val socTemp = currentTemp + 7.5f

        _telemetry.update { current ->
            val history = (current.thermalHistory + currentTemp).takeLast(12)
            val isOverThreshold = currentTemp >= current.thresholdLimitC
            val isEmergency = currentTemp >= current.emergencyAutoCutoffC

            // Emergency auto-cooldown protection: if device reaches 45.0°C, automatically disengage override
            val activeOverride = if (isEmergency) false else current.isOverrideActive

            val throttleLabel = when {
                isEmergency -> "EMERGENCY SAFETY COOLDOWN TRIGGERED"
                isOverThreshold && !activeOverride -> "THERMAL THROTTLING ENGAGED (-25% FPS)"
                activeOverride -> "OVERRIDE ACTIVE (Peak 90-120 FPS Sustained)"
                else -> "NORMAL (Zero Throttling)"
            }

            val healthScore = if (activeOverride) {
                (99 - ((currentTemp - 32f).coerceAtLeast(0f) * 1.5f).toInt()).coerceIn(75, 100)
            } else {
                (85 - ((currentTemp - 32f).coerceAtLeast(0f) * 4f).toInt()).coerceIn(40, 95)
            }

            current.copy(
                batteryTempC = currentTemp,
                estimatedSocTempC = socTemp,
                currentThrottleLevel = throttleLabel,
                isOverrideActive = activeOverride,
                thermalHistory = history,
                sustainedFpsHealthPercent = healthScore
            )
        }
    }

    fun setOverrideActive(active: Boolean) {
        _telemetry.update { current ->
            current.copy(
                isOverrideActive = active,
                currentThrottleLevel = if (active) "OVERRIDE ACTIVE (Peak 90-120 FPS Sustained)" else "STOCK MULTI-TURBO (Default Throttling)"
            )
        }
    }

    fun setThresholdLimit(tempC: Float) {
        _telemetry.update { it.copy(thresholdLimitC = tempC.coerceIn(38f, 44f)) }
    }

    fun toggleDisplayDimmingBypass() {
        _telemetry.update { it.copy(displayDimmingBlocked = !it.displayDimmingBlocked) }
    }

    fun toggleBypassChargeCooling() {
        _telemetry.update { it.copy(bypassChargeCoolingActive = !it.bypassChargeCoolingActive) }
    }

    fun getThermalProfiles(): List<ThermalProfileConfig> {
        return listOf(
            ThermalProfileConfig(
                id = "esports_peak",
                title = "Esports Peak Sustained (Tournaments)",
                thresholdTempC = 42.5f,
                overrideStatusLevel = 0,
                description = "Overrides Android 16 thermal service to Status 0 (NONE). Prevents Vivo T4 from dropping frames during intense Pochinki / Bootcamp smoke battles.",
                sustainedFpsLabel = "90 - 120 FPS Locked",
                colorHex = 0xFFFF5722,
                shizukuCommand = "cmd thermalservice override-status 0 && settings put global vivo_multi_turbo_thermal 1"
            ),
            ThermalProfileConfig(
                id = "balanced_dynamic",
                title = "Balanced Multi-Turbo Dynamic",
                thresholdTempC = 40.0f,
                overrideStatusLevel = 1,
                description = "Stock FuntouchOS / OriginOS thermal regulation. Balances device surface temperature with steady frame pacing.",
                sustainedFpsLabel = "90 FPS Dynamic",
                colorHex = 0xFF00E5FF,
                shizukuCommand = "cmd thermalservice reset"
            ),
            ThermalProfileConfig(
                id = "ice_endurance",
                title = "Ice Endurance Marathon",
                thresholdTempC = 38.0f,
                overrideStatusLevel = 2,
                description = "Keeps device cool to the touch during 4-5 hour rank push sessions. Caps excessive CPU spike heat.",
                sustainedFpsLabel = "60 FPS Ice Cool",
                colorHex = 0xFF00FF9D,
                shizukuCommand = "cmd thermalservice override-status 1"
            )
        )
    }
}
