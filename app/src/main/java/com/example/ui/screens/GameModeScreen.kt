package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
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
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DeveloperBoard
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.SportsEsports
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
import com.example.model.SelectedGameMode
import com.example.ui.components.CyberActionButton
import com.example.ui.theme.*
import com.example.util.ShizukuConnectionState
import com.example.util.ShizukuOptimizationTask
import com.example.viewmodel.GameNavTab
import com.example.viewmodel.GameTurboUiState
import com.example.viewmodel.GameTurboViewModel

@Composable
fun GameModeScreen(
    viewModel: GameTurboViewModel,
    uiState: GameTurboUiState,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val shizukuState = uiState.shizukuConnectionState

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Mode Header
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(CyberDark)
                    .border(1.dp, MonsterOrange.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(MonsterOrange)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "GAME ENGINE MODE SELECTOR",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.2.sp
                            ),
                            color = MonsterOrange
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Vivo T4 & iQOO Z10 Calibrations",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
                        color = TextPrimary
                    )
                    Text(
                        text = "Dedicated profiles for BGMI, system-wide smoothness, and Shizuku binder OS-level optimizations.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
            }
        }

        // Mode Cards
        item {
            Text(
                text = "AVAILABLE PRESETS",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                ),
                color = TextMuted
            )
        }

        items(SelectedGameMode.entries.size) { index ->
            val mode = SelectedGameMode.entries[index]
            val isSelected = uiState.selectedGameMode == mode
            val modeColor = Color(mode.colorHex)

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (isSelected) CyberCardActive else CyberCard)
                    .border(
                        width = if (isSelected) 1.5.dp else 1.dp,
                        color = if (isSelected) modeColor else CyberCardBorder,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .clickable {
                        viewModel.selectGameMode(mode)
                        Toast.makeText(context, "Active Mode: ${mode.title}", Toast.LENGTH_SHORT).show()
                    }
                    .padding(16.dp)
                    .testTag("mode_card_${mode.name.lowercase()}")
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(modeColor.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                val icon = when (mode) {
                                    SelectedGameMode.BGMI_ESPORTS -> Icons.Default.SportsEsports
                                    SelectedGameMode.GENERAL_PERFORMANCE -> Icons.Default.Speed
                                    SelectedGameMode.VIVO_LITE_SHIZUKU -> Icons.Default.DeveloperBoard
                                }
                                Icon(
                                    imageVector = icon,
                                    contentDescription = null,
                                    tint = modeColor,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = mode.title,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = if (isSelected) modeColor else TextPrimary
                                )
                                Text(
                                    text = mode.badge,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Black,
                                        fontSize = 9.sp,
                                        fontFamily = FontFamily.Monospace
                                    ),
                                    color = modeColor
                                )
                            }
                        }

                        if (isSelected) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(modeColor),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Selected",
                                    tint = Color.Black,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = mode.subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        lineHeight = 18.sp
                    )
                }
            }
        }

        // Shizuku Manager Service & Binder Connection Panel
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CyberDark),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = androidx.compose.ui.graphics.Brush.linearGradient(
                        listOf(
                            if (shizukuState == ShizukuConnectionState.CONNECTED) NeonLime else MonsterOrange,
                            Color.Transparent
                        )
                    )
                ),
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.DeveloperBoard,
                                contentDescription = null,
                                tint = if (shizukuState == ShizukuConnectionState.CONNECTED) NeonLime else WarningYellow,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "SHIZUKU MANAGER SERVICE",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.sp
                                    ),
                                    color = if (shizukuState == ShizukuConnectionState.CONNECTED) NeonLime else WarningYellow
                                )
                                Text(
                                    text = "Non-Root Binder System Bridge",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                    color = TextMuted
                                )
                            }
                        }

                        // Recheck / Refresh Status
                        IconButton(
                            onClick = {
                                viewModel.checkShizukuStatus(simulated = (shizukuState != ShizukuConnectionState.CONNECTED))
                                Toast.makeText(context, "Checking Shizuku Binder status...", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(CyberCard)
                                .size(34.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Check Shizuku",
                                tint = NeonCyan,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Connection Status Box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(CyberCard)
                            .border(
                                1.dp,
                                if (shizukuState == ShizukuConnectionState.CONNECTED) NeonLime.copy(alpha = 0.5f) else CyberCardBorder,
                                RoundedCornerShape(12.dp)
                            )
                            .padding(12.dp)
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
                                            .background(
                                                when (shizukuState) {
                                                    ShizukuConnectionState.CONNECTED -> NeonLime
                                                    ShizukuConnectionState.PERMISSION_NEEDED -> NeonCyan
                                                    ShizukuConnectionState.SERVICE_STOPPED -> WarningYellow
                                                    ShizukuConnectionState.NOT_INSTALLED -> DangerRed
                                                }
                                            )
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = shizukuState.title,
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                        color = TextPrimary
                                    )
                                }
                                Text(
                                    text = shizukuState.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary
                                )
                            }

                            when (shizukuState) {
                                ShizukuConnectionState.PERMISSION_NEEDED -> {
                                    Button(
                                        onClick = {
                                            viewModel.requestShizukuAuthorization()
                                            Toast.makeText(context, "Opening Shizuku Authorization prompt...", Toast.LENGTH_SHORT).show()
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = NeonLime,
                                            contentColor = Color.Black
                                        )
                                    ) {
                                        Icon(Icons.Default.Bolt, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Authorize")
                                    }
                                }
                                ShizukuConnectionState.CONNECTED -> {
                                    OutlinedButton(
                                        onClick = { viewModel.openShizukuApp() },
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = NeonLime)
                                    ) {
                                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("View in App")
                                    }
                                }
                                ShizukuConnectionState.SERVICE_STOPPED -> {
                                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Button(
                                            onClick = {
                                                viewModel.requestShizukuAuthorization()
                                                Toast.makeText(context, "Sending Shizuku authorization request...", Toast.LENGTH_SHORT).show()
                                            },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = MonsterOrange,
                                                contentColor = Color.White
                                            )
                                        ) {
                                            Icon(Icons.Default.Bolt, contentDescription = null, modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Authorize / Sync", fontSize = 11.sp)
                                        }

                                        OutlinedButton(
                                            onClick = { viewModel.openShizukuApp() },
                                            colors = ButtonDefaults.outlinedButtonColors(contentColor = WarningYellow)
                                        ) {
                                            Icon(Icons.Default.Link, contentDescription = null, modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Open", fontSize = 11.sp)
                                        }
                                    }
                                }
                                ShizukuConnectionState.NOT_INSTALLED -> {
                                    OutlinedButton(
                                        onClick = { viewModel.openShizukuApp() },
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = NeonCyan)
                                    ) {
                                        Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Install")
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "ADVANCED SYSTEM OPTIMIZATIONS (VIVO T4 & IQOO Z10)",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = TextMuted
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Shizuku Optimization Tasks List
                    uiState.shizukuTasks.forEach { task ->
                        val isApplied = uiState.appliedShizukuTaskIds.contains(task.id)
                        ShizukuTaskItemRow(
                            task = task,
                            isApplied = isApplied,
                            onToggle = {
                                viewModel.toggleShizukuTask(task.id)
                                Toast.makeText(
                                    context,
                                    if (!isApplied) "Applied: ${task.title}" else "Reverted: ${task.title}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedButton(
                        onClick = {
                            viewModel.applyAllShizukuTasks()
                            Toast.makeText(context, "All 5 advanced system optimizations applied!", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = NeonLime),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Apply All System Tweaks")
                    }
                }
            }
        }

        // Apply Mode CTA Button
        item {
            CyberActionButton(
                text = if (uiState.selectedGameMode == SelectedGameMode.BGMI_ESPORTS) "Launch BGMI with Monster Boost" else "Apply Device Optimization",
                icon = if (uiState.selectedGameMode == SelectedGameMode.BGMI_ESPORTS) Icons.Default.PlayArrow else Icons.Default.Bolt,
                primaryColor = Color(uiState.selectedGameMode.colorHex),
                secondaryColor = Color(uiState.selectedGameMode.colorHex).copy(alpha = 0.7f),
                isLoading = uiState.isCleaningRam,
                testTag = "apply_mode_action_button",
                onClick = {
                    if (uiState.selectedGameMode == SelectedGameMode.BGMI_ESPORTS) {
                        viewModel.boostAndLaunchGame(viewModel.gameLauncher.bgmiPackageName) {
                            Toast.makeText(context, "BGMI Esports Mode Active! Ready for match.", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        viewModel.cleanRamDeep { freed, procs ->
                            Toast.makeText(context, "${uiState.selectedGameMode.title} applied! Freed ${freed}MB RAM.", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun ShizukuTaskItemRow(
    task: ShizukuOptimizationTask,
    isApplied: Boolean,
    onToggle: () -> Unit
) {
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(CyberCard)
            .border(
                1.dp,
                if (isApplied) NeonLime.copy(alpha = 0.6f) else CyberCardBorder,
                RoundedCornerShape(12.dp)
            )
            .padding(12.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = task.title,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimary
                    )
                    Text(
                        text = "Target: ${task.targetComponent}",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = NeonCyan
                    )
                }

                Switch(
                    checked = isApplied,
                    onCheckedChange = { onToggle() },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.Black,
                        checkedTrackColor = NeonLime
                    )
                )
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = task.explanation,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(6.dp))
                    .background(CyberDark)
                    .clickable {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        clipboard.setPrimaryClip(ClipData.newPlainText("Command", task.command))
                        Toast.makeText(context, "Copied ADB command", Toast.LENGTH_SHORT).show()
                    }
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = task.command,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontFamily = FontFamily.Monospace,
                        fontSize = 9.sp
                    ),
                    color = TextMuted,
                    maxLines = 1,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = Icons.Default.ContentCopy,
                    contentDescription = "Copy",
                    tint = NeonLime,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}
