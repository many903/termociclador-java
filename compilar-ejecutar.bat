@echo off
title Termociclador - Con Imagen Funcionando
echo ===============================================
echo    SISTEMA TERMOCICLADOR - CON IMAGEN
echo ===============================================
echo.

echo Verificando estructura de carpetas...
if not exist resources ( 
    echo Creando carpeta resources...
    mkdir resources 
    echo COLOCAR el archivo grafica1.jpg en la carpeta resources
)

echo Mostrando contenido de resources:
dir resources 2>nul || echo Carpeta resources esta vacia o no existe

echo.
echo Compilando codigo fuente...
javac -cp ".;lib/jSerialComm-2.11.4.jar" TermocicladorUI.java

if %errorlevel% neq 0 (
    echo.
    echo ERROR: Compilacion fallida!
    echo.
    echo Posibles soluciones:
    echo 1. Verifique que jSerialComm-2.11.4.jar esta en la carpeta lib
    echo 2. Verifique que tiene Java instalado
    pause
    exit /b 1
)

echo.
echo Compilacion exitosa! Ejecutando aplicacion...
echo ===============================================
echo Si no ve la imagen, revise la consola para mensajes de depuracion
java -cp ".;lib/jSerialComm-2.11.4.jar" TermocicladorUI

echo.
echo Aplicacion terminada.
pause