# Script para actualizar todos los archivos Kubernetes para usar imágenes Docker personalizadas
# Este script reemplaza las imágenes selimhorri/ por $DOCKER_NAMESPACE/ en todos los archivos YAML de Kubernetes

param (
    [Parameter(Mandatory=$false)]
    [string]$DOCKER_NAMESPACE = "kenbra"
)

# Verificar si se proporcionó un namespace
if ([string]::IsNullOrWhiteSpace($DOCKER_NAMESPACE)) {
    $DOCKER_NAMESPACE = Read-Host -Prompt "Introduce tu usuario de DockerHub"
    if ([string]::IsNullOrWhiteSpace($DOCKER_NAMESPACE)) {
        $DOCKER_NAMESPACE = "kenbra"
        Write-Host "Usando el valor por defecto: $DOCKER_NAMESPACE"
    }
}

# Asegúrate de estar en la raíz del proyecto
$projectRoot = $PSScriptRoot
$kubernetesDir = Join-Path -Path $projectRoot -ChildPath "k8s"

# Obtener todos los archivos YAML
$yamlFiles = Get-ChildItem -Path $kubernetesDir -Filter "*.yaml" 

# Excluir archivos que no son de nuestros microservicios
$excludeFiles = @("jenkins-deployment.yaml", "jenkins-pv.yaml", "jenkins-rbac.yaml", "zipkin.yaml")

Write-Host "===== Actualizando archivos Kubernetes para usar imágenes de $DOCKER_NAMESPACE =====" -ForegroundColor Cyan

foreach ($file in $yamlFiles) {
    # Verificar si el archivo está en la lista de exclusión
    if ($excludeFiles -contains $file.Name) {
        Write-Host "Omitiendo $($file.Name) (no es un microservicio)" -ForegroundColor Gray
        continue
    }
    
    Write-Host "Procesando $($file.Name)..." -ForegroundColor Yellow
    
    # Leer el contenido del archivo
    $content = Get-Content -Path $file.FullName -Raw
    
    # Reemplazar las imágenes selimhorri/ por $DOCKER_NAMESPACE/
    $newContent = $content -replace 'image: selimhorri/', "image: ${DOCKER_NAMESPACE}/"
    
    # Guardar el archivo actualizado
    $newContent | Set-Content -Path $file.FullName -NoNewline
    
    Write-Host "  $($file.Name) actualizado" -ForegroundColor Green
}

Write-Host "===== Actualización de archivos Kubernetes completada =====" -ForegroundColor Cyan
Write-Host "Ahora los archivos Kubernetes usarán imágenes de Docker Hub: $DOCKER_NAMESPACE/*-ecommerce-boot:0.1.0" -ForegroundColor Green
