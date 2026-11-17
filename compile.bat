@echo off
echo ========================================================
echo  SMART PARKING MANAGEMENT SYSTEM - COMPILATION SCRIPT
echo ========================================================

if not exist "target\classes" mkdir target\classes
if not exist "target\test-classes" mkdir target\test-classes

echo [1/3] Compiling Java source files...
dir /s /b src\main\java\*.java > sources.tmp
javac -encoding UTF-8 -cp "lib\mysql-connector-j.jar" -d target\classes @sources.tmp
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Compilation failed!
    del sources.tmp
    pause
    exit /b %ERRORLEVEL%
)
del sources.tmp

echo [2/3] Copying application resources...
xcopy /s /y /q src\main\resources target\classes >nul

echo [3/3] Packaging JAR artifact...
jar --create --file target\smart-parking-system-1.0.0.jar --main-class com.smartparking.Main -C target\classes .

echo ========================================================
echo  BUILD SUCCESSFUL! Artifact: target\smart-parking-system-1.0.0.jar
echo ========================================================
