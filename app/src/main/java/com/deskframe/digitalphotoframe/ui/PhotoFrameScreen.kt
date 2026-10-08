package com.deskframe.digitalphotoframe.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.PhotoAlbum
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.deskframe.digitalphotoframe.data.FrameSettings
import com.deskframe.digitalphotoframe.data.OrientationMode
import com.deskframe.digitalphotoframe.data.PhotoCropConfig
import com.deskframe.digitalphotoframe.data.ScaleMode
import com.deskframe.digitalphotoframe.data.TransitionType
import com.deskframe.digitalphotoframe.ui.components.ClockOverlay
import com.deskframe.digitalphotoframe.ui.components.ControlsOverlay
import com.deskframe.digitalphotoframe.ui.components.PhotoCropEditor
import com.deskframe.digitalphotoframe.ui.components.PhotoImportOptionsDialog
import com.deskframe.digitalphotoframe.ui.components.PhotoListDialog
import com.deskframe.digitalphotoframe.ui.components.PhotoSettingsDialog
import com.deskframe.digitalphotoframe.ui.components.ScaleModeSelectionDialog
import com.deskframe.digitalphotoframe.ui.transitions.KenBurnsPhotoView
import kotlinx.coroutines.delay
import kotlin.random.Random

@Composable
fun PhotoFrameScreen(
    settings: FrameSettings,
    onUpdateSettings: (FrameSettings) -> Unit,
    onSaveCrop: (String, PhotoCropConfig) -> Unit,
    onRemovePhoto: (String) -> Unit,
    onClearAllPhotos: () -> Unit,
    onPickGooglePhotos: () -> Unit,
    onPickGalleryPhotos: () -> Unit,
    onPickFolder: () -> Unit,
    modifier: Modifier = Modifier
) {
    var currentIndex by remember { mutableIntStateOf(0) }
    var isPlaying by remember { mutableStateOf(true) }
    var showControls by remember { mutableStateOf(false) }
    var showSettingsDialog by remember { mutableStateOf(false) }
    var showPhotoListDialog by remember { mutableStateOf(false) }
    var showImportDialog by remember { mutableStateOf(false) }
    var showScaleModeDialog by remember { mutableStateOf(false) }
    var editingCropUri by remember { mutableStateOf<String?>(null) }
    var lastInteractionTime by remember { mutableLongStateOf(System.currentTimeMillis()) }

    // Shuffled or sequential list
    val displayList = remember(settings.photoUris, settings.isShuffle) {
        if (settings.isShuffle) settings.photoUris.shuffled(Random(42)) else settings.photoUris
    }

    // Keep index within bounds
    LaunchedEffect(displayList.size) {
        if (displayList.isNotEmpty() && currentIndex >= displayList.size) {
            currentIndex = 0
        }
    }

    // Auto-advance slideshow timer based on intervalSeconds (paused while editing crop)
    LaunchedEffect(isPlaying, currentIndex, displayList.size, settings.intervalSeconds, editingCropUri) {
        if (isPlaying && displayList.isNotEmpty() && editingCropUri == null) {
            delay(settings.intervalSeconds * 1000L)
            currentIndex = (currentIndex + 1) % displayList.size
        }
    }

    // Auto-hide controls after 4 seconds of inactivity
    LaunchedEffect(showControls, lastInteractionTime) {
        if (showControls) {
            delay(4000L)
            showControls = false
        }
    }

    // Swipe navigation detection
    var totalDragDistance by remember { mutableStateOf(0f) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                showControls = !showControls
                lastInteractionTime = System.currentTimeMillis()
            }
            .pointerInput(displayList.size) {
                if (displayList.size > 1) {
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            if (totalDragDistance > 100f) {
                                // Swiped right -> Previous photo
                                currentIndex = if (currentIndex - 1 < 0) displayList.size - 1 else currentIndex - 1
                                lastInteractionTime = System.currentTimeMillis()
                            } else if (totalDragDistance < -100f) {
                                // Swiped left -> Next photo
                                currentIndex = (currentIndex + 1) % displayList.size
                                lastInteractionTime = System.currentTimeMillis()
                            }
                            totalDragDistance = 0f
                        },
                        onHorizontalDrag = { _, dragAmount ->
                            totalDragDistance += dragAmount
                        }
                    )
                }
            }
    ) {
        if (displayList.isEmpty()) {
            // Empty State Screen
            EmptyFrameView(
                onPickGooglePhotos = onPickGooglePhotos,
                onPickGallery = onPickGalleryPhotos,
                onPickFolder = onPickFolder
            )
        } else {
            val currentPhotoUri = displayList.getOrNull(currentIndex) ?: displayList.first()
            val currentCropConfig = settings.photoCrops[currentPhotoUri]

            // Determine active transition effect
            val effectiveTransition = if (settings.transitionType == TransitionType.RANDOM) {
                val nonRandom = listOf(
                    TransitionType.KEN_BURNS,
                    TransitionType.CROSSFADE,
                    TransitionType.SLIDE,
                    TransitionType.ZOOM_FADE
                )
                remember(currentIndex) { nonRandom.random() }
            } else {
                settings.transitionType
            }

            // Photo presentation layer
            if (effectiveTransition == TransitionType.KEN_BURNS) {
                AnimatedContent(
                    targetState = currentPhotoUri,
                    transitionSpec = {
                        fadeIn(animationSpec = tween(1000)) togetherWith
                                fadeOut(animationSpec = tween(1000))
                    },
                    label = "KenBurnsTransition"
                ) { targetUri ->
                    val targetCrop = settings.photoCrops[targetUri]
                    KenBurnsPhotoView(
                        photoUri = targetUri,
                        scaleMode = settings.scaleMode,
                        durationSeconds = settings.intervalSeconds,
                        cropConfig = targetCrop
                    )
                }
            } else {
                val transitionSpec: ContentTransform = when (effectiveTransition) {
                    TransitionType.SLIDE -> {
                        slideInHorizontally(animationSpec = tween(700)) { it } togetherWith
                                slideOutHorizontally(animationSpec = tween(700)) { -it }
                    }
                    TransitionType.ZOOM_FADE -> {
                        (scaleIn(initialScale = 0.88f, animationSpec = tween(800)) + fadeIn(tween(800))) togetherWith
                                (scaleOut(targetScale = 1.10f, animationSpec = tween(800)) + fadeOut(tween(800)))
                    }
                    TransitionType.FLIP_3D -> {
                        (slideInHorizontally(animationSpec = tween(600)) { it / 2 } +
                                scaleIn(initialScale = 0.92f, animationSpec = tween(600)) +
                                fadeIn(tween(600))) togetherWith
                                (slideOutHorizontally(animationSpec = tween(600)) { -it / 2 } +
                                        scaleOut(targetScale = 0.92f, animationSpec = tween(600)) +
                                        fadeOut(tween(600)))
                    }
                    else -> { // CROSSFADE
                        fadeIn(animationSpec = tween(800)) togetherWith
                                fadeOut(animationSpec = tween(800))
                    }
                }

                AnimatedContent(
                    targetState = currentPhotoUri,
                    transitionSpec = { transitionSpec },
                    label = "StandardPhotoTransition"
                ) { targetUri ->
                    val context = LocalContext.current
                    val cropConfig = settings.photoCrops[targetUri]
                    val hasCustomCrop = cropConfig != null && !cropConfig.isDefault

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clipToBounds()
                            .background(Color.Black),
                        contentAlignment = Alignment.Center
                    ) {
                        if (hasCustomCrop) {
                            // Render exact custom user crop
                            val crop = cropConfig!!

                            if (settings.scaleMode == ScaleMode.FIT_AMBIENT && crop.scale <= 1.2f) {
                                AsyncImage(
                                    model = ImageRequest.Builder(context)
                                        .data(targetUri)
                                        .crossfade(400)
                                        .build(),
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .alpha(0.35f)
                                )
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(Color.Black.copy(alpha = 0.45f))
                                )
                            }

                            AsyncImage(
                                model = ImageRequest.Builder(context)
                                    .data(targetUri)
                                    .crossfade(400)
                                    .build(),
                                contentDescription = null,
                                contentScale = ContentScale.Fit,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .graphicsLayer {
                                        scaleX = crop.scale
                                        scaleY = crop.scale
                                        translationX = crop.panX * size.width
                                        translationY = crop.panY * size.height
                                    }
                            )
                        } else {
                            // Default rendering
                            when (settings.scaleMode) {
                                ScaleMode.FIT_AMBIENT -> {
                                    // Background ambient layer
                                    AsyncImage(
                                        model = ImageRequest.Builder(context)
                                            .data(targetUri)
                                            .crossfade(400)
                                            .build(),
                                        contentDescription = null,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .alpha(0.35f)
                                    )
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(Color.Black.copy(alpha = 0.45f))
                                    )
                                    // Foreground uncropped photo
                                    AsyncImage(
                                        model = ImageRequest.Builder(context)
                                            .data(targetUri)
                                            .crossfade(400)
                                            .build(),
                                        contentDescription = null,
                                        contentScale = ContentScale.Fit,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                                ScaleMode.FIT_BLACK -> {
                                    AsyncImage(
                                        model = ImageRequest.Builder(context)
                                            .data(targetUri)
                                            .crossfade(400)
                                            .build(),
                                        contentDescription = null,
                                        contentScale = ContentScale.Fit,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                                ScaleMode.CROP -> {
                                    AsyncImage(
                                        model = ImageRequest.Builder(context)
                                            .data(targetUri)
                                            .crossfade(400)
                                            .build(),
                                        contentDescription = null,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Night Dim Overlay
            if (settings.isNightDim) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.65f))
                )
            }

            // Clock Overlay (Only if explicitly enabled by user)
            if (settings.showClock) {
                ClockOverlay(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(24.dp)
                )
            }

            // Floating Controls HUD Overlay
            ControlsOverlay(
                visible = showControls,
                currentIndex = currentIndex,
                totalCount = displayList.size,
                isPlaying = isPlaying,
                isNightDim = settings.isNightDim,
                currentScaleMode = settings.scaleMode,
                currentOrientation = settings.orientationMode,
                intervalSeconds = settings.intervalSeconds,
                onPrevious = {
                    currentIndex = if (currentIndex - 1 < 0) displayList.size - 1 else currentIndex - 1
                    lastInteractionTime = System.currentTimeMillis()
                },
                onNext = {
                    currentIndex = (currentIndex + 1) % displayList.size
                    lastInteractionTime = System.currentTimeMillis()
                },
                onTogglePlayPause = {
                    isPlaying = !isPlaying
                    lastInteractionTime = System.currentTimeMillis()
                },
                onToggleNightDim = {
                    onUpdateSettings(settings.copy(isNightDim = !settings.isNightDim))
                    lastInteractionTime = System.currentTimeMillis()
                },
                onToggleOrientation = {
                    val nextOrientation = when (settings.orientationMode) {
                        OrientationMode.AUTO -> OrientationMode.LANDSCAPE
                        OrientationMode.LANDSCAPE -> OrientationMode.PORTRAIT
                        OrientationMode.PORTRAIT -> OrientationMode.AUTO
                    }
                    onUpdateSettings(settings.copy(orientationMode = nextOrientation))
                    lastInteractionTime = System.currentTimeMillis()
                },
                onCycleInterval = {
                    val intervals = listOf(5, 10, 15, 30, 60, 300, 900)
                    val nextInterval = intervals.firstOrNull { it > settings.intervalSeconds } ?: intervals.first()
                    onUpdateSettings(settings.copy(intervalSeconds = nextInterval))
                    lastInteractionTime = System.currentTimeMillis()
                },
                onOpenScaleModeDialog = {
                    showScaleModeDialog = true
                    lastInteractionTime = System.currentTimeMillis()
                },
                onOpenSettings = {
                    showSettingsDialog = true
                    lastInteractionTime = System.currentTimeMillis()
                },
                onOpenPhotoList = {
                    showPhotoListDialog = true
                    lastInteractionTime = System.currentTimeMillis()
                },
                onAddPhotos = {
                    showImportDialog = true
                    lastInteractionTime = System.currentTimeMillis()
                },
                onEditCurrentCrop = {
                    editingCropUri = currentPhotoUri
                    lastInteractionTime = System.currentTimeMillis()
                }
            )
        }

        // Photo Crop Editor Overlay
        if (editingCropUri != null) {
            val uriToEdit = editingCropUri!!
            PhotoCropEditor(
                photoUri = uriToEdit,
                initialCrop = settings.photoCrops[uriToEdit] ?: PhotoCropConfig(),
                onSaveCrop = { newCrop ->
                    onSaveCrop(uriToEdit, newCrop)
                    editingCropUri = null
                },
                onDismiss = { editingCropUri = null }
            )
        }

        // Import Options Dialog
        if (showImportDialog) {
            PhotoImportOptionsDialog(
                onImportGooglePhotos = {
                    showImportDialog = false
                    onPickGooglePhotos()
                },
                onImportGallery = {
                    showImportDialog = false
                    onPickGalleryPhotos()
                },
                onImportFolder = {
                    showImportDialog = false
                    onPickFolder()
                },
                onDismiss = { showImportDialog = false }
            )
        }

        // Scale / Crop Mode Dialog
        if (showScaleModeDialog) {
            ScaleModeSelectionDialog(
                currentScaleMode = settings.scaleMode,
                onSelectScaleMode = { mode ->
                    onUpdateSettings(settings.copy(scaleMode = mode))
                    showScaleModeDialog = false
                },
                onDismiss = { showScaleModeDialog = false }
            )
        }

        // Settings Dialog
        if (showSettingsDialog) {
            PhotoSettingsDialog(
                currentSettings = settings,
                onSave = { updated ->
                    onUpdateSettings(updated)
                    showSettingsDialog = false
                },
                onDismiss = { showSettingsDialog = false }
            )
        }

        // Photo List Dialog
        if (showPhotoListDialog) {
            PhotoListDialog(
                photoUris = settings.photoUris,
                photoCrops = settings.photoCrops,
                onRemovePhoto = onRemovePhoto,
                onEditCrop = { uri ->
                    showPhotoListDialog = false
                    editingCropUri = uri
                },
                onClearAll = onClearAllPhotos,
                onDismiss = { showPhotoListDialog = false }
            )
        }
    }
}

@Composable
private fun EmptyFrameView(
    onPickGooglePhotos: () -> Unit,
    onPickGallery: () -> Unit,
    onPickFolder: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF101010))
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.PhotoAlbum,
                contentDescription = null,
                tint = Color(0xFF1E88E5),
                modifier = Modifier.size(64.dp)
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Dijital Fotoğraf Çerçevesi",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Fotoğraflarınızı içe aktararak çerçeveyi başlatın:",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Google Photos Button
                Button(
                    onClick = onPickGooglePhotos,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4285F4)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.width(260.dp)
                ) {
                    Icon(imageVector = Icons.Default.CloudDownload, contentDescription = null)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("Google Fotoğraflar'dan Ekle", fontWeight = FontWeight.SemiBold)
                }

                // Gallery Button
                Button(
                    onClick = onPickGallery,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E88E5)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.width(260.dp)
                ) {
                    Icon(imageVector = Icons.Default.AddPhotoAlternate, contentDescription = null)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("Cihaz Galerisinden Ekle", fontWeight = FontWeight.SemiBold)
                }

                // Folder Button
                OutlinedButton(
                    onClick = onPickFolder,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.width(260.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.FolderOpen,
                        contentDescription = null,
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("Tüm Klasörü Seç", color = Color.White)
                }
            }
        }
    }
}
