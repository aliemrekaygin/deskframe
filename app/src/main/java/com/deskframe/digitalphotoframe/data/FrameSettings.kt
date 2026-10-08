package com.deskframe.digitalphotoframe.data

enum class TransitionType(val titleTr: String) {
    KEN_BURNS("Canlı Çerçeve (Ken Burns)"),
    CROSSFADE("Yumuşak Kararma (Crossfade)"),
    SLIDE("Sayfa Kaydırma (Slide)"),
    ZOOM_FADE("Büyüyen Geçiş (Zoom & Fade)"),
    FLIP_3D("3 Boyutlu Döndürme (3D Flip)"),
    RANDOM("Rastgele Efekt (Tümü)")
}

enum class ScaleMode(val titleTr: String, val descTr: String) {
    FIT_AMBIENT("Akıllı Sığdır (Kırpmasız)", "Fotoğrafın tamamı görünür, kenarlara ortam arka planı konur"),
    FIT_BLACK("Tam Sığdır (Siyah Kenarlık)", "Fotoğrafın tamamı görünür, kenarlar siyah kalır"),
    CROP("Ekrana Doldur (Kırp)", "Ekranı tamamen kaplayacak şekilde kenarlardan kırpar")
}

enum class OrientationMode(val titleTr: String) {
    AUTO("Otomatik (Cihaz Sensörü)"),
    LANDSCAPE("Yatay (Masa Standı)"),
    PORTRAIT("Dikey")
}

data class FrameSettings(
    val photoUris: List<String> = emptyList(),
    val photoCrops: Map<String, PhotoCropConfig> = emptyMap(),
    val intervalSeconds: Int = 10,
    val transitionType: TransitionType = TransitionType.KEN_BURNS,
    val scaleMode: ScaleMode = ScaleMode.FIT_AMBIENT,
    val orientationMode: OrientationMode = OrientationMode.AUTO,
    val isShuffle: Boolean = false,
    val showClock: Boolean = false, // Varsayılan olarak kapalı (kullanıcı isteği)
    val isNightDim: Boolean = false
)
