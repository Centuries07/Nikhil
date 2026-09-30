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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CenterFocusStrong
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ScopeZoomConfig
import com.example.ui.theme.*
import com.example.viewmodel.GameTurboUiState
import com.example.viewmodel.GameTurboViewModel

@Composable
fun AiTacticalScreen(
    viewModel: GameTurboViewModel,
    uiState: GameTurboUiState,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // AI Tactical Banner
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(CyberDark)
                    .border(1.dp, NeonLime.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(NeonLime.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = NeonLime,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "BGMI AI TACTICAL ASSISTANT",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            ),
                            color = NeonLime
                        )
                        Text(
                            text = "Aim Assist & Scope Zoom Matrix",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
                            color = TextPrimary
                        )
                    }
                }
            }
        }

        // Raipur, Shankar Nagar Geographic Telemetry Card
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
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = MonsterOrange,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "RAIPUR • SHANKAR NAGAR (CHHATTISGARH)",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = MonsterOrange
                                )
                                Text(
                                    text = "Central India Direct Cloud Gateway",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(NeonLime.copy(alpha = 0.15f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "24 - 32 ms Expected",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = NeonLime
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "From Shankar Nagar, Raipur, BGMI packets travel ~950 km directly to AWS Mumbai Datacenter (ap-south-1). With the GameTurbo Anycast DNS routing and Socket KeepAlive enabled, your ping stays locked under 30ms without random 100ms spikes during close-range fights.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        lineHeight = 18.sp
                    )
                }
            }
        }

        // Interactive Crosshair Reticle & Pre-Aim Trainer
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CyberDark),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.horizontalGradient(listOf(NeonCyan.copy(alpha = 0.5f), Color.Transparent))
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CenterFocusStrong,
                                contentDescription = null,
                                tint = NeonCyan,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "TACTICAL CROSSHAIR RETICLE PREVIEW",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = NeonCyan
                            )
                        }

                        Text(
                            text = "Pre-Aim Centering",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = TextMuted
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Reticle Canvas Box
                    val crosshairColor = Color(uiState.crosshairColorHex)
                    val sizePx = uiState.crosshairSizeDp
                    val gapPx = uiState.crosshairGapDp

                    Box(
                        modifier = Modifier
                            .size(130.dp)
                            .clip(CircleShape)
                            .background(CyberBlack)
                            .border(1.5.dp, CyberCardBorder, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val centerX = size.width / 2
                            val centerY = size.height / 2
                            val strokeWidth = 2.5f

                            // Top tick
                            drawLine(
                                color = crosshairColor,
                                start = Offset(centerX, centerY - gapPx - sizePx),
                                end = Offset(centerX, centerY - gapPx),
                                strokeWidth = strokeWidth
                            )
                            // Bottom tick
                            drawLine(
                                color = crosshairColor,
                                start = Offset(centerX, centerY + gapPx),
                                end = Offset(centerX, centerY + gapPx + sizePx),
                                strokeWidth = strokeWidth
                            )
                            // Left tick
                            drawLine(
                                color = crosshairColor,
                                start = Offset(centerX - gapPx - sizePx, centerY),
                                end = Offset(centerX - gapPx, centerY),
                                strokeWidth = strokeWidth
                            )
                            // Right tick
                            drawLine(
                                color = crosshairColor,
                                start = Offset(centerX + gapPx, centerY),
                                end = Offset(centerX + gapPx + sizePx, centerY),
                                strokeWidth = strokeWidth
                            )

                            // Center dot
                            if (uiState.crosshairDotEnabled) {
                                drawCircle(
                                    color = crosshairColor,
                                    radius = 2.5f,
                                    center = Offset(centerX, centerY)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Reticle Color Picker Buttons
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val colors = listOf(0xFF00FF9D, 0xFF00E5FF, 0xFFFF5722, 0xFFFFD166, 0xFFFFFFFF)
                        colors.forEach { c ->
                            val isSelected = uiState.crosshairColorHex == c
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(Color(c))
                                    .border(
                                        width = if (isSelected) 2.dp else 0.dp,
                                        color = if (isSelected) Color.White else Color.Transparent,
                                        shape = CircleShape
                                    )
                                    .clickable {
                                        viewModel.updateCrosshair(color = c)
                                    }
                            )
                        }
                    }
                }
            }
        }

        // AI Aim Assist Breakdown & Pro Mechanics
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CyberCard),
                shape = RoundedCornerShape(16.dp),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.horizontalGradient(listOf(NeonLime.copy(alpha = 0.4f), Color.Transparent))
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "HOW BGMI AIM ASSIST OPERATES ON ANDROID 16",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = NeonLime
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "• Magnetic Head Centering: Aim Assist in BGMI applies a subtle velocity pull towards the upper torso. If your crosshair is placed at chest level, bullet spread automatically hits the head hitbox.\n• Gyro ADS Synchrony: On the Vivo T4, locking screen touch response to 240Hz ensures that your gyro micro-adjustments register within 16ms, giving you the fastest aim snap in TDM and Classic.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        lineHeight = 18.sp
                    )
                }
            }
        }

        // Scope Zoom & Recoil Sensitivity Matrix
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.ZoomIn,
                    contentDescription = null,
                    tint = NeonCyan,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "TACTICAL SCOPE ZOOM MATRIX (AIM & RECOIL)",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    ),
                    color = TextMuted
                )
            }
        }

        items(uiState.scopeConfigs) { scope ->
            ScopeZoomCard(scope)
        }
    }
}

@Composable
fun ScopeZoomCard(scope: ScopeZoomConfig) {
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
                        text = scope.scopeName,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimary
                    )
                    Text(
                        text = "Zoom Magnification: ${scope.zoomLevel}",
                        style = MaterialTheme.typography.bodySmall,
                        color = NeonCyan
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(CyberDark)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "ADS: ${scope.recommendedAdsSens}%",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            ),
                            color = NeonLime
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(CyberDark)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "Gyro: ${scope.recommendedGyroSens}%",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            ),
                            color = MonsterOrange
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = scope.recoilPullTip,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                lineHeight = 18.sp
            )
        }
    }
}
