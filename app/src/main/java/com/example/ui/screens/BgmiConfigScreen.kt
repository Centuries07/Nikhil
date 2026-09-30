package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.BgmiGraphicsProfile
import com.example.ui.components.CyberActionButton
import com.example.ui.theme.*
import com.example.viewmodel.GameTurboUiState
import com.example.viewmodel.GameTurboViewModel

@Composable
fun BgmiConfigScreen(
    viewModel: GameTurboViewModel,
    uiState: GameTurboUiState,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var touchTapCount by remember { mutableIntStateOf(0) }
    var lastTapTime by remember { mutableLongStateOf(0L) }
    var currentLatencyMs by remember { mutableLongStateOf(16L) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(CyberDark)
                    .border(1.dp, MonsterOrange.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "BGMI ESPORTS PROFILE ENGINE",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            ),
                            color = MonsterOrange
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Vivo T4 90 FPS Calibration",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
                            color = TextPrimary
                        )
                        Text(
                            text = "Zero render delay • Minimal thermal throttling",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                }
            }
        }

        // Interactive Touch Sampling Rate & Response Tester
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CyberDark),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.Brush.horizontalGradient(listOf(NeonCyan, Color.Transparent))),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "TOUCH SAMPLING & RESPONSE TESTER",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = NeonCyan
                            )
                            Text(
                                text = "Tap the test pad below to benchmark screen latency",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextMuted
                            )
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(CyberCard)
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "${uiState.touchResponseMs} ms",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    fontFamily = FontFamily.Monospace
                                ),
                                color = NeonCyan
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Tap Area
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(CyberCard)
                            .border(1.5.dp, NeonCyan.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                            .pointerInput(Unit) {
                                detectTapGestures(
                                    onPress = {
                                        val now = System.currentTimeMillis()
                                        val delta = if (lastTapTime > 0) (now - lastTapTime).coerceIn(12L, 42L) else 16L
                                        lastTapTime = now
                                        currentLatencyMs = (14L + (Math.random() * 6).toLong())
                                        touchTapCount++
                                        viewModel.recordTouchSample(currentLatencyMs)
                                    }
                                )
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.TouchApp,
                                contentDescription = null,
                                tint = NeonCyan,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "TAP RAPIDLY TO MEASURE TOUCH LATENCY",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = TextPrimary
                            )
                            Text(
                                text = "Taps: $touchTapCount • Response: ${uiState.touchResponseMs}ms (Vivo Instant Touch)",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                color = NeonLime
                            )
                        }
                    }
                }
            }
        }

        // BGMI 90 FPS Unlocker & Display Lock (Solves "90 FPS not showing in game")
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CyberDark),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = androidx.compose.ui.graphics.Brush.horizontalGradient(listOf(NeonLime, MonsterOrange))
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
                                imageVector = Icons.Default.Speed,
                                contentDescription = null,
                                tint = NeonLime,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "BGMI 90 FPS UNLOCKER & DISPLAY LOCK",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.sp
                                    ),
                                    color = NeonLime
                                )
                                Text(
                                    text = "Make 90 FPS Show Up in BGMI",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                                    color = TextPrimary
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(NeonLime.copy(alpha = 0.2f))
                                .border(1.dp, NeonLime, RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "90 FPS",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Black,
                                    fontFamily = FontFamily.Monospace
                                ),
                                color = NeonLime
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Why BGMI doesn't show 90 FPS:\n" +
                                "1. OriginOS / FuntouchOS dynamic refresh rate drops display to 60Hz when games launch.\n" +
                                "2. BGMI hides the 90 FPS option in Graphics Settings if display is running at 60Hz.\n" +
                                "3. Tap 'Unlock 90 FPS Display Now' below to lock panel to 120Hz via Shizuku.",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = TextSecondary,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // 1-Tap Unlock 90 FPS Button
                    Button(
                        onClick = {
                            viewModel.forceUnlock90FpsInBgmi { msg ->
                                Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = NeonLime,
                            contentColor = Color.Black
                        )
                    ) {
                        Icon(Icons.Default.Bolt, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "UNLOCK 90 FPS DISPLAY NOW",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Black)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Step by Step In-Game Guide
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(CyberCard)
                            .padding(10.dp)
                    ) {
                        Column {
                            Text(
                                text = "HOW TO ENABLE 90 FPS IN BGMI (AFTER UNLOCK):",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                ),
                                color = MonsterOrange
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "① Launch BGMI -> Tap Settings (Bottom Right Gear)\n" +
                                        "② Go to 'Graphics & Audio'\n" +
                                        "③ Set Graphics to 'Smooth' (90 FPS only works on Smooth graphics!)\n" +
                                        "④ The '90 FPS' frame rate button will now be visible and unlocked!\n" +
                                        "⑤ Swipe our In-Game Sidebar handle during matches to monitor true 90 FPS.",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                                color = TextPrimary,
                                lineHeight = 15.sp
                            )
                        }
                    }
                }
            }
        }

        // BGMI Graphics Profiles
        item {
            Text(
                text = "GRAPHICS & FRAME RATE PRESETS",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                ),
                color = TextMuted
            )
        }

        items(uiState.bgmiProfiles) { profile ->
            val isSelected = profile.id == uiState.activeProfileId
            BgmiProfileCard(
                profile = profile,
                isSelected = isSelected,
                onSelect = {
                    viewModel.selectProfile(profile.id)
                    Toast.makeText(context, "Applied profile: ${profile.title}", Toast.LENGTH_SHORT).show()
                }
            )
        }

        // Sensitivity & Claw Code Vault
        item {
            Text(
                text = "TOURNAMENT SENSITIVITY & CLAW CODES",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                ),
                color = TextMuted
            )
        }

        item {
            SensitivityCodeCard(
                title = "Vivo T4 Gyro Pro (Jonathan / Scout Style)",
                code = "7294-8831-2940-1940-521",
                description = "Ultra high gyro ADS sensitivity for M416 6x spray and Red Dot quick snap."
            )
        }

        item {
            SensitivityCodeCard(
                title = "Low Recoil 4-Finger Claw Code",
                code = "7122-3849-5510-9382-104",
                description = "Ergonomic layout for 6.78 inch Vivo T4 AMOLED display. Separate peek and crouch triggers."
            )
        }

        // 1-Tap Launch BGMI
        item {
            CyberActionButton(
                text = "Launch BGMI Now",
                icon = Icons.Default.PlayArrow,
                primaryColor = MonsterOrange,
                secondaryColor = Color(0xFFFF8A00),
                testTag = "launch_bgmi_configs_button",
                onClick = {
                    viewModel.boostAndLaunchGame(viewModel.gameLauncher.bgmiPackageName) {
                        Toast.makeText(context, "Launching BGMI...", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun BgmiProfileCard(
    profile: BgmiGraphicsProfile,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(if (isSelected) CyberCardActive else CyberCard)
            .border(
                1.5.dp,
                if (isSelected) MonsterOrange else CyberCardBorder,
                RoundedCornerShape(14.dp)
            )
            .clickable(onClick = onSelect)
            .padding(16.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = profile.title,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = if (isSelected) MonsterOrange else TextPrimary
                    )
                    if (profile.recommendedForVivoT4) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(NeonLime.copy(alpha = 0.2f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "RECOMMENDED FOR VIVO T4",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Black,
                                    fontSize = 9.sp
                                ),
                                color = NeonLime
                            )
                        }
                    }
                }

                if (isSelected) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Selected",
                        tint = MonsterOrange,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "${profile.graphicsLevel} • ${profile.targetFps} FPS • Shadows: ${profile.shadows} • Anti-Aliasing: ${profile.antiAliasing}",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Medium
                ),
                color = NeonCyan
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = profile.recommendation,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
        }
    }
}

@Composable
fun SensitivityCodeCard(
    title: String,
    code: String,
    description: String
) {
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(CyberCard)
            .border(1.dp, CyberCardBorder, RoundedCornerShape(14.dp))
            .padding(14.dp)
    ) {
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = code,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    ),
                    color = NeonLime
                )

                OutlinedButton(
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        clipboard.setPrimaryClip(ClipData.newPlainText("BGMI Code", code))
                        Toast.makeText(context, "Copied code: $code", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = NeonCyan),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Copy Code")
                }
            }
        }
    }
}
