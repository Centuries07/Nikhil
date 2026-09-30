package com.example.ui.components

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.theme.*
import com.example.util.GamingSidebarController
import com.example.viewmodel.GameTurboViewModel

@Composable
fun OverlaySidebarCard(
    viewModel: GameTurboViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isRunning by GamingSidebarController.isServiceRunning.collectAsStateWithLifecycle()
    val isExpanded by GamingSidebarController.isSidebarExpanded.collectAsStateWithLifecycle()
    val isHidden by GamingSidebarController.isOverlayHidden.collectAsStateWithLifecycle()
    val isZoomActive by GamingSidebarController.isZoomLoupeActive.collectAsStateWithLifecycle()
    val currentZoom by GamingSidebarController.zoomMagnification.collectAsStateWithLifecycle()
    val hasPermission = remember(isRunning) { GamingSidebarController.canDrawOverlays(context) }

    Card(
        colors = CardDefaults.cardColors(containerColor = CyberDark),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.horizontalGradient(
                listOf(NeonCyan, if (isRunning) NeonLime else MonsterOrange)
            )
        ),
        shape = RoundedCornerShape(18.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isRunning) NeonLime.copy(alpha = 0.2f) else CyberCard)
                            .border(1.dp, if (isRunning) NeonLime else NeonCyan, RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ViewSidebar,
                            contentDescription = null,
                            tint = if (isRunning) NeonLime else NeonCyan,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "IN-GAME OVERLAY SIDEBAR",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp
                            ),
                            color = NeonCyan
                        )
                        Text(
                            text = "Floating • Moveable Everywhere • Hideable",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                            color = TextPrimary
                        )
                    }
                }

                // Active status pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (isRunning) NeonLime.copy(alpha = 0.2f) else CyberCard)
                        .border(1.dp, if (isRunning) NeonLime else CyberCardBorder, RoundedCornerShape(20.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(if (isRunning) NeonLime else TextMuted)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isRunning) (if (isHidden) "HIDDEN" else "RUNNING") else "STANDBY",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 9.sp
                            ),
                            color = if (isRunning) (if (isHidden) WarningYellow else NeonLime) else TextMuted
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Floating tactical widget for BGMI: Moveable anywhere across your screen in both X and Y. Tap to expand live 90 FPS meter, movable tactical sniper zoom loupe, and instant RAM boost. Can be hidden into a mini floating bubble anytime.",
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                color = TextSecondary,
                lineHeight = 16.sp
            )

            // Permission Warning if overlay permission not granted
            if (!hasPermission) {
                Spacer(modifier = Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(WarningYellow.copy(alpha = 0.12f))
                        .border(1.dp, WarningYellow.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                        .padding(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = WarningYellow,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Overlay permission required to draw over BGMI",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                color = TextPrimary
                            )
                        }

                        Button(
                            onClick = { GamingSidebarController.openOverlaySettings(context) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = WarningYellow,
                                contentColor = Color.Black
                            ),
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text("Grant", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Primary Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        if (isRunning) {
                            GamingSidebarController.stopSidebar(context)
                            Toast.makeText(context, "Overlay Sidebar Stopped", Toast.LENGTH_SHORT).show()
                        } else {
                            GamingSidebarController.startSidebar(context)
                            Toast.makeText(context, "Overlay Sidebar Started over screen! Drag anywhere.", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.weight(1.3f),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isRunning) DangerRed else NeonLime,
                        contentColor = if (isRunning) Color.White else Color.Black
                    )
                ) {
                    Icon(
                        imageVector = if (isRunning) Icons.Default.Stop else Icons.Default.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isRunning) "STOP OVERLAY" else "START OVERLAY",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Black)
                    )
                }

                OutlinedButton(
                    onClick = {
                        GamingSidebarController.setOverlayHidden(false)
                        GamingSidebarController.toggleSidebarExpanded()
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = NeonCyan)
                ) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.Close else Icons.Default.Visibility,
                        contentDescription = null,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isExpanded) "CLOSE DOCK" else "TEST DOCK",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Hide / Unhide & Tactical Zoom Controls Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Hide / Unhide Toggle
                Button(
                    onClick = {
                        val hidden = GamingSidebarController.toggleOverlayHidden()
                        Toast.makeText(
                            context,
                            if (hidden) "Overlay hidden! Tap floating bubble or notification to restore." else "Overlay restored!",
                            Toast.LENGTH_SHORT
                        ).show()
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isHidden) NeonLime else WarningYellow.copy(alpha = 0.2f),
                        contentColor = if (isHidden) Color.Black else WarningYellow
                    ),
                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = if (isHidden) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isHidden) "SHOW OVERLAY" else "HIDE OVERLAY",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Tactical Zoom Loupe Direct Toggle
                Button(
                    onClick = {
                        val active = GamingSidebarController.toggleZoomLoupe()
                        Toast.makeText(
                            context,
                            if (active) "Tactical Zoom Loupe Active! Drag scope over your crosshair." else "Zoom Loupe Disabled",
                            Toast.LENGTH_SHORT
                        ).show()
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isZoomActive) NeonCyan else CyberCard,
                        contentColor = if (isZoomActive) Color.Black else TextPrimary
                    ),
                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ZoomIn,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isZoomActive) "ZOOM: ON (${currentZoom}x)" else "ZOOM: OFF",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Quick Zoom Multiplier Chips (1.5x, 2.0x, 3.0x, 4.0x)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ZOOM MAG:",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 9.sp
                    ),
                    color = TextMuted
                )

                listOf(1.5f, 2.0f, 3.0f, 4.0f).forEach { mag ->
                    val isSelected = currentZoom == mag
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(24.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isSelected) NeonCyan.copy(alpha = 0.25f) else CyberCard)
                            .border(
                                1.dp,
                                if (isSelected) NeonCyan else Color.Transparent,
                                RoundedCornerShape(6.dp)
                            )
                            .clickable {
                                GamingSidebarController.setZoomMagnification(mag)
                                if (!isZoomActive) {
                                    GamingSidebarController.setZoomLoupeActive(true)
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${mag}x",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = if (isSelected) FontWeight.Black else FontWeight.Normal,
                                fontSize = 10.sp
                            ),
                            color = if (isSelected) NeonCyan else TextMuted
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Native System Window Magnifier Direct Button
            OutlinedButton(
                onClick = {
                    GamingSidebarController.openAccessibilityMagnifierSettings(context)
                    Toast.makeText(context, "Enable 'Window Magnification' for hardware screen zoom over BGMI!", Toast.LENGTH_LONG).show()
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = NeonCyan),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Icon(Icons.Default.FindInPage, contentDescription = null, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Activate Android System Window Magnifier",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
