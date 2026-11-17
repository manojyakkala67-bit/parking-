@echo off
echo ========================================================
echo  LAUNCHING SMART PARKING MANAGEMENT SYSTEM
echo ========================================================

java -cp "target\classes;lib\mysql-connector-j.jar" com.smartparking.Main

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo If compilation is needed first, run: compile.bat
    pause
)
