@echo off
title Ejecutar TermocicladorUI
echo Ejecutando TermocicladorUI...

if not exist "bin\TermocicladorUI.class" (
    echo ❌ Error: No se encuentra el archivo compilado
    echo Ejecute primero: compilar-ejecutar.bat
    pause
    exit /b 1
)

java -cp "bin;lib\jSerialComm-2.11.4.jar" TermocicladorUI

echo.
echo Aplicacion terminada
pause