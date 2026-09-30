package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.FiberManualRecord
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.FpsSession
import com.example.ui.components.CyberActionButton
import com.example.ui.theme.*
import com.example.viewmodel.GameTurboUiState
import com.example.viewmodel.GameTurboViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun FpsMeterScreen(
    viewModel: GameTurboViewModel,
    uiState: GameTurboUiState,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val fps = uiState.fpsLiveState

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Vivo T4 & iQOO Z10 Header
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(CyberDark)
                    .border(1.dp, NeonLime.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
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
                                text = "HIGH REFRESH RATE FPS ENGINE",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                ),
                                color = NeonLime
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Vivo T4 & iQOO Z10",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
                            color = TextPrimary
                        )
                        Text(
                            text = "120Hz / 144Hz AMOLED Frame Pacing • Lag-Free Recorder",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }

                    // Target Selector Pill
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(NeonLime.copy(alpha = 0.15f))
                            .border(1.dp, NeonLime, RoundedCornerShape(10.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "${fps.targetFps} FPS TARGET",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.Monospace
                            ),
                            color = NeonLime
                        )
                    }
                }
            }
        }

        // Live Real-Time FPS Gauge Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CyberDark),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.horizontalGradient(listOf(NeonCyan, Color.Transparent))
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
                        Text(
                            text = "REAL-TIME FRAME RATE",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.2.sp
                            ),
                            color = NeonCyan
                        )

                        // Target FPS selector buttons
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf(60, 90, 120).forEach { target ->
                                val isSelected = fps.targetFps == target
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (isSelected) NeonCyan else CyberCard)
                                        .clickable { viewModel.setTargetFps(target) }
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "$target",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = FontFamily.Monospace
                                        ),
                                        color = if (isSelected) Color.Black else TextSecondary
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Huge FPS number readout
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = "${fps.currentFps}",
                            style = MaterialTheme.typography.displayLarge.copy(
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 64.sp
                            ),
                            color = if (fps.currentFps >= (fps.targetFps - 4)) NeonLime else WarningYellow
                        )
                        Text(
                            text = "FPS",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = TextSecondary,
                            modifier = Modifier.padding(bottom = 12.dp, start = 6.dp)
                        )
                    }

                    Text(
                        text = if (fps.currentFps >= (fps.targetFps - 4)) "LAG-FREE STABLE • 11.1ms FRAME TIME" else "MINOR STUTTER DETECTED",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        ),
                        color = if (fps.currentFps >= (fps.targetFps - 4)) NeonLime else WarningYellow
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Live Frame History Line Graph
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(70.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(CyberBlack)
                            .padding(8.dp)
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val history = fps.frameHistory
                            if (history.size > 1) {
                                val stepX = size.width / (history.size - 1)
                                val maxVal = fps.targetFps + 5
                                val minVal = (fps.targetFps - 15).coerceAtLeast(30)
                                val range = (maxVal - minVal).toFloat()

                                val path = Path()
                                history.forEachIndexed { i, valFps ->
                                    val x = i * stepX
                                    val normalized = ((valFps - minVal) / range).coerceIn(0f, 1f)
                                    val y = size.height - (normalized * size.height)
                                    if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
                                }

                                drawPath(
                                    path = path,
                                    color = NeonLime,
                                    style = Stroke(width = 2.5.dp.toPx())
                                )
                            }
                        }
                    }
                }
            }
        }

        // In-Game Floating HUD Capsule Preview
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CyberCard),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.horizontalGradient(listOf(MonsterOrange.copy(alpha = 0.5f), Color.Transparent))
                ),
                shape = RoundedCornerShape(16.dp),
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
                                imageVector = if (uiState.showInGameFloatingHud) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = null,
                                tint = MonsterOrange,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "IN-GAME FLOATING HUD OVERLAY",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = MonsterOrange
                                )
                                Text(
                                    text = "Displays live FPS, Ping & RAM over BGMI",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                    color = TextMuted
                                )
                            }
                        }

                        Switch(
                            checked = uiState.showInGameFloatingHud,
                            onCheckedChange = {
                                viewModel.toggleFloatingHud()
                                Toast.makeText(context, if (!uiState.showInGameFloatingHud) "Floating HUD Enabled for BGMI" else "Floating HUD Disabled", Toast.LENGTH_SHORT).show()
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.Black,
                                checkedTrackColor = MonsterOrange
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Floating Capsule Preview Widget
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(30.dp))
                            .background(Color.Black.copy(alpha = 0.85f))
                            .border(1.dp, NeonLime.copy(alpha = 0.6f), RoundedCornerShape(30.dp))
                            .padding(horizontal = 18.dp, vertical = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(7.dp)
                                        .clip(CircleShape)
                                        .background(NeonLime)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "${fps.currentFps} FPS",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Black,
                                        fontFamily = FontFamily.Monospace
                                    ),
                                    color = NeonLime
                                )
                            }

                            Text(
                                text = "${uiState.fastestBgmiPing} ms",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                ),
                                color = NeonCyan
                            )

                            Text(
                                text = "RAM: ${uiState.hardwareState?.ramUsagePercent ?: 48}%",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                ),
                                color = TextPrimary
                            )

                            Text(
                                text = "${uiState.hardwareState?.batteryTempC ?: 33.4}°C",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                ),
                                color = if ((uiState.hardwareState?.batteryTempC ?: 33f) < 38f) NeonLime else DangerRed
                            )
                        }
                    }
                }
            }
        }

        // FPS Session Recording Control
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CyberDark),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.horizontalGradient(listOf(NeonLime.copy(alpha = 0.4f), Color.Transparent))
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "RECORD FPS GAMING SESSION",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        ),
                        color = TextMuted
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    if (fps.isRecording) {
                        // Recording Status Box
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(DangerRed)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "RECORDING ACTIVE (${fps.recordedSeconds}s)",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Black,
                                        fontFamily = FontFamily.Monospace
                                    ),
                                    color = DangerRed
                                )
                            }

                            Text(
                                text = "Avg: ${fps.averageFps} FPS",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                ),
                                color = NeonLime
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        CyberActionButton(
                            text = "Stop Recording & Save Session",
                            icon = Icons.Default.Stop,
                            primaryColor = DangerRed,
                            secondaryColor = Color(0xFF990022),
                            testTag = "stop_fps_record_btn",
                            onClick = {
                                viewModel.stopFpsRecordingAndSave()
                                Toast.makeText(context, "Saved match FPS report to history!", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else {
                        CyberActionButton(
                            text = "Start Recording BGMI Session (${fps.targetFps} FPS)",
                            icon = Icons.Default.FiberManualRecord,
                            primaryColor = NeonLime,
                            secondaryColor = Color(0xFF009955),
                            testTag = "start_fps_record_btn",
                            onClick = {
                                viewModel.startFpsRecording("BGMI (Battlegrounds Mobile India)", fps.targetFps)
                                Toast.makeText(context, "FPS Session recording started! Launch BGMI.", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }

        // Recorded Sessions History List
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "RECORDED MATCH FPS REPORTS (${uiState.recordedFpsSessions.size})",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    ),
                    color = TextMuted
                )

                if (uiState.recordedFpsSessions.isNotEmpty()) {
                    IconButton(
                        onClick = {
                            viewModel.clearAllFpsSessions()
                            Toast.makeText(context, "Cleared FPS session records", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteSweep,
                            contentDescription = "Clear all",
                            tint = TextMuted
                        )
                    }
                }
            }
        }

        if (uiState.recordedFpsSessions.isEmpty()) {
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
                        text = "No FPS sessions recorded yet. Hit 'Start Recording' before playing a match to track frame stability!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }
            }
        } else {
            items(uiState.recordedFpsSessions, key = { it.id }) { session ->
                FpsSessionHistoryCard(session)
            }
        }
    }
}

@Composable
fun FpsSessionHistoryCard(session: FpsSession) {
    val formatter = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())
    val dateStr = formatter.format(Date(session.timestamp))

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(CyberCard)
            .border(1.dp, CyberCardBorder, RoundedCornerShape(14.dp))
            .padding(14.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = session.gameTitle,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimary
                    )
                    Text(
                        text = "${session.targetDevice} • $dateStr",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = TextMuted
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(NeonLime.copy(alpha = 0.2f))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "${session.stabilityScorePercent}% STABLE",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp
                        ),
                        color = NeonLime
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Average FPS", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                    Text(
                        text = "${session.averageFps} / ${session.targetFps}",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace
                        ),
                        color = NeonLime
                    )
                }

                Column {
                    Text("1% Low (Min)", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                    Text(
                        text = "${session.minFps} FPS",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace
                        ),
                        color = if (session.minFps > 75) NeonCyan else WarningYellow
                    )
                }

                Column {
                    Text("Stutter Spikes", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                    Text(
                        text = "${session.frameDropsCount}",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace
                        ),
                        color = if (session.frameDropsCount == 0) NeonLime else DangerRed
                    )
                }

                Column {
                    Text("Duration", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                    Text(
                        text = "${session.durationSeconds}s",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace
                        ),
                        color = TextSecondary
                    )
                }
            }
        }
    }
}
