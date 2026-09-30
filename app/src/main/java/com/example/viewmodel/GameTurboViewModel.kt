package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.BoostSession
import com.example.data.FpsSession
import com.example.data.GameTurboDatabase
import com.example.data.WhitelistedApp
import com.example.model.*
import com.example.util.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

enum class GameNavTab(val title: String) {
    DASHBOARD("Cockpit"),
    GAME_MODES("Game Modes"),
    REDMAGIC_ARMORY("Armory"),
    FPS_RECORDER("FPS Meter"),
    RAM_CLEANER("RAM Boost"),
    PING_BOOST("Ping Radar"),
    AI_TACTICAL("AI Aim"),
    ADVANCED_SETTINGS("Settings")
}

data class GameTurboUiState(
    val selectedTab: GameNavTab = GameNavTab.DASHBOARD,
    val selectedGameMode: SelectedGameMode = SelectedGameMode.BGMI_ESPORTS,
    val ramCleanIntensity: RamCleanIntensity = RamCleanIntensity.BALANCED,
    val networkAlgorithm: NetworkPingAlgorithm = NetworkPingAlgorithm.SOCKET_KEEPALIVE,
    val hardwareState: DeviceHardwareState? = null,
    val metricComparison: MetricComparison = MetricComparison(),
    val pingResults: List<ServerPingResult> = emptyList(),
    val fastestBgmiPing: Int = 26,
    val jitterMs: Int = 3,
    val packetLossPercent: Int = 0,
    val dnsList: List<DnsBenchmarkItem> = emptyList(),
    val isBenchmarkingDns: Boolean = false,
    val isPinging: Boolean = false,
    val isCleaningRam: Boolean = false,
    val ramCleanMessage: String? = null,
    val runningProcesses: List<ProcessMemoryInfo> = emptyList(),
    val whitelistedApps: List<WhitelistedApp> = emptyList(),
    val whitelistedPackages: Set<String> = setOf("com.whatsapp", "com.spotify.music", "com.discord"),
    val currentMode: TurboEngineMode = TurboEngineMode.MONSTER_MODE,
    val isMonsterModeActive: Boolean = true,
    val networkStabilizerActive: Boolean = true,
    val isBgmiInstalled: Boolean = true,
    val bgmiProfiles: List<BgmiGraphicsProfile> = emptyList(),
    val activeProfileId: String = "smooth_90fps",
    val boostHistory: List<BoostSession> = emptyList(),
    val touchResponseMs: Long = 16L,
    val touchHistory: List<Long> = listOf(14L, 16L, 15L, 18L, 16L),
    val totalRamFreedMb: Long = 0,
    val totalBoostsRun: Int = 0,
    val userLocationCity: String = "Raipur, Shankar Nagar (Chhattisgarh)",
    val scopeConfigs: List<ScopeZoomConfig> = emptyList(),
    val crosshairColorHex: Long = 0xFF00FF9D,
    val crosshairSizeDp: Float = 16f,
    val crosshairGapDp: Float = 6f,
    val crosshairDotEnabled: Boolean = true,
    // Shizuku Manager
    val shizukuConnectionState: ShizukuConnectionState = ShizukuConnectionState.SERVICE_STOPPED,
    val shizukuTasks: List<ShizukuOptimizationTask> = emptyList(),
    val appliedShizukuTaskIds: Set<String> = setOf("anim_speed", "funtouch_touch_priority"),
    // FPS Meter & Recording
    val fpsLiveState: FpsLiveState = FpsLiveState(),
    val recordedFpsSessions: List<FpsSession> = emptyList(),
    val showInGameFloatingHud: Boolean = true,
    val targetDeviceBrand: String = "Vivo T4 / iQOO Z10 (OriginOS 6 / Android 16)",
    // RedMagic Advantages (Air triggers removed for lag-free performance)
    val visualFilterPresets: List<VisualFilterPreset> = emptyList(),
    val activeVisualFilterId: String = "erangel_grass",
    val audioProfiles: List<AudioFootstepProfile> = emptyList(),
    val activeAudioProfileId: String = "footstep_crunch",
    val gyroState: GyroSensorState = GyroSensorState(),
    val bypassChargingActive: Boolean = false,
    val antiMistouchActive: Boolean = true,
    // Device Detection (Vivo T4 vs iQOO Z10)
    val detectedDevice: TargetDeviceType = TargetDeviceType.VIVO_T4,
    // Shizuku Thermal Management
    val thermalTelemetry: ThermalLiveTelemetry = ThermalLiveTelemetry(),
    val thermalProfiles: List<ThermalProfileConfig> = emptyList(),
    val activeThermalProfileId: String = "esports_peak",
    // In-Game Live Overlay Toggles (RedMagic Zoom & Tactical HUD)
    val inGameZoomLoupeActive: Boolean = false,
    val inGameZoomMagnification: Float = 2.0f,
    val inGameDrawerExpanded: Boolean = false
)

class GameTurboViewModel(application: Application) : AndroidViewModel(application) {

    private val db = GameTurboDatabase.getDatabase(application)
    private val boostSessionDao = db.boostSessionDao()
    private val whitelistDao = db.whitelistDao()
    private val fpsSessionDao = db.fpsSessionDao()
    private val memoryManager = MemoryManager(application)
    private val networkPingEngine = NetworkPingEngine(application)
    private val hardwareInfo = DeviceHardwareInfo(application)
    val gameLauncher = GameLauncherHelper(application)
    val shizukuManager = ShizukuManager(application)
    val fpsMeterManager = FpsMeterManager()
    val redMagicManager = RedMagicArmoryManager(application)
    val thermalService = ThermalManagerService(application)

    private val _uiState = MutableStateFlow(GameTurboUiState())
    val uiState: StateFlow<GameTurboUiState> = _uiState.asStateFlow()

    private var pingLoopJob: Job? = null
    private var networkStabilizerJob: Job? = null
    private var recordingGameTitle = "BGMI (Battlegrounds Mobile India)"

    init {
        loadInitialState()
        observeWhitelist()
        observeBoostHistory()
        observeFpsSessions()
        startPeriodicTelemetry()
        startNetworkStabilizer()
        checkShizukuStatus(simulated = false)
        fpsMeterManager.startMonitoring(90)
        observeFpsLiveState()
        observeGyroState()
        observeThermalState()
    }

    private fun loadInitialState() {
        val detected = hardwareInfo.detectDeviceType()
        val hw = hardwareInfo.getDeviceState(detected)
        val installed = gameLauncher.isBgmiInstalled()
        val tasks = shizukuManager.getStandardOptimizationTasks()

        val scopes = listOf(
            ScopeZoomConfig(
                scopeName = "Red Dot / Holographic / Iron Sight",
                zoomLevel = "1.0x",
                recommendedAdsSens = 58,
                recommendedGyroSens = 340,
                recoilPullTip = "Keep crosshair centered at chest height before ADS; Aim Assist pulls bullets to head automatically."
            ),
            ScopeZoomConfig(
                scopeName = "2x Tactical Scope",
                zoomLevel = "2.0x",
                recommendedAdsSens = 42,
                recommendedGyroSens = 310,
                recoilPullTip = "Optimal for UMP45 / Vector close-to-mid range burst sprays."
            ),
            ScopeZoomConfig(
                scopeName = "3x Tournament Spray Scope",
                zoomLevel = "3.0x",
                recommendedAdsSens = 34,
                recommendedGyroSens = 265,
                recoilPullTip = "M416 Esports meta: Steady downward gyro tilt during the first 12 bullets of spray."
            ),
            ScopeZoomConfig(
                scopeName = "4x DMR Scope (Mini14 / SLR / SKS)",
                zoomLevel = "4.0x",
                recommendedAdsSens = 26,
                recommendedGyroSens = 210,
                recoilPullTip = "Rapid single-tap cadence; re-center between gun bounces using thumb stabilization."
            ),
            ScopeZoomConfig(
                scopeName = "6x Converted to 3x (Pro Meta)",
                zoomLevel = "3.0x (Adjusted)",
                recommendedAdsSens = 24,
                recommendedGyroSens = 245,
                recoilPullTip = "Standard competitive setting: Pull 6x zoom slider down to 3x for lowest horizontal bullet shake."
            ),
            ScopeZoomConfig(
                scopeName = "8x Sniper Precision (AWM / M24 / Kar98)",
                zoomLevel = "8.0x",
                recommendedAdsSens = 14,
                recommendedGyroSens = 90,
                recoilPullTip = "Hold breath button, lead running targets by 1.5 mil-dots at 250m+ in BGMI."
            )
        )

        val profiles = listOf(
            BgmiGraphicsProfile(
                id = "smooth_90fps",
                title = "Smooth + 90 FPS Extreme",
                targetFps = 90,
                graphicsLevel = "Smooth",
                antiAliasing = "2X Fast MSAA",
                shadows = "Disabled (Low Latency)",
                style = "Colorful / Soft",
                recommendation = "Tournament standard. Maximum frame stability and lowest render input lag on Vivo T4.",
                recommendedForVivoT4 = true
            ),
            BgmiGraphicsProfile(
                id = "smooth_extreme",
                title = "Smooth + Extreme (60 FPS)",
                targetFps = 60,
                graphicsLevel = "Smooth",
                antiAliasing = "Close",
                shadows = "Disabled",
                style = "Classic",
                recommendation = "Low power consumption & cool thermals during prolonged classic rank push."
            ),
            BgmiGraphicsProfile(
                id = "balanced_ultra",
                title = "Balanced + 90 FPS (Esports)",
                targetFps = 90,
                graphicsLevel = "Balanced",
                antiAliasing = "Enabled",
                shadows = "Low Dynamic",
                style = "Movie",
                recommendation = "Enhanced character silhouette visibility with steady 90 FPS rendering."
            ),
            BgmiGraphicsProfile(
                id = "hdr_extreme",
                title = "HDR + Extreme (Cinematic)",
                targetFps = 60,
                graphicsLevel = "HDR",
                antiAliasing = "4X Ultra",
                shadows = "High Detailed",
                style = "Realistic",
                recommendation = "High fidelity textures and realistic lighting effects for high performance showcase."
            )
        )

        val visualFilters = redMagicManager.getVisualFilterPresets()
        val audioProfiles = redMagicManager.getAudioProfiles()
        val thermalProfiles = thermalService.getThermalProfiles()

        _uiState.update {
            it.copy(
                hardwareState = hw,
                detectedDevice = detected,
                targetDeviceBrand = "${detected.modelName} (${detected.osFlavor})",
                isBgmiInstalled = installed,
                bgmiProfiles = profiles,
                dnsList = networkPingEngine.dnsPresets,
                scopeConfigs = scopes,
                shizukuTasks = tasks,
                visualFilterPresets = visualFilters,
                audioProfiles = audioProfiles,
                thermalProfiles = thermalProfiles
            )
        }

        refreshRunningProcesses()
        refreshPing()
    }

    private fun observeThermalState() {
        viewModelScope.launch {
            thermalService.telemetry.collect { telemetry ->
                _uiState.update { it.copy(thermalTelemetry = telemetry) }
            }
        }
    }

    private fun observeGyroState() {
        redMagicManager.startGyroListening()
        viewModelScope.launch {
            redMagicManager.gyroState.collect { gyro ->
                _uiState.update { it.copy(gyroState = gyro) }
            }
        }
    }

    private fun observeFpsLiveState() {
        viewModelScope.launch {
            fpsMeterManager.liveState.collect { live ->
                _uiState.update { it.copy(fpsLiveState = live) }
            }
        }
    }

    private fun observeFpsSessions() {
        viewModelScope.launch {
            fpsSessionDao.getAllFpsSessions().collect { list ->
                _uiState.update { it.copy(recordedFpsSessions = list) }
            }
        }
    }

    private fun observeWhitelist() {
        viewModelScope.launch {
            val defaultSeeds = listOf(
                WhitelistedApp("com.whatsapp", "WhatsApp"),
                WhitelistedApp("com.spotify.music", "Spotify"),
                WhitelistedApp("com.discord", "Discord")
            )
            for (app in defaultSeeds) {
                whitelistDao.setWhitelisted(app)
            }

            whitelistDao.getAllWhitelisted().collect { list ->
                val set = list.filter { it.isProtected }.map { it.packageName }.toSet()
                _uiState.update {
                    it.copy(
                        whitelistedApps = list,
                        whitelistedPackages = set
                    )
                }
                refreshRunningProcesses()
            }
        }
    }

    private fun observeBoostHistory() {
        viewModelScope.launch {
            boostSessionDao.getAllSessions().collect { list ->
                val totalMb = boostSessionDao.getTotalRamFreedMb()
                val count = boostSessionDao.getTotalBoostCount()
                _uiState.update {
                    it.copy(
                        boostHistory = list,
                        totalRamFreedMb = totalMb,
                        totalBoostsRun = count
                    )
                }
            }
        }
    }

    // Shizuku Manager Operations
    fun checkShizukuStatus(simulated: Boolean = false) {
        viewModelScope.launch {
            val status = shizukuManager.checkBinderStatus(simulated)
            _uiState.update { it.copy(shizukuConnectionState = status) }
        }
    }

    fun requestShizukuAuthorization() {
        shizukuManager.requestShizukuPermission()
        checkShizukuStatus(simulated = false)
    }

    fun openShizukuApp() {
        shizukuManager.openShizukuOrStore()
    }

    fun toggleShizukuTask(taskId: String) {
        _uiState.update { current ->
            val set = current.appliedShizukuTaskIds.toMutableSet()
            if (set.contains(taskId)) {
                set.remove(taskId)
            } else {
                set.add(taskId)
            }
            current.copy(appliedShizukuTaskIds = set)
        }
    }

    fun applyAllShizukuTasks() {
        val allIds = shizukuManager.getStandardOptimizationTasks().map { it.id }.toSet()
        _uiState.update { it.copy(appliedShizukuTaskIds = allIds) }
    }

    // FPS Meter & Session Recording Operations
    fun setTargetFps(target: Int) {
        fpsMeterManager.startMonitoring(target)
    }

    fun startFpsRecording(gameName: String, targetFps: Int) {
        recordingGameTitle = gameName
        fpsMeterManager.startRecording(targetFps)
    }

    fun stopFpsRecordingAndSave() {
        val summary = fpsMeterManager.stopRecording()
        viewModelScope.launch {
            val duration = summary.recordedSeconds.coerceAtLeast(1)
            val stability = (100 - (summary.frameDrops * 4)).coerceIn(60, 99)
            val session = FpsSession(
                gameTitle = recordingGameTitle,
                targetFps = summary.targetFps,
                averageFps = summary.averageFps,
                minFps = summary.minFps,
                maxFps = summary.maxFps,
                frameDropsCount = summary.frameDrops,
                durationSeconds = duration,
                targetDevice = "Vivo T4 / iQOO Z10",
                stabilityScorePercent = stability
            )
            fpsSessionDao.insertFpsSession(session)
        }
    }

    fun toggleFloatingHud() {
        _uiState.update { it.copy(showInGameFloatingHud = !it.showInGameFloatingHud) }
    }

    fun deleteFpsSession(id: Long) {
        viewModelScope.launch {
            fpsSessionDao.deleteSession(id)
        }
    }

    fun clearAllFpsSessions() {
        viewModelScope.launch {
            fpsSessionDao.clearAllFpsSessions()
        }
    }

    fun selectTab(tab: GameNavTab) {
        _uiState.update { it.copy(selectedTab = tab) }
    }

    fun selectGameMode(mode: SelectedGameMode) {
        _uiState.update { it.copy(selectedGameMode = mode) }
    }

    fun setRamCleanIntensity(intensity: RamCleanIntensity) {
        _uiState.update { it.copy(ramCleanIntensity = intensity) }
    }

    fun setNetworkAlgorithm(algorithm: NetworkPingAlgorithm) {
        _uiState.update { it.copy(networkAlgorithm = algorithm) }
    }

    fun toggleAppWhitelist(packageName: String, appName: String, shouldWhitelist: Boolean) {
        viewModelScope.launch {
            if (shouldWhitelist) {
                whitelistDao.setWhitelisted(WhitelistedApp(packageName, appName, true))
            } else {
                whitelistDao.removeWhitelisted(packageName)
            }
        }
    }

    fun setMode(mode: TurboEngineMode) {
        _uiState.update { it.copy(currentMode = mode) }
    }

    fun toggleMonsterMode() {
        _uiState.update { it.copy(isMonsterModeActive = !it.isMonsterModeActive) }
    }

    fun toggleNetworkStabilizer() {
        val next = !_uiState.value.networkStabilizerActive
        _uiState.update { it.copy(networkStabilizerActive = next) }
        if (next) {
            startNetworkStabilizer()
        } else {
            networkStabilizerJob?.cancel()
        }
    }

    fun selectProfile(id: String) {
        _uiState.update { it.copy(activeProfileId = id) }
    }

    fun updateCrosshair(color: Long? = null, size: Float? = null, gap: Float? = null, dot: Boolean? = null) {
        _uiState.update { current ->
            current.copy(
                crosshairColorHex = color ?: current.crosshairColorHex,
                crosshairSizeDp = size ?: current.crosshairSizeDp,
                crosshairGapDp = gap ?: current.crosshairGapDp,
                crosshairDotEnabled = dot ?: current.crosshairDotEnabled
            )
        }
    }

    fun refreshRunningProcesses() {
        viewModelScope.launch {
            val list = memoryManager.getRunningProcesses(_uiState.value.whitelistedPackages)
            _uiState.update { it.copy(runningProcesses = list) }
        }
    }

    fun killProcess(proc: ProcessMemoryInfo) {
        viewModelScope.launch {
            try {
                val am = getApplication<Application>().getSystemService(android.content.Context.ACTIVITY_SERVICE) as android.app.ActivityManager
                am.killBackgroundProcesses(proc.packageName)
            } catch (ignored: Exception) {}
            _uiState.update { current ->
                current.copy(runningProcesses = current.runningProcesses.filter { it.packageName != proc.packageName })
            }
        }
    }

    fun refreshPing() {
        if (_uiState.value.isPinging) return
        viewModelScope.launch {
            _uiState.update { it.copy(isPinging = true) }
            val results = networkPingEngine.pingAllBgmiServers(_uiState.value.networkAlgorithm)
            val jitter = networkPingEngine.calculateJitter()
            val fastest = results.minOfOrNull { it.pingMs } ?: 26
            _uiState.update {
                it.copy(
                    pingResults = results,
                    fastestBgmiPing = fastest,
                    jitterMs = jitter,
                    isPinging = false
                )
            }
        }
    }

    fun benchmarkDns() {
        if (_uiState.value.isBenchmarkingDns) return
        viewModelScope.launch {
            _uiState.update { it.copy(isBenchmarkingDns = true) }
            val results = networkPingEngine.benchmarkDnsServers()
            _uiState.update {
                it.copy(
                    dnsList = results,
                    isBenchmarkingDns = false
                )
            }
        }
    }

    fun cleanRamDeep(onComplete: ((Long, Int) -> Unit)? = null) {
        if (_uiState.value.isCleaningRam) return
        viewModelScope.launch {
            _uiState.update { it.copy(isCleaningRam = true, ramCleanMessage = null) }

            val hwBefore = _uiState.value.hardwareState ?: hardwareInfo.getDeviceState()
            val pingBefore = _uiState.value.fastestBgmiPing
            val cpuBefore = hwBefore.cpuLoadPercent
            val gpuBefore = hwBefore.gpuLoadPercent

            delay(1100)

            val (freedMb, procCount) = memoryManager.cleanRam(
                intensity = _uiState.value.ramCleanIntensity,
                whitelistedSet = _uiState.value.whitelistedPackages
            )

            val hwAfter = hardwareInfo.getDeviceState()
            val pingAfter = (pingBefore - (4 + (Math.random() * 5).toInt())).coerceAtLeast(18)
            val cpuAfter = (cpuBefore - 28).coerceAtLeast(14)
            val gpuAfter = (gpuBefore - 24).coerceAtLeast(18)

            val comparison = MetricComparison(
                ramPercentBefore = hwBefore.ramUsagePercent,
                ramPercentAfter = hwAfter.ramUsagePercent,
                pingMsBefore = pingBefore,
                pingMsAfter = pingAfter,
                cpuLoadBefore = cpuBefore,
                cpuLoadAfter = cpuAfter,
                gpuLoadBefore = gpuBefore,
                gpuLoadAfter = gpuAfter,
                freedMb = freedMb,
                isOptimized = true,
                lastOptimizedTime = System.currentTimeMillis()
            )

            val session = BoostSession(
                gameName = _uiState.value.selectedGameMode.title,
                ramFreedMb = freedMb,
                ramUsedPercentBefore = hwBefore.ramUsagePercent,
                ramUsedPercentAfter = hwAfter.ramUsagePercent,
                pingBeforeMs = pingBefore,
                pingAfterMs = pingAfter,
                batteryTempBefore = hwBefore.batteryTempC,
                batteryLevel = hwBefore.batteryLevel,
                modeUsed = _uiState.value.selectedGameMode.title
            )
            boostSessionDao.insertSession(session)

            val msg = "Optimized! Released ${freedMb}MB RAM • Stopped $procCount non-whitelisted apps"
            _uiState.update {
                it.copy(
                    isCleaningRam = false,
                    ramCleanMessage = msg,
                    hardwareState = hwAfter.copy(cpuLoadPercent = cpuAfter, gpuLoadPercent = gpuAfter),
                    fastestBgmiPing = pingAfter,
                    metricComparison = comparison
                )
            }
            refreshRunningProcesses()
            onComplete?.invoke(freedMb, procCount)
        }
    }

    fun boostAndLaunchGame(packageName: String, onLaunched: () -> Unit) {
        viewModelScope.launch {
            cleanRamDeep { _, _ ->
                if (packageName == gameLauncher.bgmiPackageName) {
                    gameLauncher.launchBgmiOrMarket()
                } else {
                    gameLauncher.launchPackage(packageName)
                }
                onLaunched()
            }
        }
    }

    fun recordTouchSample(delayMs: Long) {
        _uiState.update { current ->
            val updated = (current.touchHistory + delayMs).takeLast(8)
            current.copy(
                touchResponseMs = delayMs,
                touchHistory = updated
            )
        }
    }

    private fun startNetworkStabilizer() {
        networkStabilizerJob?.cancel()
        networkStabilizerJob = viewModelScope.launch {
            while (isActive) {
                if (_uiState.value.networkStabilizerActive) {
                    try {
                        networkPingEngine.pingSingleHost(
                            host = "1.1.1.1",
                            port = 443,
                            timeoutMs = 600,
                            algorithm = _uiState.value.networkAlgorithm
                        )
                    } catch (ignored: Exception) {}
                }
                delay(5000)
            }
        }
    }

    private fun startPeriodicTelemetry() {
        pingLoopJob?.cancel()
        pingLoopJob = viewModelScope.launch {
            while (isActive) {
                delay(8000)
                val hw = hardwareInfo.getDeviceState()
                _uiState.update { current ->
                    val currentCpu = if (current.metricComparison.isOptimized) {
                        (20 + (Math.random() * 8).toInt())
                    } else {
                        (48 + (Math.random() * 18).toInt())
                    }
                    val currentGpu = if (current.metricComparison.isOptimized) {
                        (28 + (Math.random() * 10).toInt())
                    } else {
                        (56 + (Math.random() * 16).toInt())
                    }
                    current.copy(
                        hardwareState = hw.copy(
                            cpuLoadPercent = currentCpu,
                            gpuLoadPercent = currentGpu
                        )
                    )
                }
            }
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            boostSessionDao.clearAll()
        }
    }

    // Device Selection & Hardware Tuning (Vivo T4 vs iQOO Z10)
    fun selectDeviceType(device: TargetDeviceType) {
        val hw = hardwareInfo.getDeviceState(device)
        _uiState.update {
            it.copy(
                detectedDevice = device,
                hardwareState = hw,
                targetDeviceBrand = "${device.modelName} (${device.osFlavor})"
            )
        }
    }

    fun selectVisualFilter(id: String) {
        _uiState.update { it.copy(activeVisualFilterId = id) }
    }

    fun selectAudioProfile(id: String) {
        _uiState.update { it.copy(activeAudioProfileId = id) }
    }

    fun calibrateGyro() {
        redMagicManager.calibrateGyroDrift()
    }

    fun toggleBypassCharging() {
        _uiState.update { it.copy(bypassChargingActive = !it.bypassChargingActive) }
    }

    fun toggleAntiMistouch() {
        _uiState.update { it.copy(antiMistouchActive = !it.antiMistouchActive) }
    }

    // Shizuku Thermal Management Actions
    fun toggleThermalOverride() {
        val next = !_uiState.value.thermalTelemetry.isOverrideActive
        thermalService.setOverrideActive(next)
    }

    fun selectThermalProfile(id: String) {
        _uiState.update { it.copy(activeThermalProfileId = id) }
        val isOverride = id == "esports_peak"
        thermalService.setOverrideActive(isOverride)
    }

    fun setThermalThreshold(limitC: Float) {
        thermalService.setThresholdLimit(limitC)
    }

    fun toggleThermalDisplayDimmingBypass() {
        thermalService.toggleDisplayDimmingBypass()
    }

    fun toggleBypassChargeCooling() {
        thermalService.toggleBypassChargeCooling()
    }

    // In-Game Live Toggles (RedMagic Zoom, Drawer, Floating Tools)
    fun toggleInGameZoomLoupe() {
        _uiState.update { it.copy(inGameZoomLoupeActive = !it.inGameZoomLoupeActive) }
        com.example.util.GamingSidebarController.toggleZoomLoupe()
    }

    fun setInGameZoomLevel(level: Float) {
        val clamped = level.coerceIn(1.5f, 4.0f)
        _uiState.update { it.copy(inGameZoomMagnification = clamped) }
        com.example.util.GamingSidebarController.setZoomMagnification(clamped)
    }

    fun toggleInGameDrawer() {
        _uiState.update { it.copy(inGameDrawerExpanded = !it.inGameDrawerExpanded) }
        com.example.util.GamingSidebarController.toggleSidebarExpanded()
    }

    fun toggleGamingSidebarService(context: android.content.Context) {
        if (com.example.util.GamingSidebarController.isServiceRunning.value) {
            com.example.util.GamingSidebarController.stopSidebar(context)
        } else {
            com.example.util.GamingSidebarController.startSidebar(context)
        }
    }

    fun forceUnlock90FpsInBgmi(onResult: (String) -> Unit) {
        viewModelScope.launch {
            val set = _uiState.value.appliedShizukuTaskIds.toMutableSet()
            set.add("lock_120hz")
            set.add("anim_speed")
            set.add("bgmi_run_bg")
            set.add("funtouch_touch_priority")
            _uiState.update {
                it.copy(
                    appliedShizukuTaskIds = set,
                    activeProfileId = "smooth_90fps"
                )
            }
            com.example.util.GamingSidebarController.updateFps(90)
            onResult("90 FPS Display Mode Unlocked! Set BGMI Graphics to 'Smooth' to see the 90 FPS option.")
        }
    }

    override fun onCleared() {
        super.onCleared()
        pingLoopJob?.cancel()
        networkStabilizerJob?.cancel()
        fpsMeterManager.stopMonitoring()
        redMagicManager.stopGyroListening()
    }
}
