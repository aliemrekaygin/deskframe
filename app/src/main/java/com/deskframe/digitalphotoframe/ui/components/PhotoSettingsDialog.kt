package com.deskframe.digitalphotoframe.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.deskframe.digitalphotoframe.data.FrameSettings
import com.deskframe.digitalphotoframe.data.OrientationMode
import com.deskframe.digitalphotoframe.data.ScaleMode
import com.deskframe.digitalphotoframe.data.TransitionType

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PhotoSettingsDialog(
    currentSettings: FrameSettings,
    onSave: (FrameSettings) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedInterval by remember { mutableIntStateOf(currentSettings.intervalSeconds) }
    var selectedTransition by remember { mutableStateOf(currentSettings.transitionType) }
    var selectedScaleMode by remember { mutableStateOf(currentSettings.scaleMode) }
    var selectedOrientation by remember { mutableStateOf(currentSettings.orientationMode) }
    var isShuffle by remember { mutableStateOf(currentSettings.isShuffle) }
    var showClock by remember { mutableStateOf(currentSettings.showClock) }
    var isNightDim by remember { mutableStateOf(currentSettings.isNightDim) }

    val intervalOptions = listOf(
        5 to "5 sn",
        10 to "10 sn",
        15 to "15 sn",
        30 to "30 sn",
        60 to "1 dk",
        120 to "2 dk",
        300 to "5 dk",
        600 to "10 dk",
        1800 to "30 dk",
        3600 to "1 saat"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Çerçeve Ayarları",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = Color.White
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Section: Ekran Yönü (Orientation)
                Column {
                    Text(
                        text = "Ekran Yönü (Döndürme)",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        OrientationMode.values().forEach { mode ->
                            FilterChip(
                                selected = selectedOrientation == mode,
                                onClick = { selectedOrientation = mode },
                                label = { Text(mode.titleTr, fontSize = 12.sp) }
                            )
                        }
                    }
                }

                HorizontalDivider(color = Color.White.copy(alpha = 0.1f))

                // Section: Fotoğraf Ekranda Kalma Süresi (Interval)
                Column {
                    Text(
                        text = "Fotoğraf Ekranda Kalma Süresi",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        intervalOptions.forEach { (seconds, label) ->
                            FilterChip(
                                selected = selectedInterval == seconds,
                                onClick = { selectedInterval = seconds },
                                label = { Text(label, fontSize = 12.sp) }
                            )
                        }
                    }
                }

                HorizontalDivider(color = Color.White.copy(alpha = 0.1f))

                // Section: Sığdırma / Kırpma Formatı (ScaleMode)
                Column {
                    Text(
                        text = "Fotoğraf Sığdırma & Kırpma Formatı",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    ScaleMode.values().forEach { mode ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedScaleMode = mode }
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            FilterChip(
                                selected = selectedScaleMode == mode,
                                onClick = { selectedScaleMode = mode },
                                label = { Text(mode.titleTr, fontSize = 12.sp) }
                            )
                        }
                    }
                }

                HorizontalDivider(color = Color.White.copy(alpha = 0.1f))

                // Section: Geçiş Efektleri
                Column {
                    Text(
                        text = "Geçiş Efekti",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        TransitionType.values().forEach { transition ->
                            FilterChip(
                                selected = selectedTransition == transition,
                                onClick = { selectedTransition = transition },
                                label = { Text(transition.titleTr, fontSize = 12.sp) }
                            )
                        }
                    }
                }

                HorizontalDivider(color = Color.White.copy(alpha = 0.1f))

                // Section: Switches
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Karışık Sıralama (Shuffle)", color = Color.White, fontSize = 14.sp)
                    Switch(checked = isShuffle, onCheckedChange = { isShuffle = it })
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Saat ve Tarih Göster", color = Color.White, fontSize = 14.sp)
                    Switch(checked = showClock, onCheckedChange = { showClock = it })
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Gece Karartma Modu", color = Color.White, fontSize = 14.sp)
                    Switch(checked = isNightDim, onCheckedChange = { isNightDim = it })
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onSave(
                        currentSettings.copy(
                            intervalSeconds = selectedInterval,
                            transitionType = selectedTransition,
                            scaleMode = selectedScaleMode,
                            orientationMode = selectedOrientation,
                            isShuffle = isShuffle,
                            showClock = showClock,
                            isNightDim = isNightDim
                        )
                    )
                }
            ) {
                Text("Kaydet", color = Color(0xFF1E88E5), fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Vazgeç", color = Color.White.copy(alpha = 0.7f))
            }
        },
        containerColor = Color(0xFF1E1E1E),
        shape = RoundedCornerShape(24.dp)
    )
}
