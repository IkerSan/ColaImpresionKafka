# Script para limpiar, formatear y arrancar Kafka en Windows (Modo KRaft)
# Guardar en la carpeta del proyecto o en el escritorio

$KAFKA_DIR = "C:\KAFKA\kafka_2.13-4.1.1"
$LOG_DIR = "C:\tmp\kraft-combined-logs"

Write-Host "--- PASO 1: LIMPIEZA ---" -ForegroundColor Cyan
if (Test-Path $LOG_DIR) {
    Write-Host "Eliminando logs antiguos en $LOG_DIR..."
    Remove-Item -Path $LOG_DIR -Recurse -Force
    Write-Host "Logs eliminados." -ForegroundColor Green
} else {
    Write-Host "No se encontraron logs antiguos. Todo limpio." -ForegroundColor Green
}

Write-Host "`n--- PASO 2: FORMATEO ---" -ForegroundColor Cyan
Set-Location $KAFKA_DIR

# Generamos el UUID con PowerShell
$UUID = [Guid]::NewGuid().ToString()
Write-Host "Generado UUID (PowerShell): $UUID"

Write-Host "Formateando almacenamiento..."
# AÑADIDO: --standalone para arreglar el error de quorum voters
$formatCmd = ".\bin\windows\kafka-storage.bat format -t $UUID -c .\config\server.properties --standalone"
Invoke-Expression $formatCmd

if ($?) {
    Write-Host "Formateo exitoso." -ForegroundColor Green
} else {
    Write-Host "Error al formatear. Revisa los permisos." -ForegroundColor Red
    # Continuamos porque a veces devuelve falso positivo en PowerShell, pero el log de arriba dirá si falló
}

Write-Host "`n--- PASO 3: ARRANCANDO KAFKA ---" -ForegroundColor Cyan
Write-Host "Kafka va a arrancar ahora. NO CIERRES ESTA VENTANA." -ForegroundColor Yellow
. .\bin\windows\kafka-server-start.bat .\config\server.properties
