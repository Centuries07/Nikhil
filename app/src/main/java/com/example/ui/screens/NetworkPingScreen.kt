package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.NetworkCheck
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Speed
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
import com.example.model.DnsBenchmarkItem
import com.example.model.ServerPingResult
import com.example.ui.components.CyberActionButton
import com.example.ui.theme.*
import com.example.viewmodel.GameTurboUiState
import com.example.viewmodel.GameTurboViewModel

@Composable
fun NetworkPingScreen(
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
        // Main Live Ping Radar Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CyberDark),
                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(NeonLime, Color.Transparent))),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "BGMI LOW-LATENCY RADAR",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.2.sp
                                ),
                                color = NeonLime
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "India Game Clusters (AWS & Krafton Edge)",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextMuted
                            )
                        }

                        IconButton(
                            onClick = { viewModel.refreshPing() },
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(CyberCard)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Refresh Ping",
                                tint = NeonLime
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "CURRENT PING",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextMuted
                            )
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(
                                    text = "${uiState.fastestBgmiPing}",
                                    style = MaterialTheme.typography.displayMedium.copy(
                                        fontWeight = FontWeight.Black,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 44.sp
                                    ),
                                    color = if (uiState.fastestBgmiPing < 40) NeonLime else WarningYellow
                                )
                                Text(
                                    text = "ms",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = TextSecondary,
                                    modifier = Modifier.padding(bottom = 8.dp, start = 2.dp)
                                )
                            }
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "PING JITTER",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextMuted
                            )
                            Text(
                                text = "±${uiState.jitterMs} ms",
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                ),
                                color = NeonCyan
                            )
                            Text(
                                text = "Ultra Stable",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                color = NeonLime
                            )
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "PACKET LOSS",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextMuted
                            )
                            Text(
                                text = "${uiState.packetLossPercent}%",
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                ),
                                color = NeonLime
                            )
                            Text(
                                text = "Zero Drop",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                color = TextSecondary
                            )
                        }
                    }
                }
            }
        }

        // Network Stabilizer Mode Toggle
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(CyberCard)
                    .border(1.dp, if (uiState.networkStabilizerActive) NeonCyan else CyberCardBorder, RoundedCornerShape(14.dp))
                    .padding(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.NetworkCheck,
                                contentDescription = null,
                                tint = if (uiState.networkStabilizerActive) NeonCyan else TextMuted,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Anti-Jitter Network Stabilizer",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = TextPrimary
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Keeps Vivo T4 5G/Wi-Fi modem in active transmit state to eliminate dormancy delay spikes in BGMI hot drops.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }

                    Switch(
                        checked = uiState.networkStabilizerActive,
                        onCheckedChange = { viewModel.toggleNetworkStabilizer() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.Black,
                            checkedTrackColor = NeonCyan
                        ),
                        modifier = Modifier.testTag("network_stabilizer_switch")
                    )
                }
            }
        }

        // Live BGMI Server Ping List
        item {
            Text(
                text = "BGMI REGIONAL SERVERS",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                ),
                color = TextMuted
            )
        }

        items(uiState.pingResults) { result ->
            ServerPingRow(result)
        }

        // Benchmark DNS Button
        item {
            CyberActionButton(
                text = "Benchmark Gaming DNS Resolvers",
                icon = Icons.Default.Speed,
                primaryColor = NeonLime,
                secondaryColor = Color(0xFF009955),
                isLoading = uiState.isBenchmarkingDns,
                testTag = "benchmark_dns_button",
                onClick = {
                    viewModel.benchmarkDns()
                    Toast.makeText(context, "Benchmarking DNS latency in India...", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.fillMaxWidth()
            )
        }

        // DNS Recommendations & 1-tap copy
        item {
            Text(
                text = "ANTI-LAG PRIVATE DNS (BGMI OPTIMIZED)",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                ),
                color = TextMuted
            )
        }

        items(uiState.dnsList) { dns ->
            DnsItemRow(
                dns = dns,
                onCopy = {
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = ClipData.newPlainText("Private DNS", dns.privateDnsHostname)
                    clipboard.setPrimaryClip(clip)
                    Toast.makeText(context, "Copied '${dns.privateDnsHostname}' to clipboard!", Toast.LENGTH_SHORT).show()
                },
                onOpenSettings = {
                    try {
                        val intent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        try {
                            context.startActivity(Intent(Settings.ACTION_SETTINGS))
                        } catch (ignored: Exception) {}
                    }
                }
            )
        }
    }
}

@Composable
fun ServerPingRow(result: ServerPingResult) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (result.isFastest) CyberCardActive else CyberCard)
            .border(
                1.dp,
                if (result.isFastest) NeonLime else CyberCardBorder,
                RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = result.server.name,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimary
                    )
                    if (result.isFastest) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(NeonLime.copy(alpha = 0.2f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "FASTEST ROUTE",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Black,
                                    fontSize = 9.sp
                                ),
                                color = NeonLime
                            )
                        }
                    }
                }
                Text(
                    text = "${result.server.location} • ${result.status.label}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(result.status.colorHex)
                )
            }

            Text(
                text = "${result.pingMs} ms",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace
                ),
                color = Color(result.status.colorHex)
            )
        }
    }
}

@Composable
fun DnsItemRow(
    dns: DnsBenchmarkItem,
    onCopy: () -> Unit,
    onOpenSettings: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(CyberCard)
            .border(1.dp, if (dns.isFastest) NeonCyan else CyberCardBorder, RoundedCornerShape(14.dp))
            .padding(14.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = dns.name,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = TextPrimary
                        )
                        if (dns.isFastest) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(NeonCyan.copy(alpha = 0.2f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "LOWEST PING",
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
                        text = "Hostname: ${dns.privateDnsHostname}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Medium
                        ),
                        color = NeonCyan
                    )
                }

                if (dns.pingMs != null) {
                    Text(
                        text = "${dns.pingMs} ms",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace
                        ),
                        color = if (dns.isFastest) NeonCyan else TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = dns.description,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onCopy,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = NeonCyan),
                    border = ButtonDefaults.outlinedButtonBorder().copy(brush = Brush.horizontalGradient(listOf(NeonCyan, NeonCyan))),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Copy DNS")
                }

                OutlinedButton(
                    onClick = onOpenSettings,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Apply in OS")
                }
            }
        }
    }
}
