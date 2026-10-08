package com.deskframe.digitalphotoframe.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AspectRatio
import androidx.compose.material.icons.filled.Crop
import androidx.compose.material.icons.filled.FitScreen
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.deskframe.digitalphotoframe.data.ScaleMode

@Composable
fun ScaleModeSelectionDialog(
    currentScaleMode: ScaleMode,
    onSelectScaleMode: (ScaleMode) -> Unit,
    onDismiss: () -> Unit
) {
    val modes = listOf(
        Triple(
            ScaleMode.FIT_AMBIENT,
            "Akıllı Sığdır (Önerilen)",
            "Fotoğrafın tamamı gösterilir (hiç kırpılmaz), kenarlara ortam arka planı eklenir."
        ),
        Triple(
            ScaleMode.FIT_BLACK,
            "Tam Sığdır (Siyah Kenar)",
            "Fotoğrafın tamamı gösterilir, kenarlar siyah kalır (AMOLED pilleri kapatır)."
        ),
        Triple(
            ScaleMode.CROP,
            "Ekrana Doldur (Kırp)",
            "Fotoğraf tüm ekranı dolduracak şekilde kenarlardan kırpılır."
        )
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Fotoğraf Sığdırma / Kırpma",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color.White
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Fotoğrafların ekranda nasıl yerleşeceğini seçin:",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 13.sp
                )

                modes.forEach { (mode, title, desc) ->
                    val isSelected = currentScaleMode == mode
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelectScaleMode(mode) },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) Color(0xFF1E88E5).copy(alpha = 0.25f) else Color(0xFF282828)
                        ),
                        border = if (isSelected) BorderStroke(1.5.dp, Color(0xFF1E88E5)) else null
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = when (mode) {
                                    ScaleMode.FIT_AMBIENT -> Icons.Default.AspectRatio
                                    ScaleMode.FIT_BLACK -> Icons.Default.FitScreen
                                    ScaleMode.CROP -> Icons.Default.Crop
                                },
                                contentDescription = null,
                                tint = if (isSelected) Color(0xFF1E88E5) else Color.White.copy(alpha = 0.8f),
                                modifier = Modifier.size(24.dp)
                            )

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = title,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 14.sp,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = desc,
                                    fontSize = 11.sp,
                                    color = Color.White.copy(alpha = 0.65f),
                                    lineHeight = 15.sp
                                )
                            }

                            RadioButton(
                                selected = isSelected,
                                onClick = { onSelectScaleMode(mode) },
                                colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF1E88E5))
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Tamam", color = Color(0xFF1E88E5), fontWeight = FontWeight.Bold)
            }
        },
        containerColor = Color(0xFF1E1E1E),
        shape = RoundedCornerShape(20.dp)
    )
}
