@echo off
echo ========================================
echo Réinitialisation de la base de données
echo ========================================
echo.

set PSQL="C:\Program Files\PostgreSQL\18\bin\psql.exe"

echo Suppression de la base de données existante...
%PSQL% -U postgres -c "DROP DATABASE IF EXISTS okane_transfer;"

if %errorlevel% neq 0 (
    echo Erreur lors de la suppression de la base de données.
    echo Verifiez que PostgreSQL est demarre et que le mot de passe est correct.
    pause
    exit /b 1
)

echo Création d'une nouvelle base de données...
%PSQL% -U postgres -c "CREATE DATABASE okane_transfer;"

if %errorlevel% neq 0 (
    echo Erreur lors de la creation de la base de données.
    pause
    exit /b 1
)

echo.
echo ========================================
echo Base de données réinitialisée avec succès!
echo Vous pouvez maintenant démarrer Tomcat.
echo ========================================
pause
