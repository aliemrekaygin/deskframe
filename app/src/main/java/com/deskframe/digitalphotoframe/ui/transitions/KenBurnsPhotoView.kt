package com.deskframe.digitalphotoframe.ui.transitions

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.deskframe.digitalphotoframe.data.PhotoCropConfig
import com.deskframe.digitalphotoframe.data.ScaleMode
import kotlin.random.Random

@Composable
fun KenBurnsPhotoView(
    photoUri: String,
    scaleMode: ScaleMode,
    durationSeconds: Int,
    cropConfig: PhotoCropConfig? = null,
    modifier: Modifier = Modifier
) {
    val durationMs = (durationSeconds * 1000).coerceAtLeast(3000)
    val context = LocalContext.current
    val hasCustomCrop = cropConfig != null && !cropConfig.isDefault

    // Subtle scale & translation animations
    val scaleAnim = remember(photoUri) { Animatable(1.0f) }
    val transXAnim = remember(photoUri) { Animatable(0f) }
    val transYAnim = remember(photoUri) { Animatable(0f) }

    // Foreground subtle breathing (kept very subtle)
    val fgScaleAnim = remember(photoUri) { Animatable(1.0f) }

    val targetBgScale = remember(photoUri) { 1.05f + Random.nextFloat() * 0.05f }
    val targetBgTransX = remember(photoUri) { (Random.nextFloat() * 2f - 1f) * 25f }
    val targetBgTransY = remember(photoUri) { (Random.nextFloat() * 2f - 1f) * 25f }

    val targetFgScale = remember(photoUri) {
        if (scaleMode == ScaleMode.CROP) 1.04f else 1.015f
    }

    LaunchedEffect(photoUri) {
        scaleAnim.animateTo(
            targetValue = targetBgScale,
            animationSpec = tween(durationMillis = durationMs, easing = LinearEasing)
        )
    }

    LaunchedEffect(photoUri) {
        transXAnim.animateTo(
            targetValue = targetBgTransX,
            animationSpec = tween(durationMillis = durationMs, easing = LinearEasing)
        )
    }

    LaunchedEffect(photoUri) {
        transYAnim.animateTo(
            targetValue = targetBgTransY,
            animationSpec = tween(durationMillis = durationMs, easing = LinearEasing)
        )
    }

    LaunchedEffect(photoUri) {
        fgScaleAnim.animateTo(
            targetValue = targetFgScale,
            animationSpec = tween(durationMillis = durationMs, easing = LinearEasing)
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .clipToBounds()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        if (hasCustomCrop) {
            // Render exact user framing!
            val crop = cropConfig!!

            // Ambient background if FIT_AMBIENT
            if (scaleMode == ScaleMode.FIT_AMBIENT && crop.scale <= 1.2f) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(photoUri)
                        .crossfade(400)
                        .build(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            scaleX = scaleAnim.value * 1.1f
                            scaleY = scaleAnim.value * 1.1f
                            translationX = transXAnim.value
                            translationY = transYAnim.value
                        }
                        .alpha(0.35f)
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.45f))
                )
            }

            // Foreground photo positioned exactly as user framed
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(photoUri)
                    .crossfade(400)
                    .build(),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        val subtleBreath = 1.0f + (fgScaleAnim.value - 1.0f) * 0.5f
                        scaleX = crop.scale * subtleBreath
                        scaleY = crop.scale * subtleBreath
                        translationX = crop.panX * size.width
                        translationY = crop.panY * size.height
                    }
            )
        } else {
            // Default framing according to scaleMode
            when (scaleMode) {
                ScaleMode.FIT_AMBIENT -> {
                    // Background Layer: Ambient dimmed image
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(photoUri)
                            .crossfade(400)
                            .build(),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer {
                                scaleX = scaleAnim.value * 1.1f
                                scaleY = scaleAnim.value * 1.1f
                                translationX = transXAnim.value
                                translationY = transYAnim.value
                            }
                            .alpha(0.35f)
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.45f))
                    )

                    // Foreground: Uncropped pristine photo
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(photoUri)
                            .crossfade(400)
                            .build(),
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer {
                                scaleX = fgScaleAnim.value
                                scaleY = fgScaleAnim.value
                            }
                    )
                }

                ScaleMode.FIT_BLACK -> {
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(photoUri)
                            .crossfade(400)
                            .build(),
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer {
                                scaleX = fgScaleAnim.value
                                scaleY = fgScaleAnim.value
                            }
                    )
                }

                ScaleMode.CROP -> {
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(photoUri)
                            .crossfade(400)
                            .build(),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer {
                                scaleX = scaleAnim.value
                                scaleY = scaleAnim.value
                                translationX = transXAnim.value
                                translationY = transYAnim.value
                            }
                    )
                }
            }
        }
    }
}
