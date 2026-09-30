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
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.NetworkCheck
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Tune
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
import com.example.model.NetworkPingAlgorithm
import com.example.model.RamCleanIntensity
import com.example.ui.theme.*
import com.example.viewmodel.GameTurboUiState
import com.example.viewmodel.GameTurboViewModel

@Composable
fun AdvancedSettingsScreen(
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
        // Header
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(CyberDark)
                    .border(1.dp, NeonCyan.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(NeonCyan.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = null,
                            tint = NeonCyan,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "ADVANCED TUNING PARAMETERS",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            ),
                            color = NeonCyan
                        )
                        Text(
                            text = "RAM, Network & App Whitelist",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
                            color = TextPrimary
                        )
                    }
                }
            }
        }

        // Section 1: RAM Cleaning Intensity
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.CleaningServices,
                    contentDescription = null,
                    tint = MonsterOrange,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "RAM CLEANING INTENSITY",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    ),
                    color = TextMuted
                )
            }
        }

        items(RamCleanIntensity.entries.size) { index ->
            val intensity = RamCleanIntensity.entries[index]
            val isSelected = uiState.ramCleanIntensity == intensity

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
                    .clickable {
                        viewModel.setRamCleanIntensity(intensity)
                        Toast.makeText(context, "Intensity: ${intensity.title}", Toast.LENGTH_SHORT).show()
                    }
                    .padding(14.dp)
                    .testTag("ram_intensity_${intensity.name.lowercase()}")
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = isSelected,
                                onClick = { viewModel.setRamCleanIntensity(intensity) },
                                colors = RadioButtonDefaults.colors(selectedColor = MonsterOrange)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = intensity.title,
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = if (isSelected) MonsterOrange else TextPrimary
                            )
                        }
                        Text(
                            text = intensity.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                            modifier = Modifier.padding(start = 36.dp)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(CyberDark)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = intensity.estimatedFreedRange,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            ),
                            color = if (isSelected) MonsterOrange else TextMuted
                        )
                    }
                }
            }
        }

        // Section 2: Network Ping Boosting Algorithm
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.NetworkCheck,
                    contentDescription = null,
                    tint = NeonLime,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "NETWORK PING BOOSTING ALGORITHM",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    ),
                    color = TextMuted
                )
            }
        }

        items(NetworkPingAlgorithm.entries.size) { index ->
            val algo = NetworkPingAlgorithm.entries[index]
            val isSelected = uiState.networkAlgorithm == algo

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(if (isSelected) CyberCardActive else CyberCard)
                    .border(
                        1.5.dp,
                        if (isSelected) NeonLime else CyberCardBorder,
                        RoundedCornerShape(14.dp)
                    )
                    .clickable {
                        viewModel.setNetworkAlgorithm(algo)
                        Toast.makeText(context, "Network Algorithm: ${algo.title}", Toast.LENGTH_SHORT).show()
                    }
                    .padding(14.dp)
                    .testTag("algo_${algo.name.lowercase()}")
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = isSelected,
                                onClick = { viewModel.setNetworkAlgorithm(algo) },
                                colors = RadioButtonDefaults.colors(selectedColor = NeonLime)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = algo.title,
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = if (isSelected) NeonLime else TextPrimary
                            )
                        }
                        Text(
                            text = algo.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                            modifier = Modifier.padding(start = 36.dp)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(CyberDark)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = algo.protocol,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            ),
                            color = if (isSelected) NeonLime else TextMuted
                        )
                    }
                }
            }
        }

        // Section 3: App Whitelist (Protected from RAM Killing)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = NeonCyan,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "APP WHITELIST (PROTECTED APPS)",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        ),
                        color = TextMuted
                    )
                }

                Text(
                    text = "${uiState.whitelistedPackages.size} Protected",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    ),
                    color = NeonCyan
                )
            }
        }

        item {
            Text(
                text = "Whitelisted apps will NEVER be closed during RAM Boost. Protect messengers, Discord calls, or music streams while playing BGMI:",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
        }

        // Whitelist toggles for background processes & preset apps
        items(uiState.runningProcesses) { proc ->
            val isWhitelisted = uiState.whitelistedPackages.contains(proc.packageName)

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(CyberCard)
                    .border(
                        1.dp,
                        if (isWhitelisted) NeonCyan.copy(alpha = 0.6f) else CyberCardBorder,
                        RoundedCornerShape(12.dp)
                    )
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = proc.appName,
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = TextPrimary
                            )
                            if (isWhitelisted) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(NeonCyan.copy(alpha = 0.2f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "PROTECTED",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Black,
                                            fontSize = 9.sp
                                        ),
                                        color = NeonCyan
                                    )
                                }
                            }
                        }
                        Text(
                            text = "${proc.packageName} • ${proc.memoryUsageMb} MB",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontFamily = FontFamily.Monospace,
                                fontSize = 10.sp
                            ),
                            color = TextMuted
                        )
                    }

                    Switch(
                        checked = isWhitelisted,
                        onCheckedChange = { checked ->
                            viewModel.toggleAppWhitelist(proc.packageName, proc.appName, checked)
                            Toast.makeText(
                                context,
                                if (checked) "Added ${proc.appName} to whitelist" else "Removed ${proc.appName} from whitelist",
                                Toast.LENGTH_SHORT
                            ).show()
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.Black,
                            checkedTrackColor = NeonCyan
                        ),
                        modifier = Modifier.testTag("whitelist_switch_${proc.packageName.replace('.', '_')}")
                    )
                }
            }
        }
    }
}
