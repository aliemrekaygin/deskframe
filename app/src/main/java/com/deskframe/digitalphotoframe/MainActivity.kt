package com.deskframe.digitalphotoframe

import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.deskframe.digitalphotoframe.data.OrientationMode
import com.deskframe.digitalphotoframe.data.SettingsRepository
import com.deskframe.digitalphotoframe.ui.PhotoFrameScreen
import com.deskframe.digitalphotoframe.ui.theme.DigitalPhotoFrameTheme
import com.deskframe.digitalphotoframe.util.PhotoStorageHelper
import com.deskframe.digitalphotoframe.util.UriHelper
import com.deskframe.digitalphotoframe.util.WindowInsetsHelper
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var repository: SettingsRepository

    // ActivityResult launcher for Google Photos
    private val pickGooglePhotosLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK && result.data != null) {
            val uris = mutableListOf<Uri>()
            result.data?.clipData?.let { clipData ->
                for (i in 0 until clipData.itemCount) {
                    uris.add(clipData.getItemAt(i).uri)
                }
            } ?: result.data?.data?.let { uri ->
                uris.add(uri)
            }

            if (uris.isNotEmpty()) {
                lifecycleScope.launch {
                    val persisted = PhotoStorageHelper.importAndPersistUris(applicationContext, uris)
                    repository.addPhotoUris(persisted)
                }
            }
        }
    }

    // ActivityResult launcher for standard device gallery
    private val pickGalleryLauncher = registerForActivityResult(
        ActivityResultContracts.OpenMultipleDocuments()
    ) { uris ->
        if (uris.isNotEmpty()) {
            lifecycleScope.launch {
                val persisted = PhotoStorageHelper.importAndPersistUris(applicationContext, uris)
                repository.addPhotoUris(persisted)
            }
        }
    }

    // ActivityResult launcher for picking an entire folder
    private val pickFolderLauncher = registerForActivityResult(
        ActivityResultContracts.OpenDocumentTree()
    ) { treeUri ->
        if (treeUri != null) {
            lifecycleScope.launch {
                val uris = UriHelper.listImagesFromTreeUri(this@MainActivity, treeUri).map { Uri.parse(it) }
                if (uris.isNotEmpty()) {
                    val persisted = PhotoStorageHelper.importAndPersistUris(applicationContext, uris)
                    repository.addPhotoUris(persisted)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Keep screen on and hide status/navigation bars
        WindowInsetsHelper.applyImmersiveMode(this)

        repository = SettingsRepository(applicationContext)

        setContent {
            DigitalPhotoFrameTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.Black
                ) {
                    val settings by repository.settings.collectAsStateWithLifecycle()

                    // Dynamically apply orientation changes
                    LaunchedEffect(settings.orientationMode) {
                        requestedOrientation = when (settings.orientationMode) {
                            OrientationMode.AUTO -> ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR
                            OrientationMode.LANDSCAPE -> ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
                            OrientationMode.PORTRAIT -> ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
                        }
                    }

                    PhotoFrameScreen(
                        settings = settings,
                        onUpdateSettings = { updated ->
                            repository.updateSettings(updated)
                        },
                        onSaveCrop = { uri, crop ->
                            repository.savePhotoCrop(uri, crop)
                        },
                        onRemovePhoto = { uri ->
                            repository.removePhotoUri(uri)
                        },
                        onClearAllPhotos = {
                            repository.clearAllPhotos()
                        },
                        onPickGooglePhotos = {
                            launchGooglePhotosPicker()
                        },
                        onPickGalleryPhotos = {
                            pickGalleryLauncher.launch(arrayOf("image/*"))
                        },
                        onPickFolder = {
                            pickFolderLauncher.launch(null)
                        }
                    )
                }
            }
        }
    }

    private fun launchGooglePhotosPicker() {
        val googlePhotosIntent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "image/*"
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            setPackage("com.google.android.apps.photos")
        }
        try {
            pickGooglePhotosLauncher.launch(googlePhotosIntent)
        } catch (_: Exception) {
            // Fallback if Google Photos is unavailable
            val fallback = Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "image/*"
                putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            }
            pickGooglePhotosLauncher.launch(fallback)
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            WindowInsetsHelper.applyImmersiveMode(this)
        }
    }
}
