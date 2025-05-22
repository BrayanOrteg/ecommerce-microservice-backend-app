# Script para desplegar Jenkins en Kubernetes (Minikube)

param (
    [Parameter(Mandatory=$false)]
    [string]$Namespace = "default",
    
    [Parameter(Mandatory=$false)]
    [switch]$OpenBrowser = $true
)

Write-Host "===== DESPLEGANDO JENKINS EN KUBERNETES =====" -ForegroundColor Cyan

# Verificar si Minikube está corriendo
$minikubeStatus = minikube status 2>&1
if ($LASTEXITCODE -ne 0) {
    Write-Host "Minikube no está en ejecución. Iniciando Minikube..." -ForegroundColor Yellow
    minikube start --cpus=no-limit --memory=no-limit
}

# Si se especificó un namespace diferente a default, crearlo si no existe
if ($Namespace -ne "default") {
    $namespaceExists = kubectl get namespace $Namespace 2>&1
    if ($LASTEXITCODE -ne 0) {
        Write-Host "Creando namespace $Namespace..." -ForegroundColor Yellow
        kubectl create namespace $Namespace
    } else {
        Write-Host "Usando namespace existente: $Namespace" -ForegroundColor Yellow
    }
}

# Determinar qué archivos usar según el namespace
$rbacFile = if ($Namespace -eq "default") { "jenkins-rbac-default.yaml" } else { "jenkins-rbac.yaml" }
$deploymentFile = if ($Namespace -eq "default") { "jenkins-deployment-default.yaml" } else { "jenkins-deployment.yaml" }

# Desplegar recursos de Jenkins
Write-Host "Desplegando volumen persistente..." -ForegroundColor Yellow
kubectl apply -f k8s/jenkins-pv.yaml

Write-Host "Desplegando configuración RBAC..." -ForegroundColor Yellow
kubectl apply -f k8s/$rbacFile

Write-Host "Desplegando Jenkins..." -ForegroundColor Yellow
kubectl apply -f k8s/$deploymentFile

# Esperar a que Jenkins esté listo
Write-Host "Esperando a que Jenkins esté listo..." -ForegroundColor Yellow
$ready = $false
$attempts = 0
$maxAttempts = 30

while (-not $ready -and $attempts -lt $maxAttempts) {
    $attempts++
    Start-Sleep -Seconds 10
    
    $podStatus = kubectl get pods -n $Namespace -l app=jenkins -o jsonpath='{.items[0].status.phase}' 2>&1
    if ($LASTEXITCODE -eq 0 -and $podStatus -eq "Running") {
        $ready = $true
        Write-Host "Jenkins está listo." -ForegroundColor Green
    } else {
        Write-Host "Esperando a que Jenkins esté listo... Intento $attempts de $maxAttempts" -ForegroundColor Yellow
    }
}

if (-not $ready) {
    Write-Host "Jenkins no pudo iniciarse después de $maxAttempts intentos. Verifica los logs." -ForegroundColor Red
    exit 1
}

# Mostrar la URL de acceso
if ($Namespace -eq "default") {
    $jenkinsUrl = minikube service jenkins --url
    Write-Host "Jenkins está disponible en: $jenkinsUrl" -ForegroundColor Green
} else {
    $jenkinsUrl = minikube service jenkins -n $Namespace --url
    Write-Host "Jenkins está disponible en: $jenkinsUrl" -ForegroundColor Green
}

# Obtener la contraseña inicial
Write-Host "`nObteniendo contraseña inicial de administrador..." -ForegroundColor Yellow
if ($Namespace -eq "default") {
    $podName = kubectl get pods -l app=jenkins -o jsonpath='{.items[0].metadata.name}'
} else {
    $podName = kubectl get pods -n $Namespace -l app=jenkins -o jsonpath='{.items[0].metadata.name}'
}

Write-Host "Esperando a que el archivo de contraseña esté disponible..." -ForegroundColor Yellow
Start-Sleep -Seconds 30

if ($Namespace -eq "default") {
    $initialPassword = kubectl exec $podName -- cat /var/jenkins_home/secrets/initialAdminPassword 2>&1
} else {
    $initialPassword = kubectl exec -n $Namespace $podName -- cat /var/jenkins_home/secrets/initialAdminPassword 2>&1
}

if ($LASTEXITCODE -eq 0) {
    Write-Host "`nContraseña inicial de administrador:" -ForegroundColor Green
    Write-Host $initialPassword -ForegroundColor Cyan
} else {
    Write-Host "`nNo se pudo obtener la contraseña inicial automáticamente." -ForegroundColor Yellow
    Write-Host "Espera unos minutos más y ejecuta este comando manualmente:" -ForegroundColor Yellow
    if ($Namespace -eq "default") {
        Write-Host "kubectl exec $podName -- cat /var/jenkins_home/secrets/initialAdminPassword" -ForegroundColor White
    } else {
        Write-Host "kubectl exec -n $Namespace $podName -- cat /var/jenkins_home/secrets/initialAdminPassword" -ForegroundColor White
    }
}

# Abrir el navegador si se solicitó
if ($OpenBrowser) {
    Write-Host "`nAbriendo Jenkins en el navegador..." -ForegroundColor Yellow
    if ($Namespace -eq "default") {
        minikube service jenkins
    } else {
        minikube service jenkins -n $Namespace
    }
} else {
    # Mostrar comandos para acceder a Jenkins
    Write-Host "`nPara acceder a Jenkins, ejecuta:" -ForegroundColor Yellow
    if ($Namespace -eq "default") {
        Write-Host "minikube service jenkins" -ForegroundColor White
    } else {
        Write-Host "minikube service jenkins -n $Namespace" -ForegroundColor White
    }
}

Write-Host "`n===== DESPLIEGUE DE JENKINS COMPLETADO =====" -ForegroundColor Cyan
