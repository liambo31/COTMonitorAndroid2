# ساخت APK بدون Android Studio با Codemagic

این پروژه فایل `codemagic.yaml` آماده دارد و با **JDK 17، Gradle 8.7، AGP 8.5.2 و Kotlin 2.0.21**، نسخه‌ی Debug APK می‌سازد.

## مسیر پیشنهادی، GitHub سپس Codemagic

### 1. ساخت Repository در GitHub

1. وارد [github.com](https://github.com) شو و یک Repository جدید بساز، مثلاً `cot-monitor-android`.
2. Repository را روی حالت Private بگذار.
3. فایل ZIP را از حالت فشرده خارج کن. مهم است که فایل `codemagic.yaml` در **ریشه‌ی Repository** باشد، نه داخل یک پوشه‌ی اضافه.
4. همه‌ی فایل‌های داخل پوشه‌ی `COTMonitorAndroid` را داخل Repository آپلود کن و Commit بزن.
5. مطمئن شو این مسیرها وجود دارند:

```text
codemagic.yaml
settings.gradle.kts
build.gradle.kts
gradlew
gradlew.bat
gradle/wrapper/gradle-wrapper.properties
app/build.gradle.kts
```

### 2. اتصال به Codemagic

1. به [codemagic.io/start](https://codemagic.io/start/) برو و با GitHub وارد شو.
2. روی **Add application** بزن.
3. GitHub را انتخاب کن و دسترسی Codemagic به Repository را تأیید کن.
4. Repository `cot-monitor-android` و شاخه‌ی `main` را انتخاب کن.
5. نوع پروژه را **Android / Native Android** انتخاب کن.
6. اگر گزینه‌ی Workflow file یا YAML آمد، استفاده از `codemagic.yaml` را انتخاب کن.
7. برنامه را اضافه کن.

Codemagic فایل `codemagic.yaml` را از ریشه‌ی Repository می‌خواند. این فایل به‌صورت خودکار Android SDK پروژه را تنظیم می‌کند، Java 17 را انتخاب می‌کند و APK را با `assembleDebug` می‌سازد.

### 3. گرفتن APK

1. داخل صفحه‌ی برنامه در Codemagic روی **Start new build** بزن.
2. Workflow را `cot-monitor-debug` انتخاب کن.
3. شاخه‌ی `main` را انتخاب کن.
4. روی Start بزن.
5. بعد از سبز شدن Build، بخش **Artifacts** را باز کن.
6. فایل `app-debug.apk` را دانلود کن و روی گوشی نصب کن.

برای نصب APK خارج از Google Play، اندروید ممکن است اجازه‌ی نصب از همین مرورگر یا File Manager را بخواهد. فقط همان APK ساخته‌شده از Repository خودت را نصب کن.

## اگر Build خطا داد

- `codemagic.yaml` باید دقیقاً در ریشه‌ی Repository باشد.
- فایل `gradlew` باید در GitHub آپلود شده و executable باشد. Workflow خودش `chmod +x ./gradlew` را اجرا می‌کند.
- اگر Gradle می‌گوید SDK پیدا نشد، خط مربوط به `local.properties` را حذف نکن.
- خطاهای کامل را از بخش Build logs کپی کن. معمولاً با همان چند خط اول می‌شود مشکل را پیدا کرد.

## نسخه‌ی Release و امضای APK

این Workflow عمداً **Debug APK** می‌سازد تا سریع تست شود. برای Release باید یک Android Keystore در Codemagic اضافه کنیم و کلیدها را به‌صورت Secret نگه داریم؛ Keystore را داخل GitHub یا چت آپلود نکن.

## محدودیت فعلی برنامه

این APK هنوز به CQG وصل نیست و با شبیه‌ساز و ثبت دستی تیک کار می‌کند. اتصال واقعی CQG مرحله‌ی جداست و به دسترسی رسمی CQG WebAPI نیاز دارد.
