package com.example.model

data class GameServer(
    val name: String,
    val location: String,
    val host: String,
    val port: Int = 443,
    val isPrimaryBgmi: Boolean = false,
    val distanceKmFromRaipur: Int = 950
)

data class ServerPingResult(
    val server: GameServer,
    val pingMs: Int,
    val status: PingQuality,
    val isFastest: Boolean = false
)

enum class PingQuality(val label: String, val colorHex: Long) {
    ULTRA_SMOOTH("< 30ms • Competitive", 0xFF00FF9D),
    GOOD("30-55ms • Stable", 0xFF00E5FF),
    FAIR("55-80ms • Playable", 0xFFFFD166),
    HIGH_LATENCY("> 80ms • Laggy", 0xFFFF4560)
}

data class DnsBenchmarkItem(
    val name: String,
    val provider: String,
    val primaryIp: String,
    val privateDnsHostname: String,
    val pingMs: Int? = null,
    val isFastest: Boolean = false,
    val description: String
)

data class BgmiGraphicsProfile(
    val id: String,
    val title: String,
    val targetFps: Int,
    val graphicsLevel: String,
    val antiAliasing: String,
    val shadows: String,
    val style: String,
    val recommendation: String,
    val recommendedForVivoT4: Boolean = false
)

data class ProcessMemoryInfo(
    val packageName: String,
    val appName: String,
    val memoryUsageMb: Int,
    val isCleanable: Boolean,
    val isWhitelisted: Boolean = false
)

enum class TargetDeviceType(
    val brand: String,
    val modelName: String,
    val chipset: String,
    val refreshRateHz: Int,
    val touchSamplingHz: Int,
    val coolingTech: String,
    val osFlavor: String
) {
    VIVO_T4(
        brand = "VIVO",
        modelName = "Vivo T4 5G",
        chipset = "Qualcomm Snapdragon 7s Gen 3",
        refreshRateHz = 120,
        touchSamplingHz = 240,
        coolingTech = "VC Liquid Cooling Architecture",
        osFlavor = "FuntouchOS 16 / Android 16"
    ),
    IQOO_Z10(
        brand = "iQOO",
        modelName = "iQOO Z10",
        chipset = "MediaTek Dimensity 7300 Turbo / SD 7 Gen 3",
        refreshRateHz = 144,
        touchSamplingHz = 360,
        coolingTech = "Graphite 3D Halo Cooling System",
        osFlavor = "Monster OriginOS 6 / Android 16"
    )
}

data class DeviceHardwareState(
    val manufacturer: String,
    val model: String,
    val androidVersion: String,
    val sdkInt: Int,
    val totalRamMb: Long,
    val usedRamMb: Long,
    val availableRamMb: Long,
    val ramUsagePercent: Int,
    val cpuCores: Int,
    val cpuLoadPercent: Int = 42,
    val gpuLoadPercent: Int = 54,
    val batteryTempC: Float,
    val batteryLevel: Int,
    val batteryHealth: String,
    val isVivoDevice: Boolean,
    val networkType: String,
    val wifiFrequencyGhz: Float?,
    val detectedDevice: TargetDeviceType = TargetDeviceType.VIVO_T4
)

data class MetricComparison(
    val ramPercentBefore: Int = 74,
    val ramPercentAfter: Int = 48,
    val pingMsBefore: Int = 58,
    val pingMsAfter: Int = 26,
    val cpuLoadBefore: Int = 68,
    val cpuLoadAfter: Int = 24,
    val gpuLoadBefore: Int = 72,
    val gpuLoadAfter: Int = 38,
    val freedMb: Long = 1380,
    val isOptimized: Boolean = false,
    val lastOptimizedTime: Long = 0L
)

enum class SelectedGameMode(
    val title: String,
    val subtitle: String,
    val iconName: String,
    val badge: String,
    val colorHex: Long
) {
    BGMI_ESPORTS(
        title = "BGMI Esports Turbo",
        subtitle = "Tailored for Battlegrounds Mobile India • Mumbai AWS priority • 90 FPS lock",
        iconName = "sports_esports",
        badge = "BGMI DEDICATED",
        colorHex = 0xFFFF5722
    ),
    GENERAL_PERFORMANCE(
        title = "Universal Device Boost",
        subtitle = "General multitasking • System-wide cache trim • Battery balanced",
        iconName = "speed",
        badge = "ALL APPS",
        colorHex = 0xFF00E5FF
    ),
    VIVO_LITE_SHIZUKU(
        title = "Vivo T4 Lite & Shizuku OS",
        subtitle = "Deep FuntouchOS debloat • Shizuku non-root system bridge • Zero background lag",
        iconName = "developer_board",
        badge = "SHIZUKU READY",
        colorHex = 0xFF00FF9D
    )
}

enum class RamCleanIntensity(
    val title: String,
    val description: String,
    val estimatedFreedRange: String
) {
    GENTLE(
        title = "Gentle Cache Flush",
        description = "Clears app cache descriptors and runtime temp buffers. Leaves background apps alive.",
        estimatedFreedRange = "300 - 600 MB"
    ),
    BALANCED(
        title = "Balanced Optimization (Default)",
        description = "Stops inactive background services and clears cached app processes.",
        estimatedFreedRange = "600 - 1200 MB"
    ),
    AGGRESSIVE(
        title = "Aggressive Gaming Purge",
        description = "Terminates all non-whitelisted apps and triggers deep Java VM heap garbage compaction.",
        estimatedFreedRange = "1200 - 2200 MB"
    ),
    EXTREME_MONSTER(
        title = "Extreme Monster Mode (Vivo T4 / iQOO Z10)",
        description = "Maximum memory eviction, stops broadcast listeners, locks high priority zRAM for BGMI.",
        estimatedFreedRange = "1800 - 3200 MB"
    )
}

enum class NetworkPingAlgorithm(
    val title: String,
    val description: String,
    val protocol: String
) {
    SOCKET_KEEPALIVE(
        title = "Anti-Dormancy Socket Pulse",
        description = "Sends 0-byte keepalive pulses every 4s to prevent 5G/LTE radio from entering sleep mode.",
        protocol = "TCP / RRC Active"
    ),
    ANYCAST_DNS(
        title = "Anycast Gaming DNS Switcher",
        description = "Bypasses slow ISP resolver hops (Jio/Airtel) directly to low-jitter Mumbai Anycast nodes.",
        protocol = "DoT / DoH Anycast"
    ),
    TCP_NODELAY(
        title = "High-Priority TCP NoDelay Emulation",
        description = "Disables Nagle packet buffering algorithm for instant bullet-hit packet transmission.",
        protocol = "Low-Latency Socket"
    )
}

data class ScopeZoomConfig(
    val scopeName: String,
    val zoomLevel: String,
    val recommendedAdsSens: Int,
    val recommendedGyroSens: Int,
    val recoilPullTip: String
)

enum class TurboEngineMode(
    val title: String,
    val subtitle: String,
    val colorHex: Long,
    val targetFps: String,
    val cpuGovernor: String
) {
    MONSTER_MODE(
        title = "Monster Turbo Mode",
        subtitle = "Peak CPU/GPU lock • 120/144 FPS Max • Zero Throttling",
        colorHex = 0xFFFF5722,
        targetFps = "90 - 120 FPS",
        cpuGovernor = "Performance Max"
    ),
    BALANCED_GAME(
        title = "Esports Balanced",
        subtitle = "Thermal regulated • 90 FPS Ultra • Constant Ping",
        colorHex = 0xFF00E5FF,
        targetFps = "90 FPS",
        cpuGovernor = "Schedutil Balanced"
    ),
    STAMINA_GAME(
        title = "Stamina Mode",
        subtitle = "Cool thermals • 60 FPS • Extended battery",
        colorHex = 0xFF00FF9D,
        targetFps = "60 FPS",
        cpuGovernor = "Power Save / Efficiency"
    )
}

// Visual & Audio Pro Arsenal (Air triggers removed as requested for lag-free performance)
data class VisualFilterPreset(
    val id: String,
    val title: String,
    val description: String,
    val colorAccentHex: Long,
    val gammaMultiplier: Float,
    val contrastBoost: Float,
    val targetMap: String
)

data class AudioFootstepProfile(
    val id: String,
    val title: String,
    val description: String,
    val footstepBoostDb: Int,
    val gunshotDampenDb: Int,
    val vehicleRadarHz: String
)

// Shizuku Thermal Management Models
data class ThermalProfileConfig(
    val id: String,
    val title: String,
    val thresholdTempC: Float,
    val overrideStatusLevel: Int,
    val description: String,
    val sustainedFpsLabel: String,
    val colorHex: Long,
    val shizukuCommand: String
)

data class ThermalLiveTelemetry(
    val batteryTempC: Float = 33.5f,
    val estimatedSocTempC: Float = 41.2f,
    val currentThrottleLevel: String = "NONE (Peak 90-120 FPS Active)",
    val thermalStatusInt: Int = 0,
    val isOverrideActive: Boolean = true,
    val thresholdLimitC: Float = 42.5f,
    val emergencyAutoCutoffC: Float = 45.0f,
    val thermalHistory: List<Float> = listOf(32.8f, 33.1f, 33.3f, 33.5f, 33.4f, 33.5f),
    val sustainedFpsHealthPercent: Int = 98,
    val displayDimmingBlocked: Boolean = true,
    val bypassChargeCoolingActive: Boolean = true
)
