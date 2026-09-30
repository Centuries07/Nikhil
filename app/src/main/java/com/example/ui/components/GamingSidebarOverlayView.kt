package com.example.ui.components

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.theme.*
import com.example.util.GamingSidebarController
import com.example.viewmodel.GameTurboUiState
import com.example.viewmodel.GameTurboViewModel
import kotlin.math.roundToInt

@Composable
fun GamingSidebarOverlayView(
    viewModel: GameTurboViewModel,
    uiState: GameTurboUiState,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isExpanded by GamingSidebarController.isSidebarExpanded.collectAsStateWithLifecycle()
    val isHidden by GamingSidebarController.isOverlayHidden.collectAsStateWithLifecycle()
    val zoomLoupeActive by GamingSidebarController.isZoomLoupeActive.collectAsStateWithLifecycle()
    val currentZoom by GamingSidebarController.zoomMagnification.collectAsStateWithLifecycle()
    val loupeSizeDp by GamingSidebarController.loupeSizeDp.collectAsStateWithLifecycle()
    val is90FpsForced by GamingSidebarController.is90FpsForced.collectAsStateWithLifecycle()

    // Handle position: freely movable everywhere (both X and Y)
    var handleX by remember { mutableFloatStateOf(16f) }
    var handleY by remember { mutableFloatStateOf(280f) }

    // Loupe position: freely movable everywhere
    var loupeOffsetX by remember { mutableFloatStateOf(0f) }
    var loupeOffsetY by remember { mutableFloatStateOf(0f) }

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val density = androidx.compose.ui.platform.LocalDensity.current
        val maxW = constraints.maxWidth.toFloat()
        val maxH = constraints.maxHeight.toFloat()

        // Center Tactical Zoom Loupe (freely movable anywhere on screen)
        if (zoomLoupeActive || uiState.inGameZoomLoupeActive) {
            MovableTacticalZoomLoupe(
                magnification = currentZoom,
                sizeDp = loupeSizeDp,
                offsetX = loupeOffsetX,
                offsetY = loupeOffsetY,
                onDrag = { dx, dy ->
                    val limitX = maxW / 2f - 40f
                    val limitY = maxH / 2f - 40f
                    loupeOffsetX = (loupeOffsetX + dx).coerceIn(-limitX, limitX)
                    loupeOffsetY = (loupeOffsetY + dy).coerceIn(-limitY, limitY)
                },
                onClose = {
                    GamingSidebarController.setZoomLoupeActive(false)
                },
                modifier = Modifier.align(Alignment.Center)
            )
        }

        // STATE 1: HIDDEN (Collapsed into a discreet 36dp floating mini bubble)
        if (isHidden) {
            Box(
                modifier = Modifier
                    .offset { IntOffset(handleX.roundToInt(), handleY.roundToInt()) }
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(CyberDark.copy(alpha = 0.85f))
                    .border(1.5.dp, NeonLime, CircleShape)
                    .pointerInput(Unit) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            val limitX = (maxW - 40f * density.density).coerceAtLeast(0f)
                            val limitY = (maxH - 40f * density.density).coerceAtLeast(0f)
                            handleX = (handleX + dragAmount.x).coerceIn(0f, limitX)
                            handleY = (handleY + dragAmount.y).coerceIn(0f, limitY)
                        }
                    }
                    .clickable {
                        GamingSidebarController.setOverlayHidden(false)
                        Toast.makeText(context, "Overlay restored! Drag anywhere or tap to expand.", Toast.LENGTH_SHORT).show()
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Visibility,
                    contentDescription = "Unhide Gaming Sidebar",
                    tint = NeonLime,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
        // STATE 2: RETRACTED HANDLE (Freely draggable everywhere across the screen)
        else if (!isExpanded) {
            Box(
                modifier = Modifier
                    .offset { IntOffset(handleX.roundToInt(), handleY.roundToInt()) }
                    .width(62.dp)
                    .height(72.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(CyberDark.copy(alpha = 0.94f))
                    .border(
                        1.5.dp,
                        Brush.linearGradient(listOf(NeonCyan, NeonLime)),
                        RoundedCornerShape(16.dp)
                    )
                    .pointerInput(Unit) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            val limitX = (maxW - 68f * density.density).coerceAtLeast(0f)
                            val limitY = (maxH - 78f * density.density).coerceAtLeast(0f)
                            handleX = (handleX + dragAmount.x).coerceIn(0f, limitX)
                            handleY = (handleY + dragAmount.y).coerceIn(0f, limitY)
                        }
                    }
                    .clickable { GamingSidebarController.setSidebarExpanded(true) }
                    .padding(vertical = 5.dp, horizontal = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // Pulsing indicator dot
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(NeonLime)
                    )

                    // Live FPS Counter
                    Text(
                        text = "${uiState.fpsLiveState.currentFps}",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 13.sp
                        ),
                        color = NeonLime
                    )

                    Text(
                        text = "90 FPS",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 8.sp
                        ),
                        color = TextMuted
                    )

                    // Move hint
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.OpenWith,
                            contentDescription = "Drag anywhere",
                            tint = NeonCyan,
                            modifier = Modifier.size(10.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = "MOVE",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 7.sp
                            ),
                            color = NeonCyan
                        )
                    }
                }
            }
        }

        // STATE 3: EXPANDED GAMING DOCK (Slides out safely within screen limits)
        AnimatedVisibility(
            visible = isExpanded && !isHidden,
            enter = fadeIn() + scaleIn(initialScale = 0.9f),
            exit = fadeOut() + scaleOut(targetScale = 0.9f),
            modifier = Modifier
                .offset {
                    val dockW = (290f * density.density)
                    val dockH = (400f * density.density)
                    val safeX = handleX.coerceIn(8f, (maxW - dockW - 8f).coerceAtLeast(8f))
                    val safeY = (handleY - 80f).coerceIn(12f, (maxH - dockH - 12f).coerceAtLeast(12f))
                    IntOffset(safeX.roundToInt(), safeY.roundToInt())
                }
        ) {
            Box(
                modifier = Modifier
                    .width(290.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(CyberBlack.copy(alpha = 0.97f))
                    .border(
                        1.5.dp,
                        Brush.verticalGradient(listOf(NeonCyan, MonsterOrange)),
                        RoundedCornerShape(20.dp)
                    )
                    .padding(16.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {

                    // Header Row: Title, Hide Button & Close Button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(NeonLime)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Text(
                                    text = "GAMETURBO DOCK",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Black,
                                        letterSpacing = 1.sp
                                    ),
                                    color = NeonCyan
                                )
                                Text(
                                    text = "Moveable Everywhere",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                                    color = TextMuted
                                )
                            }
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            // HIDE BUTTON (Collapses into tiny bubble)
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(WarningYellow.copy(alpha = 0.15f))
                                    .border(1.dp, WarningYellow.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                                    .clickable {
                                        GamingSidebarController.setOverlayHidden(true)
                                        GamingSidebarController.setSidebarExpanded(false)
                                        Toast.makeText(context, "Overlay minimized to bubble! Tap bubble anytime to show.", Toast.LENGTH_SHORT).show()
                                    }
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.VisibilityOff,
                                        contentDescription = "Hide",
                                        tint = WarningYellow,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = "HIDE",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 9.sp
                                        ),
                                        color = WarningYellow
                                    )
                                }
                            }

                            // CLOSE / MINIMIZE BUTTON
                            IconButton(
                                onClick = { GamingSidebarController.setSidebarExpanded(false) },
                                modifier = Modifier
                                    .size(26.dp)
                                    .clip(CircleShape)
                                    .background(CyberCard)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Close Dock",
                                    tint = TextSecondary,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }

                    // FPS Meter Card
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(CyberCard)
                            .border(1.dp, CyberCardBorder, RoundedCornerShape(10.dp))
                            .padding(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "LIVE IN-GAME FPS",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = TextMuted
                                )
                                Row(verticalAlignment = Alignment.Bottom) {
                                    Text(
                                        text = "${uiState.fpsLiveState.currentFps}",
                                        style = MaterialTheme.typography.titleLarge.copy(
                                            fontWeight = FontWeight.Black,
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 22.sp
                                        ),
                                        color = NeonLime
                                    )
                                    Text(
                                        text = " / 90 FPS",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 11.sp
                                        ),
                                        color = TextSecondary,
                                        modifier = Modifier.padding(bottom = 3.dp, start = 2.dp)
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(NeonLime.copy(alpha = 0.2f))
                                    .border(1.dp, NeonLime, RoundedCornerShape(6.dp))
                                    .padding(horizontal = 6.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = "90 FPS LOCKED",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 9.sp
                                    ),
                                    color = NeonLime
                                )
                            }
                        }
                    }

                    // Feature 1: Toggleable Tactical Zoom Loupe
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
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
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Tactical Zoom Loupe",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = TextPrimary
                                )
                            }

                            Switch(
                                checked = zoomLoupeActive,
                                onCheckedChange = {
                                    val active = GamingSidebarController.toggleZoomLoupe()
                                    Toast.makeText(
                                        context,
                                        if (active) "Tactical Zoom Active! Drag scope anywhere on screen." else "Zoom Loupe Disabled",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.Black,
                                    checkedTrackColor = NeonCyan
                                ),
                                modifier = Modifier.scale(0.85f)
                            )
                        }

                        // Magnification selector chips (1.5x, 2.0x, 3.0x, 4.0x)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            listOf(1.5f, 2.0f, 3.0f, 4.0f).forEach { zoom ->
                                val isSelected = currentZoom == zoom
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(26.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (isSelected) NeonCyan.copy(alpha = 0.2f) else CyberCard)
                                        .border(
                                            1.dp,
                                            if (isSelected) NeonCyan else Color.Transparent,
                                            RoundedCornerShape(6.dp)
                                        )
                                        .clickable {
                                            GamingSidebarController.setZoomMagnification(zoom)
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "${zoom}x",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            fontSize = 10.sp
                                        ),
                                        color = if (isSelected) NeonCyan else TextMuted
                                    )
                                }
                            }
                        }

                        // Scope Size selector (Small / Med / Large)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            listOf(Pair("Small", 130), Pair("Medium", 170), Pair("Large", 210)).forEach { (name, size) ->
                                val isSelected = loupeSizeDp == size
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(24.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (isSelected) MonsterOrange.copy(alpha = 0.2f) else CyberCard)
                                        .border(
                                            1.dp,
                                            if (isSelected) MonsterOrange else Color.Transparent,
                                            RoundedCornerShape(6.dp)
                                        )
                                        .clickable {
                                            GamingSidebarController.setLoupeSize(size)
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = name,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            fontSize = 9.sp
                                        ),
                                        color = if (isSelected) MonsterOrange else TextMuted
                                    )
                                }
                            }
                        }

                        // Android Native System Window Magnifier button
                        OutlinedButton(
                            onClick = {
                                GamingSidebarController.openAccessibilityMagnifierSettings(context)
                                Toast.makeText(context, "Turn on 'Window Magnification' for OS-level zoom!", Toast.LENGTH_LONG).show()
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(6.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = NeonCyan),
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Icon(Icons.Default.FindInPage, contentDescription = null, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("System Window Magnifier", fontSize = 10.sp)
                        }
                    }

                    // Feature 2: Force 90 FPS Display Mode Lock
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Force 90/120Hz Display Lock",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = TextPrimary
                            )
                            Text(
                                text = "Locks display to 120Hz for BGMI 90 FPS",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 9.sp),
                                color = TextMuted
                            )
                        }

                        Switch(
                            checked = is90FpsForced,
                            onCheckedChange = {
                                GamingSidebarController.toggle90FpsForced()
                                viewModel.forceUnlock90FpsInBgmi { msg ->
                                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                }
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.Black,
                                checkedTrackColor = NeonLime
                            ),
                            modifier = Modifier.scale(0.85f)
                        )
                    }

                    // Feature 3: Quick RAM Purge & Cool Shield Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Button(
                            onClick = {
                                viewModel.cleanRamDeep { freed, _ ->
                                    Toast.makeText(context, "Purged RAM! Freed $freed MB for BGMI", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MonsterOrange,
                                contentColor = Color.White
                            ),
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.Bolt, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Clean RAM", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = {
                                viewModel.toggleThermalOverride()
                                Toast.makeText(context, "Thermal Shield Active", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = NeonCyan
                            ),
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.Thermostat, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Cool Shield", fontSize = 11.sp)
                        }
                    }

                    // System Floating Window Service Launcher Button
                    Button(
                        onClick = {
                            viewModel.toggleGamingSidebarService(context)
                            val isRunning = GamingSidebarController.isServiceRunning.value
                            Toast.makeText(
                                context,
                                if (isRunning) "Floating Gaming Sidebar active over BGMI!" else "Floating Sidebar stopped",
                                Toast.LENGTH_SHORT
                            ).show()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (GamingSidebarController.isServiceRunning.value) DangerRed else NeonLime,
                            contentColor = if (GamingSidebarController.isServiceRunning.value) Color.White else Color.Black
                        )
                    ) {
                        Icon(
                            imageVector = if (GamingSidebarController.isServiceRunning.value) Icons.Default.Stop else Icons.Default.OpenInNew,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (GamingSidebarController.isServiceRunning.value) "Stop System Overlay" else "Launch Over Game (Floating)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

// Movable Tactical Zoom Loupe (Draggable anywhere across the screen)
@Composable
fun MovableTacticalZoomLoupe(
    magnification: Float,
    sizeDp: Int,
    offsetX: Float,
    offsetY: Float,
    onDrag: (Float, Float) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
            .size(sizeDp.dp)
            .clip(CircleShape)
            .background(Color.Black.copy(alpha = 0.28f))
            .border(2.5.dp, NeonCyan, CircleShape)
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    onDrag(dragAmount.x, dragAmount.y)
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cx = size.width / 2f
            val cy = size.height / 2f
            val radius = (size.width / 2f) - 6.dp.toPx()

            // Inner Mil-Dot Range Ring (scales with magnification)
            val innerRadius = radius * (0.65f / (magnification / 2f).coerceIn(0.75f, 2f))
            drawCircle(
                color = Color(0xFF00FF9D),
                radius = innerRadius,
                style = Stroke(
                    width = 1.5.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                )
            )

            // Reticle Crosshairs
            val armLength = 24.dp.toPx()
            val gap = 12.dp.toPx()

            // Top
            drawLine(
                color = Color(0xFF00FF9D),
                start = Offset(cx, cy - gap),
                end = Offset(cx, cy - gap - armLength),
                strokeWidth = 2.dp.toPx()
            )
            // Bottom
            drawLine(
                color = Color(0xFF00FF9D),
                start = Offset(cx, cy + gap),
                end = Offset(cx, cy + gap + armLength),
                strokeWidth = 2.dp.toPx()
            )
            // Left
            drawLine(
                color = Color(0xFF00FF9D),
                start = Offset(cx - gap, cy),
                end = Offset(cx - gap - armLength, cy),
                strokeWidth = 2.dp.toPx()
            )
            // Right
            drawLine(
                color = Color(0xFF00FF9D),
                start = Offset(cx + gap, cy),
                end = Offset(cx + gap + armLength, cy),
                strokeWidth = 2.dp.toPx()
            )

            // Mil-dots on the crosshairs
            val dotSpacing = 8.dp.toPx()
            for (i in 1..3) {
                val d = gap + (i * dotSpacing)
                drawCircle(color = Color(0xFF00E5FF), radius = 2.dp.toPx(), center = Offset(cx, cy - d))
                drawCircle(color = Color(0xFF00E5FF), radius = 2.dp.toPx(), center = Offset(cx, cy + d))
                drawCircle(color = Color(0xFF00E5FF), radius = 2.dp.toPx(), center = Offset(cx - d, cy))
                drawCircle(color = Color(0xFF00E5FF), radius = 2.dp.toPx(), center = Offset(cx + d, cy))
            }

            // Center Precision Red Aim Dot
            drawCircle(
                color = Color(0xFFFF5722),
                radius = 3.5.dp.toPx(),
                center = Offset(cx, cy)
            )
        }

        // Loupe Magnification & Drag Label
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 8.dp)
        ) {
            Text(
                text = "${magnification}x SCOPE LOUPE",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 9.sp
                ),
                color = NeonCyan
            )
            Text(
                text = "DRAG TO POSITION",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 7.sp,
                    fontWeight = FontWeight.Medium
                ),
                color = TextSecondary
            )
        }

        // Small Close (X) button at top of scope
        IconButton(
            onClick = onClose,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(4.dp)
                .size(20.dp)
                .clip(CircleShape)
                .background(Color.Black.copy(alpha = 0.6f))
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close Loupe",
                tint = Color.White,
                modifier = Modifier.size(12.dp)
            )
        }
    }
}

private fun Modifier.scale(scale: Float): Modifier = this.then(
    Modifier.size((48 * scale).dp, (24 * scale).dp)
)
