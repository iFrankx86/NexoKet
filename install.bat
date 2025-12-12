@echo off
REM Script de instalación y configuración rápida para NexoKet (Windows)
REM Ejecutar como: install.bat

echo ======================================================
echo    NexoKet - Script de Instalacion Automatica
echo    Sistema de Gestion de Bodega - UTP 2025
echo ======================================================
echo.

REM Verificar Java
echo [1/6] Verificando Java...
java -version >nul 2>&1
if errorlevel 1 (
    echo [ERROR] Java no esta instalado o no esta en PATH
    echo Por favor instala Java 17 o superior desde: https://adoptium.net/
    pause
    exit /b 1
)
echo [OK] Java encontrado
java -version
echo.

REM Verificar Maven
echo [2/6] Verificando Maven...
mvn -version >nul 2>&1
if errorlevel 1 (
    echo [ERROR] Maven no esta instalado o no esta en PATH
    echo Por favor instala Maven desde: https://maven.apache.org/download.cgi
    pause
    exit /b 1
)
echo [OK] Maven encontrado
mvn -version
echo.

REM Crear directorios necesarios
echo [3/6] Creando directorios...
if not exist "logs" mkdir logs
if not exist "backups" mkdir backups
echo [OK] Directorios creados
echo.

REM Configurar variables de entorno (temporal para esta sesión)
echo [4/6] Configurando variables de entorno...
echo.
echo IMPORTANTE: Necesitas configurar tus credenciales de MongoDB
echo.
set /p MONGODB_URI="Ingresa tu MONGODB_URI (o Enter para usar default): "
if "%MONGODB_URI%"=="" set MONGODB_URI=mongodb+srv://u22311204:frank@cluster0.zknqatn.mongodb.net/

set /p MONGODB_DATABASE="Ingresa el nombre de la base de datos (o Enter para 'NexoKet'): "
if "%MONGODB_DATABASE%"=="" set MONGODB_DATABASE=NexoKet

echo.
echo Variables configuradas:
echo   MONGODB_URI=%MONGODB_URI%
echo   MONGODB_DATABASE=%MONGODB_DATABASE%
echo.

REM Compilar proyecto
echo [5/6] Compilando proyecto con Maven...
echo Esto puede tardar algunos minutos...
call mvn clean install -DskipTests
if errorlevel 1 (
    echo [ERROR] Fallo la compilacion
    pause
    exit /b 1
)
echo [OK] Proyecto compilado exitosamente
echo.

REM Ejecutar tests (opcional)
echo [6/6] Deseas ejecutar los tests? (s/n)
set /p RUN_TESTS="Respuesta: "
if /i "%RUN_TESTS%"=="s" (
    echo Ejecutando tests...
    call mvn test
    if errorlevel 1 (
        echo [WARNING] Algunos tests fallaron, pero la instalacion continua
    ) else (
        echo [OK] Todos los tests pasaron
    )
)
echo.

echo ======================================================
echo    INSTALACION COMPLETADA
echo ======================================================
echo.
echo Para ejecutar NexoKet, usa uno de estos comandos:
echo.
echo   1. Con Maven:
echo      mvn exec:java -Dexec.mainClass="utp.edu.pe.nexoket.NexoKet"
echo.
echo   2. Con JAR:
echo      java -jar target\nexoket-1.0.0-SNAPSHOT.jar
echo.
echo Logs se guardaran en: .\logs\
echo Backups se guardaran en: .\backups\
echo.
echo Para mas informacion, consulta README.md o QUICKSTART.md
echo.
pause

REM Preguntar si desea ejecutar ahora
echo.
echo Deseas ejecutar NexoKet ahora? (s/n)
set /p RUN_NOW="Respuesta: "
if /i "%RUN_NOW%"=="s" (
    echo.
    echo Ejecutando NexoKet...
    echo.
    call mvn exec:java -Dexec.mainClass="utp.edu.pe.nexoket.NexoKet"
)
