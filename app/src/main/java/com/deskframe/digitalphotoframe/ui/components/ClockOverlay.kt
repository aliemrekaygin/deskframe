package com.deskframe.digitalphotoframe.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ClockOverlay(
    modifier: Modifier = Modifier
) {
    var currentTime by remember { mutableStateOf("") }
    var currentDate by remember { mutableStateOf("") }
    var pixelShiftOffset by remember { mutableIntStateOf(0) }

    val trLocale = remember { Locale("tr", "TR") }
    val timeFormat = remember { SimpleDateFormat("HH:mm", trLocale) }
    val dateFormat = remember { SimpleDateFormat("EEEE, d MMMM", trLocale) }

    LaunchedEffect(Unit) {
        var minuteCounter = 0
        while (true) {
            val now = Date()
            currentTime = timeFormat.format(now)
            currentDate = dateFormat.format(now)

            // Pixel shift every 5 minutes to prevent AMOLED burn-in on Samsung A8 (2018)
            pixelShiftOffset = (minuteCounter % 8) * 2
            minuteCounter++

            delay(1000L)
        }
    }

    if (currentTime.isNotEmpty()) {
        Box(
            modifier = modifier
                .offset(x = pixelShiftOffset.dp, y = (pixelShiftOffset / 2).dp)
                .background(
                    color = Color.Black.copy(alpha = 0.35f),
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(horizontal = 20.dp, vertical = 10.dp)
        ) {
            Column {
                Text(
                    text = currentTime,
                    fontSize = 44.sp,
                    fontWeight = FontWeight.Light,
                    color = Color.White,
                    style = TextStyle(
                        shadow = Shadow(
                            color = Color.Black.copy(alpha = 0.7f),
                            offset = Offset(2f, 2f),
                            blurRadius = 6f
                        )
                    )
                )
                Text(
                    text = currentDate,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White.copy(alpha = 0.9f),
                    style = TextStyle(
                        shadow = Shadow(
                            color = Color.Black.copy(alpha = 0.7f),
                            offset = Offset(1f, 1f),
                            blurRadius = 4f
                        )
                    )
                )
            }
        }
    }
}
