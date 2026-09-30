package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ProcessMemoryInfo
import com.example.ui.components.CircularRamGauge
import com.example.ui.components.CyberActionButton
import com.example.ui.theme.*
import com.example.viewmodel.GameTurboUiState
import com.example.viewmodel.GameTurboViewModel

@Composable
fun RamCleanerScreen(
    viewModel: GameTurboViewModel,
    uiState: GameTurboUiState,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val hw = uiState.hardwareState

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // RAM Status Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CyberDark),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.Brush.linearGradient(listOf(NeonCyan, Color.Transparent))),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "VIVO T4 MEMORY ACCELERATOR",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp
                        ),
                        color = NeonCyan
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    CircularRamGauge(
                        percent = hw?.ramUsagePercent ?: 68,
                        usedMb = hw?.usedRamMb ?: 5240,
                        totalMb = hw?.totalRamMb ?: 8192,
                        size = 170.dp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "AVAILABLE FOR BGMI",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextMuted
                            )
                            Text(
                                text = "${hw?.availableRamMb ?: 2950} MB",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    fontFamily = FontFamily.Monospace
                                ),
                                color = NeonLime
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "TOTAL LIFETIME FREED",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextMuted
                            )
                            Text(
                                text = "${uiState.totalRamFreedMb} MB",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    fontFamily = FontFamily.Monospace
                                ),
                                color = NeonCyan
                            )
                        }
                    }
                }
            }
        }

        // Clean RAM CTA
        item {
            CyberActionButton(
                text = "Deep Clean RAM & Kill Background Lag",
                icon = Icons.Default.Memory,
                primaryColor = NeonCyan,
                secondaryColor = Color(0xFF007799),
                isLoading = uiState.isCleaningRam,
                testTag = "deep_clean_ram_button",
                onClick = {
                    viewModel.cleanRamDeep { freed, procs ->
                        Toast.makeText(context, "Released ${freed}MB RAM! Closed $procs dormant background tasks.", Toast.LENGTH_LONG).show()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Process List Section Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "BACKGROUND PROCESSES & CACHES (${uiState.runningProcesses.size})",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    ),
                    color = TextMuted
                )

                IconButton(
                    onClick = { viewModel.refreshRunningProcesses() },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh processes",
                        tint = NeonCyan
                    )
                }
            }
        }

        // Process items
        if (uiState.runningProcesses.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(CyberCard)
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Memory is clean! No memory-hungry background tasks active.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }
            }
        } else {
            items(uiState.runningProcesses, key = { it.packageName }) { proc ->
                ProcessItemRow(
                    process = proc,
                    onKill = {
                        viewModel.killProcess(proc)
                        Toast.makeText(context, "Stopped ${proc.appName}", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }

        // Gaming Memory Tips for Vivo T4
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CyberCard),
                shape = RoundedCornerShape(14.dp),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.Brush.horizontalGradient(listOf(CyberCardBorder, Color.Transparent))),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Speed,
                            contentDescription = null,
                            tint = WarningYellow,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "VIVO T4 MEMORY OPTIMIZATION TIP",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = WarningYellow
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "In Android 16 / FuntouchOS 16, RAM Expansion (Extended RAM) reserves up to 8GB of internal flash storage. For competitive BGMI at 90 FPS, real LPDDR5X RAM speed is faster than swap memory. This tool cleans real physical RAM so BGMI never has to use swap page files.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}

@Composable
fun ProcessItemRow(
    process: ProcessMemoryInfo,
    onKill: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(CyberCard)
            .border(1.dp, CyberCardBorder, RoundedCornerShape(12.dp))
            .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = process.appName,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = TextPrimary
                )
                Text(
                    text = process.packageName,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp
                    ),
                    color = TextMuted,
                    maxLines = 1
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(CyberDark)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "${process.memoryUsageMb} MB",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace
                        ),
                        color = NeonCyan
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                IconButton(
                    onClick = onKill,
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(DangerRed.copy(alpha = 0.15f))
                        .testTag("kill_process_${process.packageName.replace('.', '_')}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Stop process",
                        tint = DangerRed,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
