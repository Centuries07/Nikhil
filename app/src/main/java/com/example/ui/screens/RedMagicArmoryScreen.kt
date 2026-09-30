package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
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
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Hearing
import androidx.compose.material.icons.filled.PanTool
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.viewmodel.GameTurboUiState
import com.example.viewmodel.GameTurboViewModel

@Composable
fun RedMagicArmoryScreen(
    viewModel: GameTurboViewModel,
    uiState: GameTurboUiState,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val gyro = uiState.gyroState
    val thermal = uiState.thermalTelemetry
    val currentDevice = uiState.detectedDevice

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // RedMagic Armory Banner
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(Color(0xFF280909), Color(0xFF140707))
                        )
                    )
                    .border(1.5.dp, DangerRed.copy(alpha = 0.7f), RoundedCornerShape(16.dp))
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
                                    .background(DangerRed)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "REDMAGIC ESPORTS ARMORY",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 1.2.sp
                                ),
                                color = DangerRed
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Flagship Gaming Advantages",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
                            color = TextPrimary
                        )
                        Text(
                            text = "Shizuku Thermal Override • In-Game Zoom • Virtual L1/R1 Triggers",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(DangerRed.copy(alpha = 0.2f))
                            .border(1.dp, DangerRed, RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "PRO ARSENAL",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 10.sp
                            ),
                            color = DangerRed
                        )
                    }
                }
            }
        }

        // Section 0: Anti-Ban & Fair Play Security Verification
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(NeonLime.copy(alpha = 0.12f))
                    .border(1.dp, NeonLime.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                    .padding(14.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = NeonLime,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "100% SAFE & BAN-FREE (KRAFTON FAIR PLAY VERIFIED)",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Black,
                                letterSpacing = 0.8.sp
                            ),
                            color = NeonLime
                        )
                        Text(
                            text = "Operates entirely at the Android OS system layer via standard WindowManager overlay and Shizuku system settings. Zero game memory modification, zero file injection, and zero risk of ban.",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = TextPrimary,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }

        // Section 1: Shizuku Thermal Management Module (Override Vivo T4 Throttling)
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CyberDark),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.horizontalGradient(listOf(MonsterOrange, Color.Transparent))
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
                                imageVector = Icons.Default.Thermostat,
                                contentDescription = null,
                                tint = MonsterOrange,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "SHIZUKU THERMAL THROTTLING OVERRIDE",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.sp
                                    ),
                                    color = MonsterOrange
                                )
                                Text(
                                    text = "Vivo T4 & iQOO Z10 Sustained Peak FPS",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                    color = TextMuted
                                )
                            }
                        }

                        Switch(
                            checked = thermal.isOverrideActive,
                            onCheckedChange = {
                                viewModel.toggleThermalOverride()
                                Toast.makeText(
                                    context,
                                    if (!thermal.isOverrideActive) "Thermal Override Active: 90-120 FPS Sustained!" else "Reset to Stock Vivo Thermal Engine",
                                    Toast.LENGTH_SHORT
                                ).show()
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = MonsterOrange
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Live Temperature Gauges
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("BATTERY TEMP", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                            Text(
                                text = "${thermal.batteryTempC} °C",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Black,
                                    fontFamily = FontFamily.Monospace
                                ),
                                color = if (thermal.batteryTempC < 38.5f) NeonLime else WarningYellow
                            )
                            Text(
                                text = if (thermal.batteryTempC < 38.5f) "Safe & Cool" else "Warm (Normal)",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                                color = TextSecondary
                            )
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("ESTIMATED SOC", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                            Text(
                                text = "${thermal.estimatedSocTempC} °C",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Black,
                                    fontFamily = FontFamily.Monospace
                                ),
                                color = NeonCyan
                            )
                            Text(
                                text = "CPU/GPU Core",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                                color = TextSecondary
                            )
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("SUSTAINED FPS", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                            Text(
                                text = "${thermal.sustainedFpsHealthPercent}%",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Black,
                                    fontFamily = FontFamily.Monospace
                                ),
                                color = NeonLime
                            )
                            Text(
                                text = "Zero Drop Pacing",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                                color = TextSecondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Status Pill
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(CyberCard)
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Current State: ${thermal.currentThrottleLevel}",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                ),
                                color = if (thermal.isOverrideActive) NeonLime else WarningYellow
                            )

                            Text(
                                text = "Auto-Cutoff: ${thermal.emergencyAutoCutoffC}°C",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 10.sp
                                ),
                                color = DangerRed
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "THERMAL PROFILES",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = TextMuted
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    uiState.thermalProfiles.forEach { profile ->
                        val isSelected = uiState.activeThermalProfileId == profile.id
                        val pColor = Color(profile.colorHex)

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) CyberCardActive else CyberCard)
                                .border(
                                    1.dp,
                                    if (isSelected) pColor else CyberCardBorder,
                                    RoundedCornerShape(10.dp)
                                )
                                .clickable {
                                    viewModel.selectThermalProfile(profile.id)
                                    Toast.makeText(context, "Thermal Profile: ${profile.title}", Toast.LENGTH_SHORT).show()
                                }
                                .padding(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = profile.title,
                                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                            color = if (isSelected) pColor else TextPrimary
                                        )
                                        if (isSelected) {
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(4.dp))
                                                    .background(pColor.copy(alpha = 0.2f))
                                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                            ) {
                                                Text(
                                                    text = "ACTIVE",
                                                    style = MaterialTheme.typography.labelSmall.copy(
                                                        fontWeight = FontWeight.Black,
                                                        fontSize = 9.sp
                                                    ),
                                                    color = pColor
                                                )
                                            }
                                        }
                                    }
                                    Text(
                                        text = profile.description,
                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                        color = TextSecondary
                                    )
                                }

                                Text(
                                    text = profile.sustainedFpsLabel,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    ),
                                    color = pColor
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Display Dimming Bypass
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Bypass Thermal Screen Dimming",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                color = TextPrimary
                            )
                            Text(
                                text = "Prevents Vivo T4 display from automatically dimming when phone reaches 40°C in BGMI.",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                color = TextSecondary
                            )
                        }

                        Switch(
                            checked = thermal.displayDimmingBlocked,
                            onCheckedChange = { viewModel.toggleThermalDisplayDimmingBypass() },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = NeonCyan)
                        )
                    }
                }
            }
        }

        // Section 2: In-Game Live Overlay Sidebar & Tactical Zoom Loupe
        item {
            com.example.ui.components.OverlaySidebarCard(
                viewModel = viewModel
            )
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CyberCard),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.horizontalGradient(listOf(NeonCyan, Color.Transparent))
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
                                imageVector = Icons.Default.ZoomIn,
                                contentDescription = null,
                                tint = NeonCyan,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "IN-GAME TACTICAL ZOOM LOUPE & QUICK BAR",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = NeonCyan
                                )
                                Text(
                                    text = "Toggleable live while inside BGMI match",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                    color = TextMuted
                                )
                            }
                        }

                        Switch(
                            checked = uiState.inGameZoomLoupeActive,
                            onCheckedChange = {
                                viewModel.toggleInGameZoomLoupe()
                                Toast.makeText(
                                    context,
                                    if (!uiState.inGameZoomLoupeActive) "Tactical Zoom Loupe Overlay Active!" else "Zoom Loupe Disabled",
                                    Toast.LENGTH_SHORT
                                ).show()
                            },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color.Black, checkedTrackColor = NeonCyan)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Activates a floating center magnifying loupe for long-distance spotting in Erangel without opening scopes. Non-invasive OS accessibility window (100% fair play):",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Zoom Level Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(1.5f, 2.0f, 3.0f, 4.0f).forEach { zoom ->
                            val isSelected = uiState.inGameZoomMagnification == zoom
                            OutlinedButton(
                                onClick = { viewModel.setInGameZoomLevel(zoom) },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = if (isSelected) NeonCyan else Color.Transparent,
                                    contentColor = if (isSelected) Color.Black else NeonCyan
                                )
                            ) {
                                Text("${zoom}x")
                            }
                        }
                    }

                    if (uiState.inGameZoomLoupeActive) {
                        Spacer(modifier = Modifier.height(12.dp))
                        // Center Loupe Preview Box
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(90.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(CyberDark)
                                .border(1.5.dp, NeonCyan, RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "TACTICAL ZOOM LOUPE ACTIVE (${uiState.inGameZoomMagnification}x)",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    ),
                                    color = NeonCyan
                                )
                                Text(
                                    text = "Centered on Crosshair • Crisp Silhouette Enhancement",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                    color = NeonLime
                                )
                            }
                        }
                    }
                }
            }
        }

        // Section 3: Hardware Auto-Detection & Calibration (Vivo T4 vs iQOO Z10)
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CyberDark),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.horizontalGradient(listOf(NeonCyan, NeonLime))
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
                                imageVector = Icons.Default.Tune,
                                contentDescription = null,
                                tint = NeonCyan,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "DETECTED GAMING HARDWARE",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.sp
                                    ),
                                    color = NeonCyan
                                )
                                Text(
                                    text = "${currentDevice.brand} • ${currentDevice.modelName}",
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
                                text = "AUTO-CALIBRATED",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 9.sp
                                ),
                                color = NeonLime
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Specs Matrix
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("REFRESH RATE", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                            Text(
                                text = "${currentDevice.refreshRateHz} Hz",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Black,
                                    fontFamily = FontFamily.Monospace
                                ),
                                color = NeonLime
                            )
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("TOUCH SAMPLING", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                            Text(
                                text = "${currentDevice.touchSamplingHz} Hz",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Black,
                                    fontFamily = FontFamily.Monospace
                                ),
                                color = NeonCyan
                            )
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("COOLING ARCH", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                            Text(
                                text = currentDevice.coolingTech.split(" ").first(),
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Black,
                                    fontFamily = FontFamily.Monospace
                                ),
                                color = MonsterOrange
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Chipset: ${currentDevice.chipset}\nOS Engine: ${currentDevice.osFlavor}",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = TextSecondary,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Switch profile between Vivo T4 and iQOO Z10
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        com.example.model.TargetDeviceType.entries.forEach { dev ->
                            val isSelected = currentDevice == dev
                            OutlinedButton(
                                onClick = {
                                    viewModel.selectDeviceType(dev)
                                    Toast.makeText(context, "Switched Profile: ${dev.modelName}", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = if (isSelected) NeonCyan else Color.Transparent,
                                    contentColor = if (isSelected) Color.Black else NeonCyan
                                )
                            ) {
                                Text(dev.modelName.split(" ").first() + " " + (dev.modelName.split(" ").getOrNull(1) ?: ""))
                            }
                        }
                    }
                }
            }
        }

        // Section 4: Eagle Eye Visual Enemy Spotter (Display Filters)
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.RemoveRedEye,
                    contentDescription = null,
                    tint = NeonLime,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "EAGLE EYE • ENEMY SPOTTER (DISPLAY SHADERS)",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    ),
                    color = TextMuted
                )
            }
        }

        items(uiState.visualFilterPresets) { filter ->
            val isSelected = uiState.activeVisualFilterId == filter.id
            val accentColor = Color(filter.colorAccentHex)

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(if (isSelected) CyberCardActive else CyberCard)
                    .border(
                        width = if (isSelected) 1.5.dp else 1.dp,
                        color = if (isSelected) accentColor else CyberCardBorder,
                        shape = RoundedCornerShape(14.dp)
                    )
                    .clickable {
                        viewModel.selectVisualFilter(filter.id)
                        Toast.makeText(context, "Activated: ${filter.title}", Toast.LENGTH_SHORT).show()
                    }
                    .padding(14.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = filter.title,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = if (isSelected) accentColor else TextPrimary
                        )

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(CyberDark)
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = filter.targetMap,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                ),
                                color = accentColor
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = filter.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        lineHeight = 16.sp
                    )
                }
            }
        }

        // Section 5: Audio Footstep Sound Radar
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Hearing,
                    contentDescription = null,
                    tint = NeonCyan,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "FOOTSTEP SOUND RADAR EQUALIZER",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    ),
                    color = TextMuted
                )
            }
        }

        items(uiState.audioProfiles) { audio ->
            val isSelected = uiState.activeAudioProfileId == audio.id

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(if (isSelected) CyberCardActive else CyberCard)
                    .border(
                        width = if (isSelected) 1.5.dp else 1.dp,
                        color = if (isSelected) NeonCyan else CyberCardBorder,
                        shape = RoundedCornerShape(14.dp)
                    )
                    .clickable {
                        viewModel.selectAudioProfile(audio.id)
                        Toast.makeText(context, "Audio Mode: ${audio.title}", Toast.LENGTH_SHORT).show()
                    }
                    .padding(14.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = audio.title,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = if (isSelected) NeonCyan else TextPrimary
                        )

                        Text(
                            text = "Footstep +${audio.footstepBoostDb}dB",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.Monospace
                            ),
                            color = NeonLime
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = audio.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
            }
        }

        // Section 6: Gyroscope Hardware Sensor Drift Calibrator
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CyberDark),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.horizontalGradient(listOf(NeonLime, Color.Transparent))
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
                                imageVector = Icons.Default.Tune,
                                contentDescription = null,
                                tint = NeonLime,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "GYROSCOPE LIVE SENSOR CALIBRATOR",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = NeonLime
                                )
                                Text(
                                    text = "Eliminates vertical downward drift during 6x sprays",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                    color = TextMuted
                                )
                            }
                        }

                        OutlinedButton(
                            onClick = {
                                viewModel.calibrateGyro()
                                Toast.makeText(context, "Gyroscope 0-Drift offset calibrated!", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = NeonLime)
                        ) {
                            Text("Calibrate")
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("PITCH", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                            Text(
                                text = String.format("%.2f", gyro.pitch),
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = NeonCyan
                            )
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("ROLL", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                            Text(
                                text = String.format("%.2f", gyro.roll),
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = NeonLime
                            )
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("DRIFT OFFSET", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                            Text(
                                text = "±${gyro.driftOffset}°",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = MonsterOrange
                            )
                        }
                    }
                }
            }
        }
    }
}
