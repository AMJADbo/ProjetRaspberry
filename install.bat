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

echo Verification de MySQL via UwAmp...
if exist "C:\logiciels\UwAmp\bin\database\mysql-5.7.11\bin\mysql.exe" (
    set MYSQL=C:\logiciels\UwAmp\bin\database\mysql-5.7.11\bin\mysql.exe
) else (
    echo MySQL introuvable. Verifiez que UwAmp est installe dans C:\logiciels\UwAmp
    pause
    exit /b 1
)

echo MySQL trouve !

echo Creation de la base de donnees... Le mot de passe est "root"
set /p MDPROOT=root : 
%MYSQL% -u root -p%MDPROOT% -e "CREATE DATABASE IF NOT EXISTS rfid_payment CHARACTER SET utf8mb4;"

echo Lancement du Backend...
cd Backend
call mvnw.cmd spring-boot:run
pause