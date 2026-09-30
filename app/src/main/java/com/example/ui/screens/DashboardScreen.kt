package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.SelectedGameMode
import com.example.ui.components.CircularRamGauge
import com.example.ui.components.CpuGpuLoadMeter
import com.example.ui.components.CyberActionButton
import com.example.ui.components.TelemetryStatPill
import com.example.ui.theme.*
import com.example.viewmodel.GameNavTab
import com.example.viewmodel.GameTurboUiState
import com.example.viewmodel.GameTurboViewModel

@Composable
fun DashboardScreen(
    viewModel: GameTurboViewModel,
    uiState: GameTurboUiState,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val hw = uiState.hardwareState
    val comparison = uiState.metricComparison

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Vivo T4 & Location Header Banner (Raipur Shankar Nagar Context)
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(Color(0xFF0F1829), Color(0xFF16233B))
                        )
                    )
                    .border(1.dp, CyberCardBorder, RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(NeonLime)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "VIVO T4 • MULTI-TURBO ENGINE",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                ),
                                color = NeonLime
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${hw?.manufacturer ?: "VIVO"} ${hw?.model ?: "T4 (Gaming Ed.)"}",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Black,
                                letterSpacing = (-0.5).sp
                            ),
                            color = TextPrimary
                        )

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = MonsterOrange,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = uiState.userLocationCity,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 11.sp
                                ),
                                color = TextSecondary
                            )
                        }
                    }

                    // Active Mode Badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(uiState.selectedGameMode.colorHex).copy(alpha = 0.15f))
                            .border(1.dp, Color(uiState.selectedGameMode.colorHex), RoundedCornerShape(10.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = uiState.selectedGameMode.badge,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp
                            ),
                            color = Color(uiState.selectedGameMode.colorHex)
                        )
                    }
                }
            }
        }

        // Master Control for In-Game Floating Overlay Sidebar
        item {
            com.example.ui.components.OverlaySidebarCard(
                viewModel = viewModel
            )
        }

        // Clean RAM Banner Feedback if present
        if (uiState.ramCleanMessage != null) {
            item {
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(NeonLime.copy(alpha = 0.15f))
                            .border(1.dp, NeonLime, RoundedCornerShape(12.dp))
                            .padding(14.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Bolt,
                                contentDescription = null,
                                tint = NeonLime,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = uiState.ramCleanMessage,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = NeonLime
                            )
                        }
                    }
                }
            }
        }

        // Real-Time Performance Monitoring Dashboard
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CyberDark),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.horizontalGradient(listOf(NeonCyan.copy(alpha = 0.45f), Color.Transparent))
                ),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "REAL-TIME TELEMETRY MONITOR",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.2.sp
                                ),
                                color = NeonCyan
                            )
                            Text(
                                text = "Active System Sensors & Indian Game Nodes",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextMuted
                            )
                        }

                        IconButton(
                            onClick = {
                                viewModel.refreshPing()
                                viewModel.refreshRunningProcesses()
                            },
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(CyberCard)
                                .size(34.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Refresh Telemetry",
                                tint = NeonCyan,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // RAM Load Gauge + BGMI Server Ping
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        CircularRamGauge(
                            percent = hw?.ramUsagePercent ?: 62,
                            usedMb = hw?.usedRamMb ?: 5080,
                            totalMb = hw?.totalRamMb ?: 8192,
                            size = 145.dp
                        )

                        // Live Ping Widget for BGMI
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(CyberCard)
                                .border(1.dp, CyberCardBorder, RoundedCornerShape(16.dp))
                                .padding(16.dp)
                        ) {
                            Text(
                                text = "BGMI MUMBAI (AWS)",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = TextMuted
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(
                                    text = "${uiState.fastestBgmiPing}",
                                    style = MaterialTheme.typography.headlineLarge.copy(
                                        fontWeight = FontWeight.Black,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 38.sp
                                    ),
                                    color = if (uiState.fastestBgmiPing < 35) NeonLime else WarningYellow
                                )
                                Text(
                                    text = "ms",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = TextSecondary,
                                    modifier = Modifier.padding(bottom = 6.dp, start = 2.dp)
                                )
                            }
                            Text(
                                text = "Raipur Route • Jitter ±${uiState.jitterMs}ms",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                                color = TextSecondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // CPU & GPU Load Meters
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        CpuGpuLoadMeter(
                            label = "CPU Load (8 Cores)",
                            percent = hw?.cpuLoadPercent ?: 38,
                            accentColor = if ((hw?.cpuLoadPercent ?: 38) > 65) MonsterOrange else NeonCyan,
                            modifier = Modifier.weight(1f)
                        )

                        CpuGpuLoadMeter(
                            label = "GPU Render Load",
                            percent = hw?.gpuLoadPercent ?: 44,
                            accentColor = if ((hw?.gpuLoadPercent ?: 44) > 70) DangerRed else NeonLime,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Temperature and Network Pills
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        TelemetryStatPill(
                            label = "Battery Temp",
                            value = "${hw?.batteryTempC ?: 33.4} °C",
                            unit = if ((hw?.batteryTempC ?: 33f) < 38f) "Normal" else "Warm",
                            accentColor = if ((hw?.batteryTempC ?: 33f) < 38f) NeonLime else DangerRed,
                            modifier = Modifier.weight(1f)
                        )
                        TelemetryStatPill(
                            label = "Network Mode",
                            value = hw?.networkType ?: "5G SA",
                            unit = if (uiState.networkStabilizerActive) "Stabilized" else "Idle",
                            accentColor = NeonCyan,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // Before vs After Optimization Comparison Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CyberCard),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.horizontalGradient(listOf(MonsterOrange.copy(alpha = 0.5f), Color.Transparent))
                ),
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.TrendingDown,
                                contentDescription = null,
                                tint = NeonLime,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "BEFORE & AFTER OPTIMIZATION",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                ),
                                color = TextPrimary
                            )
                        }

                        if (comparison.isOptimized) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(NeonLime.copy(alpha = 0.2f))
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = "OPTIMIZED",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Black,
                                        fontSize = 9.sp
                                    ),
                                    color = NeonLime
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Comparison Matrix Grid
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        ComparisonRowItem(
                            label = "RAM Utilization",
                            before = "${comparison.ramPercentBefore}%",
                            after = "${comparison.ramPercentAfter}%",
                            improvement = "-${comparison.ramPercentBefore - comparison.ramPercentAfter}% Load"
                        )
                        ComparisonRowItem(
                            label = "BGMI Server Latency",
                            before = "${comparison.pingMsBefore} ms",
                            after = "${comparison.pingMsAfter} ms",
                            improvement = "-${comparison.pingMsBefore - comparison.pingMsAfter} ms Ping"
                        )
                        ComparisonRowItem(
                            label = "CPU Background Load",
                            before = "${comparison.cpuLoadBefore}%",
                            after = "${comparison.cpuLoadAfter}%",
                            improvement = "-${comparison.cpuLoadBefore - comparison.cpuLoadAfter}% Load"
                        )
                        ComparisonRowItem(
                            label = "GPU Render Bottleneck",
                            before = "${comparison.gpuLoadBefore}%",
                            after = "${comparison.gpuLoadAfter}%",
                            improvement = "-${comparison.gpuLoadBefore - comparison.gpuLoadAfter}% Headroom"
                        )
                    }
                }
            }
        }

        // Primary Game Launch & Optimize CTA Buttons
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                CyberActionButton(
                    text = "Launch BGMI with Monster Boost",
                    icon = Icons.Default.PlayArrow,
                    primaryColor = MonsterOrange,
                    secondaryColor = Color(0xFFFF8A00),
                    isLoading = uiState.isCleaningRam,
                    testTag = "launch_bgmi_button",
                    onClick = {
                        viewModel.boostAndLaunchGame(viewModel.gameLauncher.bgmiPackageName) {
                            Toast.makeText(context, "BGMI Turbo Boost Activated! Welcome to Raipur BGMI server.", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    CyberActionButton(
                        text = "Clean RAM Now",
                        icon = Icons.Default.CleaningServices,
                        primaryColor = NeonCyan,
                        secondaryColor = Color(0xFF007799),
                        isLoading = uiState.isCleaningRam,
                        testTag = "clean_ram_quick_button",
                        onClick = {
                            viewModel.cleanRamDeep { freed, procs ->
                                Toast.makeText(context, "Freed ${freed}MB RAM! Closed $procs apps.", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier.weight(1f)
                    )

                    CyberActionButton(
                        text = "Boost Ping",
                        icon = Icons.Default.Wifi,
                        primaryColor = NeonLime,
                        secondaryColor = Color(0xFF009955),
                        isLoading = uiState.isPinging,
                        testTag = "boost_ping_button",
                        onClick = {
                            viewModel.refreshPing()
                            viewModel.benchmarkDns()
                            Toast.makeText(context, "Routing optimized for Raipur to AWS Mumbai!", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Navigation shortcuts to Game Modes and Settings
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(CyberCard)
                        .border(1.dp, CyberCardBorder, RoundedCornerShape(14.dp))
                        .clickable { viewModel.selectTab(GameNavTab.GAME_MODES) }
                        .padding(14.dp)
                ) {
                    Column {
                        Text(
                            text = "GAME MODE SELECTOR",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = MonsterOrange
                        )
                        Text(
                            text = "BGMI • Vivo Lite • Shizuku",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(CyberCard)
                        .border(1.dp, CyberCardBorder, RoundedCornerShape(14.dp))
                        .clickable { viewModel.selectTab(GameNavTab.AI_TACTICAL) }
                        .padding(14.dp)
                ) {
                    Column {
                        Text(
                            text = "AI AIM & ZOOM TACTICS",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = NeonLime
                        )
                        Text(
                            text = "Aim Assist & 6x-to-3x Spray",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ComparisonRowItem(
    label: String,
    before: String,
    after: String,
    improvement: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(CyberDark)
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                color = TextSecondary
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = before,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontFamily = FontFamily.Monospace,
                        color = DangerRed
                    )
                )
                Text(
                    text = " → ",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted
                )
                Text(
                    text = after,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = NeonLime
                    )
                )
                Spacer(modifier = Modifier.width(10.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(NeonLime.copy(alpha = 0.15f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = improvement,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 9.sp,
                            color = NeonLime
                        )
                    )
                }
            }
        }
    }
}
