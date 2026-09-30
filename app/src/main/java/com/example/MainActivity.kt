package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.*
import com.example.ui.theme.*
import com.example.viewmodel.GameNavTab
import com.example.viewmodel.GameTurboViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                GameTurboApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameTurboApp(
    viewModel: GameTurboViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showInfoDialog by remember { mutableStateOf(false) }

    // Back handling: If on secondary tab, back button returns to Dashboard
    BackHandler(enabled = uiState.selectedTab != GameNavTab.DASHBOARD) {
        viewModel.selectTab(GameNavTab.DASHBOARD)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = CyberBlack,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(
                                    Brush.linearGradient(listOf(DangerRed, MonsterOrange))
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Bolt,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "GAMETURBO BGMI",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 1.sp
                                ),
                                color = TextPrimary
                            )
                            Text(
                                text = "RedMagic Armory • Vivo T4 / iQOO Z10",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                color = TextMuted
                            )
                        }
                    }
                },
                actions = {
                    // Live FPS Badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(CyberCard)
                            .border(1.dp, CyberCardBorder, RoundedCornerShape(20.dp))
                            .padding(horizontal = 7.dp, vertical = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(5.dp)
                                    .clip(CircleShape)
                                    .background(NeonLime)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${uiState.fpsLiveState.currentFps} FPS",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                ),
                                color = NeonLime
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    // Live Ping badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(CyberCard)
                            .border(1.dp, CyberCardBorder, RoundedCornerShape(20.dp))
                            .padding(horizontal = 7.dp, vertical = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(5.dp)
                                    .clip(CircleShape)
                                    .background(if (uiState.fastestBgmiPing < 35) NeonLime else WarningYellow)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${uiState.fastestBgmiPing}ms",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                ),
                                color = if (uiState.fastestBgmiPing < 35) NeonLime else WarningYellow
                            )
                        }
                    }

                    IconButton(
                        onClick = { showInfoDialog = true },
                        modifier = Modifier.testTag("info_help_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Device & App Info",
                            tint = TextSecondary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = CyberDark
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = CyberDark,
                tonalElevation = 8.dp,
                windowInsets = WindowInsets.navigationBars
            ) {
                val tabs = listOf(
                    Triple(GameNavTab.DASHBOARD, "Cockpit", Icons.Default.Bolt),
                    Triple(GameNavTab.GAME_MODES, "Modes", Icons.Default.SportsEsports),
                    Triple(GameNavTab.REDMAGIC_ARMORY, "Armory", Icons.Default.Shield),
                    Triple(GameNavTab.FPS_RECORDER, "FPS", Icons.Default.Speed),
                    Triple(GameNavTab.RAM_CLEANER, "RAM", Icons.Default.Memory),
                    Triple(GameNavTab.PING_BOOST, "Ping", Icons.Default.Wifi),
                    Triple(GameNavTab.AI_TACTICAL, "Aim", Icons.Default.AutoAwesome),
                    Triple(GameNavTab.ADVANCED_SETTINGS, "Setup", Icons.Default.Tune)
                )

                tabs.forEach { (tab, label, icon) ->
                    val isSelected = uiState.selectedTab == tab
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { viewModel.selectTab(tab) },
                        icon = {
                            Icon(
                                imageVector = icon,
                                contentDescription = label,
                                modifier = Modifier.size(18.dp)
                            )
                        },
                        label = {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 8.5.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = if (tab == GameNavTab.REDMAGIC_ARMORY) Color.White else Color.Black,
                            selectedTextColor = if (tab == GameNavTab.REDMAGIC_ARMORY) DangerRed else NeonCyan,
                            unselectedIconColor = TextMuted,
                            unselectedTextColor = TextMuted,
                            indicatorColor = if (tab == GameNavTab.REDMAGIC_ARMORY) DangerRed else NeonCyan
                        ),
                        modifier = Modifier.testTag("nav_tab_${label.lowercase()}")
                    )
                }
            }
        }
    ) { innerPadding ->
        val isZoomActive by com.example.util.GamingSidebarController.isZoomLoupeActive.collectAsStateWithLifecycle()
        val zoomScale by com.example.util.GamingSidebarController.zoomMagnification.collectAsStateWithLifecycle()
        val animatedScale by androidx.compose.animation.core.animateFloatAsState(
            targetValue = if (isZoomActive || uiState.inGameZoomLoupeActive) zoomScale else 1.0f,
            animationSpec = androidx.compose.animation.core.spring(
                dampingRatio = androidx.compose.animation.core.Spring.DampingRatioMediumBouncy,
                stiffness = androidx.compose.animation.core.Spring.StiffnessMediumLow
            ),
            label = "AutomaticScreenZoom"
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(CyberBlack)
        ) {
            // Main content area that automatically zooms when the Zoom button is tapped!
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        scaleX = animatedScale
                        scaleY = animatedScale
                    }
            ) {
                when (uiState.selectedTab) {
                    GameNavTab.DASHBOARD -> DashboardScreen(
                        viewModel = viewModel,
                        uiState = uiState
                    )
                    GameNavTab.GAME_MODES -> GameModeScreen(
                        viewModel = viewModel,
                        uiState = uiState
                    )
                    GameNavTab.REDMAGIC_ARMORY -> RedMagicArmoryScreen(
                        viewModel = viewModel,
                        uiState = uiState
                    )
                    GameNavTab.FPS_RECORDER -> FpsMeterScreen(
                        viewModel = viewModel,
                        uiState = uiState
                    )
                    GameNavTab.RAM_CLEANER -> RamCleanerScreen(
                        viewModel = viewModel,
                        uiState = uiState
                    )
                    GameNavTab.PING_BOOST -> NetworkPingScreen(
                        viewModel = viewModel,
                        uiState = uiState
                    )
                    GameNavTab.AI_TACTICAL -> AiTacticalScreen(
                        viewModel = viewModel,
                        uiState = uiState
                    )
                    GameNavTab.ADVANCED_SETTINGS -> AdvancedSettingsScreen(
                        viewModel = viewModel,
                        uiState = uiState
                    )
                }
            }

            // In-Game Tactical Gaming Sidebar (Overlayable Tab & Retractable Dock, remains unscaled at 1.0x)
            com.example.ui.components.GamingSidebarOverlayView(
                viewModel = viewModel,
                uiState = uiState
            )
        }
    }

    if (showInfoDialog) {
        AlertDialog(
            onDismissRequest = { showInfoDialog = false },
            title = {
                Text(
                    text = "Vivo T4 & iQOO Z10 RedMagic Armory",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = TextPrimary
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Esports gaming enhancements tailored for Vivo T4 and iQOO Z10 on Android 16:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                    Text(
                        text = "• RedMagic Armory: Virtual L1/R1 capacitive shoulder air triggers with haptic recoil, Eagle Eye grass snake spotter display filters, and 250Hz-2kHz footstep sound radar.\n• Gyro Drift Corrector: Hardware accelerometer/gyroscope zero-drift calibration.\n• Bypass Charging: Eliminates battery heating by powering SoC directly.\n• Real-Time FPS: 60/90/120 FPS targets, live frame time graph, and match session recorder in local Room DB.\n• Shizuku Manager: 0.5x window animations and Android 16 Doze mode whitelist.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted,
                        lineHeight = 18.sp
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { showInfoDialog = false }
                ) {
                    Text("Got It", color = NeonCyan)
                }
            },
            containerColor = CyberDark
        )
    }
}
