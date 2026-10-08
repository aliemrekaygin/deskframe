# Dijital Fotoğraf Çerçevesi (Digital Photo Frame)
Samsung Galaxy A8 (2018) ve tüm Android cihazlar için optimize edilmiş masaüstü dijital fotoğraf çerçevesi uygulaması.

---

## 🌟 Özellikler

1. **Kesintisiz Ekran Deneyimi (`Keep Screen On`)**:
   - Uygulama açık olduğu sürece telefonun ekranı asla kapanmaz (`FLAG_KEEP_SCREEN_ON`).
   - Tam ekran modu (Immersive Fullscreen) ile durum çubuğu ve alt navigasyon tuşları gizlenir, gerçek bir çerçeve görünümü sunar.

2. **Zengin Geçiş Efektleri**:
   - **Canlı Çerçeve (Ken Burns Effect)**: Fotoğraflara hafif ve yavaş yakınlaşma/kaydırma hareketi kazandırır. Fotoğraflar canlı görünür ve Super AMOLED ekranda piksel yanmasını (burn-in) önler.
   - **Crossfade**: Yumuşak kararma ve açılma geçişi.
   - **Slide**: Sayfa kaydırma efekti.
   - **Zoom & Fade**: Büyüyerek odaklanan geçiş efekti.
   - **3D Flip**: 3 boyutlu derinlikli dönüş efekti.
   - **Rastgele Efekt**: Her fotoğrafta farklı bir geçiş uygular.

3. **Esnek Fotoğraf İçe Aktarma & Depolama**:
   - Tek tek veya çoklu fotoğraf seçimi.
   - Tüm bir klasörü/albümü seçebilme (`OpenDocumentTree`).
   - Cihaz yeniden başlasa bile izinlerin kaybolmaması için kalıcı URI izin yönetimi (`takePersistableUriPermission`).
   - Seçilen fotoğrafları küçük resim (thumbnail) listesinde görebilme ve istediklerinizi silebilme.

4. **Kişiselleştirilebilir Ayarlar**:
   - **Geçiş Süresi**: 3 sn, 5 sn, 10 sn, 15 sn, 30 sn, 1 dk, 5 dk aralıkları.
   - **Format Uyumu**: Ekranı Doldur (Crop) veya Tam Sığdır (Fit).
   - **Karışık Oynatma (Shuffle)**: Fotoğrafları rastgele sırayla gösterme.
   - **Şık Saat & Tarih Göstergesi**: Türkçe gün ve tarih bilgisi içeren, AMOLED yanmasını önlemek için periyodik olarak piksel kaydıran yarı şeffaf saat widget'ı.
   - **Gece Karartma Modu**: Masada gece göz almaması için ekran parlaklığını karartan gece modu.

5. **Kullanıcı Arayüzü (HUD Kontrolleri)**:
   - Ekrana dokunulduğunda oynat/duraklat, önceki/sonraki, ayarlar ve gece modu butonları belirir; 4 saniye sonra otomatik gizlenir.
   - Sağa/sola parmak kaydırma (swipe) hareketiyle fotoğraflar arasında hızlı geçiş.

---

## 📱 Samsung Galaxy A8 (2018) İçin Özel Tavsiyeler

1. **Pil Koruması (Battery Protection)**:
   - Cihaz sürekli şarjda masada kalacağı için pil sağlığını korumak adına telefonun `Ayarlar > Cihaz Bakımı > Pil` menüsünden pil koruma veya akıllı şarj seçeneklerini etkinleştirebilirsiniz.
2. **AMOLED Ekran Yanması (Burn-in) Koruması**:
   - Uygulamada varsayılan olarak açık gelen **Canlı Çerçeve (Ken Burns)** modu ve **Piksel Kaydırmalı Saat**, AMOLED piksellerinin sabit kalıp yanmasını engellemek için özel olarak tasarlanmıştır.

---

## 🚀 Kurulum & Çalıştırma

### Yöntem 1: Android Studio ile Çalıştırma
1. Depoyu klonlayın, Android Studio'yu açın ve klonladığınız proje klasörünü seçin.
2. Android cihazınızı (ör. Samsung A8) USB hata ayıklama (USB Debugging) modunda bilgisayara bağlayın.
3. Üst menüdeki yeşil **Run (Çalıştır)** butonuna basarak uygulamayı yükleyin.

### Yöntem 2: APK Olarak Doğrudan Telefona Yükleme
Uygulama derlendiğinde üretilen APK dosyasını doğrudan telefonunuza gönderebilirsiniz:
- APK Konumu: `app/build/outputs/apk/debug/app-debug.apk`
- Telefonunuza USB kablosu, Google Drive veya WhatsApp/Telegram ile atıp yükleyebilirsiniz.
