@echo off
title Termociclador - Compilacion y Ejecucion
echo ===============================================
echo    SISTEMA TERMOCICLADOR - MODO PRODUCCION
echo ===============================================
echo.

echo Limpiando compilaciones anteriores...
if exist bin ( rmdir /s /q bin )
mkdir bin

echo Copiando recursos...
if not exist bin\resources ( mkdir bin\resources )
if exist resources ( xcopy /Y resources\* bin\resources\ )

echo Compilando codigo fuente...
javac -d bin -cp "src;lib/jSerialComm-2.11.4.jar" src/TermocicladorUI.java

if %errorlevel% neq 0 (
    echo.
    echo ERROR: Compilacion fallida!
    echo Revise los mensajes de error arriba.
    pause
    exit /b 1
)

echo.
echo Compilacion exitosa! Ejecutando aplicacion...
echo ===============================================
java -cp "bin;lib/jSerialComm-2.11.4.jar" TermocicladorUI

echo.
echo Aplicacion terminada.
pause