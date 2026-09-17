@echo off
title Pagamentos App

echo ========================================
echo      INICIANDO PAGAMENTOS-APP
echo ========================================
echo.

echo Iniciando Spring Boot...
start "Spring Boot - Pagamentos API" cmd /k "cd /d backend && mvnw.cmd spring-boot:run"

echo.
echo Aguardando o Spring Boot iniciar...
timeout /t 10 /nobreak >nul

echo.
echo Iniciando Angular...
start "Angular - Pagamentos Frontend" cmd /k "cd /d frontend && npx ng serve"

echo.
echo Aguardando o Angular iniciar...
timeout /t 10 /nobreak >nul

echo.
echo Abrindo navegador...
start http://localhost:4200

echo.
echo PAGAMENTOS-APP INICIADO.