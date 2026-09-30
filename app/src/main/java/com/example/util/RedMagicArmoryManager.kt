package com.example.util

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import com.example.model.AudioFootstepProfile
import com.example.model.VisualFilterPreset
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class GyroSensorState(
    val pitch: Float = 0f,
    val roll: Float = 0f,
    val yaw: Float = 0f,
    val isCalibrated: Boolean = true,
    val driftOffset: Float = 0.02f
)

class RedMagicArmoryManager(private val context: Context) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager

    private val _gyroState = MutableStateFlow(GyroSensorState())
    val gyroState: StateFlow<GyroSensorState> = _gyroState.asStateFlow()

    private var gyroSensor: Sensor? = null
    private var accelSensor: Sensor? = null

    init {
        gyroSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
        accelSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    }

    fun startGyroListening() {
        gyroSensor?.let {
            sensorManager?.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
        accelSensor?.let {
            sensorManager?.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    fun stopGyroListening() {
        sensorManager?.unregisterListener(this)
    }

    fun calibrateGyroDrift() {
        _gyroState.update { it.copy(isCalibrated = true, driftOffset = 0.00f) }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) return
        if (event.sensor.type == Sensor.TYPE_GYROSCOPE) {
            _gyroState.update { current ->
                current.copy(
                    pitch = event.values.getOrNull(0) ?: 0f,
                    roll = event.values.getOrNull(1) ?: 0f,
                    yaw = event.values.getOrNull(2) ?: 0f
                )
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    fun getVisualFilterPresets(): List<VisualFilterPreset> {
        return listOf(
            VisualFilterPreset(
                id = "erangel_grass",
                title = "Eagle Eye • Grass Snake Spotter",
                description = "Enhances yellow/dark player silhouette contrast against dense Erangel wheat & grass fields.",
                colorAccentHex = 0xFF00FF9D,
                gammaMultiplier = 1.25f,
                contrastBoost = 1.35f,
                targetMap = "Erangel / Sanhok"
            ),
            VisualFilterPreset(
                id = "sanhok_rain_fog",
                title = "Sanhok Jungle & Fog Piercer",
                description = "Cuts down dense rain glare and boosts deep shadow clarity inside Paradise Resort and Ruins.",
                colorAccentHex = 0xFF00E5FF,
                gammaMultiplier = 1.35f,
                contrastBoost = 1.20f,
                targetMap = "Sanhok Bootcamp"
            ),
            VisualFilterPreset(
                id = "miramar_heat_shield",
                title = "Miramar Anti-Glare Ridge Spotter",
                description = "Dampens blinding sand brightness to spot snipers peeking behind 400m ridge lines.",
                colorAccentHex = 0xFFFFD166,
                gammaMultiplier = 0.95f,
                contrastBoost = 1.45f,
                targetMap = "Miramar Desert"
            ),
            VisualFilterPreset(
                id = "night_vision_high_contrast",
                title = "Cyber Dark / Night Tactical Vision",
                description = "Amplifies dark building interiors (Pochinki 3-story & Georgopol warehouses) for instant threat ID.",
                colorAccentHex = 0xFFFF5722,
                gammaMultiplier = 1.50f,
                contrastBoost = 1.50f,
                targetMap = "All Maps / CQC"
            )
        )
    }

    fun getAudioProfiles(): List<AudioFootstepProfile> {
        return listOf(
            AudioFootstepProfile(
                id = "footstep_crunch",
                title = "Footstep Crunch Radar (+12 dB)",
                description = "Boosts 250Hz - 2,000Hz spectrum where sneaker and grass walking audio lives.",
                footstepBoostDb = 12,
                gunshotDampenDb = -6,
                vehicleRadarHz = "120Hz Low Rumble"
            ),
            AudioFootstepProfile(
                id = "esports_tournament_flat",
                title = "PMGC Esports Tournament Flat",
                description = "Precise 3D spatial directional panning for pinpointing enemy grenade pins and crawling.",
                footstepBoostDb = 8,
                gunshotDampenDb = -3,
                vehicleRadarHz = "Balanced Directional"
            ),
            AudioFootstepProfile(
                id = "gunshot_ear_protection",
                title = "High-Caliber Gunshot Dampener",
                description = "Suppresses deafening AWM/M249 high-treble spikes while keeping quiet footstep volume audible.",
                footstepBoostDb = 10,
                gunshotDampenDb = -14,
                vehicleRadarHz = "Suppressed"
            )
        )
    }
}
