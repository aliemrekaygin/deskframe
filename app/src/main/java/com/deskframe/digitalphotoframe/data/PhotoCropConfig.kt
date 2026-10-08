package com.deskframe.digitalphotoframe.data

import org.json.JSONObject

data class PhotoCropConfig(
    val scale: Float = 1.0f,
    val panX: Float = 0.0f, // Normalized offset relative to container width (-1.0 to 1.0)
    val panY: Float = 0.0f  // Normalized offset relative to container height (-1.0 to 1.0)
) {
    val isDefault: Boolean
        get() = scale == 1.0f && panX == 0.0f && panY == 0.0f

    fun toJson(): JSONObject {
        return JSONObject().apply {
            put("scale", scale.toDouble())
            put("panX", panX.toDouble())
            put("panY", panY.toDouble())
        }
    }

    companion object {
        fun fromJson(json: JSONObject): PhotoCropConfig {
            return PhotoCropConfig(
                scale = json.optDouble("scale", 1.0).toFloat(),
                panX = json.optDouble("panX", 0.0).toFloat(),
                panY = json.optDouble("panY", 0.0).toFloat()
            )
        }
    }
}
