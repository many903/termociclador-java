@echo off
title Termociclador - Con Imagen Automática
echo ===============================================
echo    SISTEMA TERMOCICLADOR - CON IMAGEN AUTOMATICA
echo ===============================================
echo.

echo Compilando codigo fuente...
javac -cp ".;lib/jSerialComm-2.11.4.jar" TermocicladorUI.java

if %errorlevel% neq 0 (
    echo.
    echo ERROR: Compilacion fallida!
    echo Verifique que jSerialComm-2.11.4.jar esta en la carpeta lib
    pause
    exit /b 1
)

echo.
echo Compilacion exitosa! Ejecutando aplicacion...
echo ===============================================
echo La aplicacion creara automaticamente una imagen
echo si no encuentra grafica1.jpg
echo.
java -cp ".;lib/jSerialComm-2.11.4.jar" TermocicladorUI

echo.
echo Aplicacion terminada.
pause