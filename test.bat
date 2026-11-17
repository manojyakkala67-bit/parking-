@echo off
echo ========================================================
echo  RUNNING SMART PARKING UNIT TESTS (JUnit 4.13.2)
echo ========================================================

if not exist "target\test-classes" mkdir target\test-classes

dir /s /b src\test\java\*.java > test_sources.tmp
javac -encoding UTF-8 -cp "target\classes;lib\mysql-connector-j.jar;lib\junit-4.13.2.jar;lib\hamcrest-2.2.jar" -d target\test-classes @test_sources.tmp
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Test compilation failed!
    del test_sources.tmp
    pause
    exit /b %ERRORLEVEL%
)
del test_sources.tmp

java -cp "target\classes;target\test-classes;lib\mysql-connector-j.jar;lib\junit-4.13.2.jar;lib\hamcrest-2.2.jar" org.junit.runner.JUnitCore com.smartparking.FeeCalculationTest com.smartparking.ValidationUtilTest com.smartparking.PasswordUtilTest

echo.
pause
