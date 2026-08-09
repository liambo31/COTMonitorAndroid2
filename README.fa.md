# COT Monitor برای اندروید، JDK 17

این نسخه برای Build با **JDK 17** تنظیم شده است.

## نسخه‌های Build

- Android Gradle Plugin: `8.5.2`
- Gradle Wrapper: `8.7`
- Kotlin: `2.0.21`
- Gradle JDK: `17`
- Kotlin/Android bytecode target: Java 17

## تنظیم Android Studio

1. Android Studio را باز کن.
2. برو به `Settings > Build, Execution, Deployment > Build Tools > Gradle`.
3. در `Gradle JDK` گزینه‌ی JDK 17 را انتخاب کن.
4. پروژه را Sync کن.
5. برای Build از `./gradlew assembleDebug` در Linux/macOS یا `gradlew.bat assembleDebug` در Windows استفاده کن.

برای Build به JDK 17 و Android SDK نیاز است. Gradle Wrapper در اولین اجرا Gradle 8.7 را از آدرس رسمی دانلود می‌کند.

اتصال واقعی CQG هنوز جداست و به مجوز رسمی API، `client_app_id` و `private_label` نیاز دارد. رمزها را داخل کد یا چت قرار نده.

Foreground Service از سرویس معمولی مقاوم‌تر است، اما Battery Optimization بعضی گوشی‌ها ممکن است آن را متوقف کند. Battery Saver این اپ را خاموش کن و گوشی را به شارژر و اینترنت پایدار وصل نگه دار.

## Cloud build

برای ساخت بدون Android Studio، فایل codemagic.yaml و راهنمای README-Codemagic-fa.md را ببین.
