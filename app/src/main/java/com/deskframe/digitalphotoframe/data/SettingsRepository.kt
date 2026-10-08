package com.deskframe.digitalphotoframe.data

import android.content.Context
import android.content.SharedPreferences
import com.deskframe.digitalphotoframe.util.PhotoStorageHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject

class SettingsRepository(private val context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("digital_frame_prefs", Context.MODE_PRIVATE)

    private val _settings = MutableStateFlow(loadSettingsInternal())
    val settings: StateFlow<FrameSettings> = _settings.asStateFlow()

    private fun loadSettingsInternal(): FrameSettings {
        val uriJson = prefs.getString(KEY_PHOTO_URIS, "[]") ?: "[]"
        val uris = mutableListOf<String>()
        try {
            val jsonArray = JSONArray(uriJson)
            for (i in 0 until jsonArray.length()) {
                val uri = jsonArray.optString(i)
                if (uri.isNotBlank()) {
                    uris.add(uri)
                }
            }
        } catch (_: Exception) {
        }

        val cropsJson = prefs.getString(KEY_PHOTO_CROPS, "{}") ?: "{}"
        val crops = mutableMapOf<String, PhotoCropConfig>()
        try {
            val jsonObject = JSONObject(cropsJson)
            val keys = jsonObject.keys()
            while (keys.hasNext()) {
                val key = keys.next()
                val cropObj = jsonObject.optJSONObject(key)
                if (cropObj != null) {
                    crops[key] = PhotoCropConfig.fromJson(cropObj)
                }
            }
        } catch (_: Exception) {
        }

        val interval = prefs.getInt(KEY_INTERVAL, 10)
        val transitionName = prefs.getString(KEY_TRANSITION, TransitionType.KEN_BURNS.name)
        val transitionType = try {
            TransitionType.valueOf(transitionName ?: TransitionType.KEN_BURNS.name)
        } catch (_: Exception) {
            TransitionType.KEN_BURNS
        }

        val scaleName = prefs.getString(KEY_SCALE, ScaleMode.FIT_AMBIENT.name)
        val scaleMode = try {
            ScaleMode.valueOf(scaleName ?: ScaleMode.FIT_AMBIENT.name)
        } catch (_: Exception) {
            ScaleMode.FIT_AMBIENT
        }

        val orientationName = prefs.getString(KEY_ORIENTATION, OrientationMode.AUTO.name)
        val orientationMode = try {
            OrientationMode.valueOf(orientationName ?: OrientationMode.AUTO.name)
        } catch (_: Exception) {
            OrientationMode.AUTO
        }

        val isShuffle = prefs.getBoolean(KEY_SHUFFLE, false)
        val showClock = prefs.getBoolean(KEY_SHOW_CLOCK, false)
        val isNightDim = prefs.getBoolean(KEY_NIGHT_DIM, false)

        return FrameSettings(
            photoUris = uris,
            photoCrops = crops,
            intervalSeconds = interval,
            transitionType = transitionType,
            scaleMode = scaleMode,
            orientationMode = orientationMode,
            isShuffle = isShuffle,
            showClock = showClock,
            isNightDim = isNightDim
        )
    }

    fun updateSettings(newSettings: FrameSettings) {
        val jsonArray = JSONArray()
        newSettings.photoUris.forEach { jsonArray.put(it) }

        val cropsJson = JSONObject()
        newSettings.photoCrops.forEach { (uri, crop) ->
            cropsJson.put(uri, crop.toJson())
        }

        prefs.edit()
            .putString(KEY_PHOTO_URIS, jsonArray.toString())
            .putString(KEY_PHOTO_CROPS, cropsJson.toString())
            .putInt(KEY_INTERVAL, newSettings.intervalSeconds)
            .putString(KEY_TRANSITION, newSettings.transitionType.name)
            .putString(KEY_SCALE, newSettings.scaleMode.name)
            .putString(KEY_ORIENTATION, newSettings.orientationMode.name)
            .putBoolean(KEY_SHUFFLE, newSettings.isShuffle)
            .putBoolean(KEY_SHOW_CLOCK, newSettings.showClock)
            .putBoolean(KEY_NIGHT_DIM, newSettings.isNightDim)
            .apply()

        _settings.value = newSettings
    }

    fun savePhotoCrop(uri: String, crop: PhotoCropConfig) {
        val current = _settings.value
        val updatedCrops = current.photoCrops.toMutableMap()
        if (crop.isDefault) {
            updatedCrops.remove(uri)
        } else {
            updatedCrops[uri] = crop
        }
        updateSettings(current.copy(photoCrops = updatedCrops))
    }

    fun resetPhotoCrop(uri: String) {
        val current = _settings.value
        val updatedCrops = current.photoCrops.toMutableMap()
        updatedCrops.remove(uri)
        updateSettings(current.copy(photoCrops = updatedCrops))
    }

    fun addPhotoUris(newUris: List<String>) {
        val current = _settings.value
        val updatedList = (current.photoUris + newUris).distinct()
        updateSettings(current.copy(photoUris = updatedList))
    }

    fun removePhotoUri(uri: String) {
        val current = _settings.value
        PhotoStorageHelper.deleteLocalPhoto(uri)
        val updatedList = current.photoUris.filter { it != uri }
        val updatedCrops = current.photoCrops.toMutableMap().apply { remove(uri) }
        updateSettings(current.copy(photoUris = updatedList, photoCrops = updatedCrops))
    }

    fun clearAllPhotos() {
        val current = _settings.value
        PhotoStorageHelper.clearAllImportedPhotos(context)
        updateSettings(current.copy(photoUris = emptyList(), photoCrops = emptyMap()))
    }

    companion object {
        private const val KEY_PHOTO_URIS = "key_photo_uris"
        private const val KEY_PHOTO_CROPS = "key_photo_crops"
        private const val KEY_INTERVAL = "key_interval"
        private const val KEY_TRANSITION = "key_transition"
        private const val KEY_SCALE = "key_scale"
        private const val KEY_ORIENTATION = "key_orientation"
        private const val KEY_SHUFFLE = "key_shuffle"
        private const val KEY_SHOW_CLOCK = "key_show_clock"
        private const val KEY_NIGHT_DIM = "key_night_dim"
    }
}
