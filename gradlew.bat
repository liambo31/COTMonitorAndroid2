@echo off
setlocal EnableExtensions EnableDelayedExpansion
set "APP_HOME=%~dp0"
set "PROPS=%APP_HOME%gradle\wrapper\gradle-wrapper.properties"
for /f "tokens=1,* delims==" %%A in ('findstr /b "distributionUrl=" "%PROPS%"') do set "DIST_URL=%%B"
set "DIST_URL=%DIST_URL:\:=:%"
for /f "tokens=1 delims=/" %%A in ("%DIST_URL:*gradle-=%%") do set "VERSION=%%A"
set "VERSION=%VERSION:-bin.zip=%"
if not defined GRADLE_USER_HOME set "GRADLE_USER_HOME=%USERPROFILE%\.gradle"
set "DIST_DIR=%GRADLE_USER_HOME%\wrapper\dists\cot-monitor\gradle-%VERSION%"
set "GRADLE_BIN=%DIST_DIR%\bin\gradle.bat"
if exist "%GRADLE_BIN%" goto run
set "ZIP=%GRADLE_USER_HOME%\wrapper\dists\cot-monitor\gradle-%VERSION%-bin.zip"
if not exist "%GRADLE_USER_HOME%\wrapper\dists\cot-monitor" mkdir "%GRADLE_USER_HOME%\wrapper\dists\cot-monitor"
if exist "%ZIP%" goto extract
where curl.exe >NUL 2>&1 || (echo ERROR: curl.exe is required to download Gradle.& exit /b 1)
echo Downloading Gradle %VERSION% from %DIST_URL%
curl.exe -fL --retry 3 --connect-timeout 10 -o "%ZIP%" "%DIST_URL%"
if errorlevel 1 exit /b 1
:extract
where powershell.exe >NUL 2>&1 || (echo ERROR: PowerShell is required to install Gradle.& exit /b 1)
set "TMP_DIR=%DIST_DIR%.tmp"
if exist "%TMP_DIR%" rmdir /s /q "%TMP_DIR%"
powershell.exe -NoProfile -ExecutionPolicy Bypass -Command "Expand-Archive -LiteralPath '%ZIP%' -DestinationPath '%TMP_DIR%' -Force"
if errorlevel 1 exit /b 1
for /d %%D in ("%TMP_DIR%\gradle-*") do move "%%D" "%DIST_DIR%" >NUL
rmdir /s /q "%TMP_DIR%"
if not exist "%GRADLE_BIN%" (echo ERROR: Gradle archive did not contain bin\gradle.bat.& exit /b 1)
:run
call "%GRADLE_BIN%" %*
exit /b %ERRORLEVEL%
