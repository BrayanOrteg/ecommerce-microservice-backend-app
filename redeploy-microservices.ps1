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
    "proxy-client"
)

# Servicios de infraestructura que NO se tocan
$infrastructureServices = @(
    "service-discovery",  # Eureka
    "cloud-config",       # Config Server
    "api-gateway",        # API Gateway
    "zipkin",             # Zipkin
    "jenkins"             # Jenkins
)

Write-Host "`nServicios de infraestructura que se mantendrán:" -ForegroundColor Yellow
foreach ($infra in $infrastructureServices) {
    Write-Host "  - $infra" -ForegroundColor Yellow
}

Write-Host "`n==============================================`nEliminando microservicios`n==============================================" -ForegroundColor Red

foreach ($service in $microservices) {
    Write-Host "Eliminando deployment de $service..." -ForegroundColor Red
    kubectl delete deployment $service -n jenkins --ignore-not-found=true
    
    Write-Host "Eliminando service de $service..." -ForegroundColor Red  
    kubectl delete service $service -n jenkins --ignore-not-found=true
    
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

Write-Host "`n==============================================`nRedesplegando microservicios`n==============================================" -ForegroundColor Green

# Redesplegar microservicios
foreach ($service in $microservices) {
    $yamlFile = "k8s/$service.yaml"
    
    if (Test-Path $yamlFile) {
        Write-Host "Desplegando $service desde $yamlFile..." -ForegroundColor Green
        kubectl apply -f $yamlFile
        Start-Sleep -Seconds 2
    } else {
        Write-Host "Advertencia: No se encontró el archivo $yamlFile" -ForegroundColor Yellow
    }
}

Write-Host "`nEsperando 30 segundos para que los microservicios se inicien..." -ForegroundColor Yellow
Start-Sleep -Seconds 30

# Función para verificar si un pod está listo
function Test-PodReady {
    param (
        [string]$podName
    )
    $podStatus = kubectl get pods -n jenkins | Select-String -Pattern $podName
    return ($podStatus -match "1/1") -and ($podStatus -match "Running")
}

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

# Verificar que la infraestructura sigue funcionando
Write-Host "`nVerificando que los servicios de infraestructura siguen funcionando..." -ForegroundColor Cyan
foreach ($infra in $infrastructureServices) {
    if (Test-PodReady -podName $infra) {
        Write-Host "$infra está funcionando correctamente." -ForegroundColor Green
    } else {
        Write-Host "Advertencia: $infra podría tener problemas." -ForegroundColor Yellow
    }
}

# Mensaje final
if ($allMicroservicesReady) {
    Write-Host "`n==============================================`nTodos los microservicios han sido redesplegados exitosamente.`nLos servicios de infraestructura se mantuvieron intactos.`n==============================================" -ForegroundColor Green
    
    Write-Host "`nComandos útiles:" -ForegroundColor Cyan
    Write-Host "- Ver todos los pods: kubectl get pods -n jenkins" -ForegroundColor White
    Write-Host "- Ver logs en tiempo real: kubectl logs deployment/<servicio> -n jenkins -f" -ForegroundColor White
    Write-Host "- Acceder a Eureka: kubectl port-forward service/service-discovery 8761:8761 -n jenkins" -ForegroundColor White
    Write-Host "- Acceder a API Gateway: kubectl port-forward service/api-gateway 8080:8080 -n jenkins" -ForegroundColor White
} else {
    Write-Host "`n==============================================`nAlgunos microservicios no están listos.`nRevisa los logs para más información.`n==============================================" -ForegroundColor Red
}
