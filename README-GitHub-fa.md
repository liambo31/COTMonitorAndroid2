# ساخت APK با GitHub Actions، بدون Android Studio و بدون Gradle Wrapper

این پروژه Workflow آماده دارد و Gradle را مستقیم روی Runner گیت‌هاب نصب می‌کند؛ بنابراین نبودن یا خراب بودن `gradle-wrapper.jar` جلوی Build را نمی‌گیرد.

## مرحله ۱: ساخت Repository

1. وارد https://github.com شو و وارد حساب خودت شو.
2. روی **New** بزن.
3. نام Repository را مثلاً `cot-monitor-android` بگذار.
4. برای شروع، **Private** را انتخاب کن و روی **Create repository** بزن.

## مرحله ۲: آپلود فایل‌ها

1. ZIP این پروژه را باز کن.
2. وارد پوشه‌ی `COTMonitorAndroid` شو.
3. همه‌ی محتویات همین پوشه را در Repository گیت‌هاب آپلود کن، نه خود پوشه‌ی بیرونی را.
4. در ریشه‌ی Repository باید این مسیرها را ببینی:

```text
.github/workflows/android-apk.yml
app/
build.gradle.kts
settings.gradle.kts
gradle.properties
```

وجود `gradle/wrapper/gradle-wrapper.jar` خوب است، اما این Workflow برای Build به آن وابسته نیست.

### آپلود از طریق سایت GitHub

در صفحه‌ی Repository روی **Add file > Upload files** بزن، فایل‌ها را انتخاب کن و پایین صفحه روی **Commit changes** بزن. اگر سایت GitHub آپلود پوشه‌ی `.github` را نشان نداد، اول این مسیر را با **Add file > Create new file** بساز:

```text
.github/workflows/android-apk.yml
```

بعد محتوای فایل را از همین پروژه کپی کن.

## مرحله ۳: اجرای Build

1. در Repository روی تب **Actions** بزن.
2. از ستون چپ Workflow با نام **Build Android APK** را انتخاب کن.
3. روی **Run workflow** بزن.
4. شاخه‌ی `main` را انتخاب کن و دوباره **Run workflow** را بزن.

Workflow با JDK 17، Android SDK و Gradle 8.7 اجرا می‌شود و سپس `assembleDebug` را می‌سازد.

## مرحله ۴: دانلود APK

1. وقتی اجرای Workflow سبز شد، روی همان اجرای موفق کلیک کن.
2. پایین صفحه، بخش **Artifacts** را پیدا کن.
3. فایل `cot-monitor-debug-apk.zip` را دانلود کن.
4. ZIP را باز کن و `app-debug.apk` را روی گوشی نصب کن.

برای نصب، Android ممکن است اجازه‌ی **Install unknown apps** را برای مرورگر یا File Manager بخواهد.

## اگر Workflow را در Actions نمی‌بینی

- نام فایل باید دقیقاً `.github/workflows/android-apk.yml` باشد.
- فایل باید در شاخه‌ی `main` Commit شده باشد.
- صفحه‌ی Actions را یک‌بار Refresh کن.
- در Settings > Actions > General مطمئن شو Actions برای Repository فعال است.

## اگر Build شکست خورد

از صفحه‌ی اجرای Workflow روی مرحله‌ی قرمز کلیک کن و متن خطا را بفرست. مهم‌ترین بخش معمولاً اولین خط `FAILURE` یا `What went wrong` است، نه کل Log.

این نسخه APK Debug می‌سازد و برای تست مناسب است. برای APK Release امضاشده باید Keystore شخصی در GitHub Secrets اضافه شود؛ Keystore یا رمزها را داخل Repository عمومی قرار نده.
