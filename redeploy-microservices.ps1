# Script para eliminar y redesplegar microservicios
# Excluye servicios de infraestructura (Eureka, Config Server, API Gateway, Zipkin) y Jenkins

Write-Host "==============================================`nEliminando y redesplegando microservicios`n==============================================" -ForegroundColor Cyan

# Verificar estado de Minikube
$minikubeStatus = minikube status | Out-String
if ($minikubeStatus -notmatch "host: Running") {
    Write-Host "Error: Minikube no está en ejecución. Por favor inicia Minikube primero." -ForegroundColor Red
    exit 1
} else {
    Write-Host "Minikube está en ejecución" -ForegroundColor Green
}

# Lista de microservicios a eliminar y redesplegar (SIN infraestructura ni Jenkins)
$microservices = @(
    "user-service",
    "product-service", 
    "order-service",
    "payment-service",
    "shipping-service",
    "favourite-service",
    "proxy-client",
    "service-discovery",  # Eureka
    "cloud-config",       # Config Server
    "api-gateway",        # API Gateway
    "zipkin"             # Zipkin
)

# Servicios de infraestructura que NO se tocan
$infrastructureServices = @(

    "jenkins"             # Jenkins
)

Write-Host "`nServicios de infraestructura que se mantendrán:" -ForegroundColor Yellow
foreach ($infra in $infrastructureServices) {
    Write-Host "  - $infra" -ForegroundColor Yellow
}

Write-Host "`n==============================================`nEliminando microservicios`n==============================================" -ForegroundColor Red

foreach ($service in $microservices) {
    Write-Host "Eliminando deployment de $service..." -ForegroundColor Red
    kubectl delete deployment $service --ignore-not-found=true
    
    Write-Host "Eliminando service de $service..." -ForegroundColor Red  
    kubectl delete service $service --ignore-not-found=true
    
    Start-Sleep -Seconds 1
}

Write-Host "`nEsperando 10 segundos para que los pods terminen completamente..." -ForegroundColor Yellow
Start-Sleep -Seconds 10

# Verificar que los microservicios fueron eliminados
Write-Host "`nVerificando eliminación de microservicios..." -ForegroundColor Yellow
$remainingPods = kubectl get pods -n jenkins | Out-String
foreach ($service in $microservices) {
    if ($remainingPods -match $service) {
        Write-Host "Advertencia: Aún hay pods de $service ejecutándose" -ForegroundColor Yellow
    } else {
        Write-Host "$service eliminado correctamente" -ForegroundColor Green
    }
}

