# run-locust-test.ps1

# Configuración
$locustFile = "locustfile.py"
$users = 5
$spawnRate = 2
$duration = "30s"

# Guardar la ubicación actual
$originalLocation = Get-Location
Write-Host "Ubicación original: $originalLocation"

# Cambiar al directorio locust
$locustDir = "locust"
if (Test-Path $locustDir) {
    Set-Location $locustDir
    Write-Host "Cambiando al directorio: $locustDir"
} else {
    Write-Host "Error: No se encontró el directorio $locustDir"
    exit 1
}

# Obtener timestamp (formato: yyyyMMdd-HHmmss)
$timestamp = Get-Date -Format "yyyyMMdd-HHmmss"

# Nombre base del archivo de resultados (siempre el mismo)
$reportPrefix = "load_test_report"

# Ejecutar Locust
Write-Host "Ejecutando Locust con $users usuarios por $duration..."
python -m locust -f $locustFile --headless -u $users -r $spawnRate -t $duration --csv=$reportPrefix

Write-Host "`nInforme guardado como:"
Write-Host "$reportPrefix`_stats.csv"
Write-Host "$reportPrefix`_failures.csv"
Write-Host "$reportPrefix`_stats_history.csv"

# Regresar a la ubicación original
Set-Location $originalLocation
Write-Host "`nRegresando a la ubicación original: $originalLocation"
