# Reinicio de microservicios para asegurar registro correcto en Eureka
# Este script reinicia selectivamente los pods de microservicios para asegurar
# que se registren correctamente en el service-discovery (Eureka)

Write-Host "==============================================`nReiniciando microservicios para registro en Eureka`n==============================================" -ForegroundColor Cyan

# Verificar estado de Minikube
$minikubeStatus = minikube status | Out-String
if ($minikubeStatus -notmatch "host: Running") {
    Write-Host "Minikube no está en ejecución. Iniciando Minikube..." -ForegroundColor Yellow
    minikube start --cpus=no-limit --memory=no-limit
    
    # Esperar un tiempo para que Minikube esté completamente listo
    Write-Host "Esperando que Minikube esté completamente listo..." -ForegroundColor Yellow
    Start-Sleep -Seconds 20
} else {
    Write-Host "Minikube ya está en ejecución" -ForegroundColor Green
}

# Verificar si los servicios de infraestructura están en ejecución
Write-Host "`nVerificando disponibilidad de servicios de infraestructura..." -ForegroundColor Yellow

# Función para verificar si un pod está listo
function Test-PodReady {
    param (
        [string]$podName
    )
      $podStatus = kubectl get pods -n jenkins | Select-String -Pattern $podName
    return ($podStatus -match "1/1") -and ($podStatus -match "Running")
}

# Lista de microservicios para reiniciar
$microservices = @(
    "cloud-config",
    "user-service",
    "product-service",
    "order-service",
    "payment-service",
    "shipping-service",
    "favourite-service",
    "proxy-client"
    
)

Write-Host "`n==============================================`nReiniciando microservicios`n==============================================" -ForegroundColor Cyan

foreach ($service in $microservices) {
    Write-Host "Reiniciando $service para asegurar registro en Eureka..." -ForegroundColor Yellow
    
    # Reiniciar el deployment
    kubectl rollout restart deployment $service -n jenkins
    
    # Esperar un momento entre reinicios para no sobrecargar el sistema
    Start-Sleep -Seconds 2
}

Write-Host "`nEsperando 30 segundos para que los microservicios completen el reinicio..." -ForegroundColor Yellow
Start-Sleep -Seconds 30

# Verificar estado final de los microservicios
Write-Host "`n==============================================`nVerificando estado de los microservicios`n==============================================" -ForegroundColor Cyan

$allMicroservicesReady = $true

foreach ($service in $microservices) {
    if (Test-PodReady -podName $service) {
        Write-Host "$service está LISTO y en ejecución." -ForegroundColor Green
    } else {
        Write-Host "$service NO ESTÁ LISTO. Revisa los logs con 'kubectl logs deployment/$service -n jenkins'." -ForegroundColor Red
        $allMicroservicesReady = $false
    }
}


# Mensaje final
if ($allMicroservicesReady) {
    Write-Host "`n==============================================`nTodos los microservicios han sido reiniciados y están en ejecución.`nDeberían estar correctamente registrados en Eureka.`n==============================================" -ForegroundColor Green
} else {
    Write-Host "`n==============================================`nAlgunos microservicios no están listos.`nRevisa los logs para más información.`n==============================================" -ForegroundColor Red
}

Write-Host "`nPara verificar el estado de los pods, ejecuta: kubectl get pods -n jenkins" -ForegroundColor Cyan
Write-Host "Para ver los logs de un servicio específico, ejecuta: kubectl logs deployment/<nombre-servicio> -n jenkins" -ForegroundColor Cyan
Write-Host "Para acceder a la UI de Eureka, ejecuta: kubectl port-forward service/service-discovery 8761:8761 -n jenkins`ny abre http://localhost:8761 en tu navegador" -ForegroundColor Cyan
