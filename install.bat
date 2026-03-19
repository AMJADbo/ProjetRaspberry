@echo off
chcp 65001
echo Installation RFID Payment System

echo Verification de Java...
java -version
if %errorlevel% neq 0 (
    echo Java non installe. Allez sur https://adoptium.net
    pause
    exit /b 1
)

echo Verification de MySQL...
mysql --version
if %errorlevel% neq 0 (
    echo MySQL non installe.
    pause
    exit /b 1
)

echo Creation de la base de donnees...
mysql -u root -e "CREATE DATABASE IF NOT EXISTS rfid_payment;"

echo Lancement du Backend...
cd Backend
call mvnw.cmd spring-boot:run
pause