# cleanup-microservices.ps1
# Script PowerShell para eliminar todos los microservicios excepto Jenkins

param(
    [switch]$Force,
    [string]$Namespace = "jenkins"
)

# Configurar colores para output
$Red = [System.ConsoleColor]::Red
$Green = [System.ConsoleColor]::Green
$Yellow = [System.ConsoleColor]::Yellow
$Blue = [System.ConsoleColor]::Blue
$White = [System.ConsoleColor]::White

function Write-ColorOutput {
    param(
        [string]$Message,
        [System.ConsoleColor]$Color = $White
    )
    $currentColor = $Host.UI.RawUI.ForegroundColor
    $Host.UI.RawUI.ForegroundColor = $Color
    Write-Host $Message
    $Host.UI.RawUI.ForegroundColor = $currentColor
}

function Write-Status {
    param([string]$Message)
    Write-ColorOutput "🔄 [INFO] $Message" $Blue
}

function Write-Success {
    param([string]$Message)
    Write-ColorOutput "✅ [SUCCESS] $Message" $Green
}

function Write-Warning {
    param([string]$Message)
    Write-ColorOutput "⚠️  [WARNING] $Message" $Yellow
}

function Write-Error {
    param([string]$Message)
    Write-ColorOutput "❌ [ERROR] $Message" $Red
}

# Banner
Write-Host ""
Write-ColorOutput "================================================" $Blue
Write-ColorOutput "🧹 CLEANUP MICROSERVICES SCRIPT" $Blue
Write-ColorOutput "================================================" $Blue
Write-Status "Eliminando microservicios (manteniendo Jenkins)..."
Write-Host ""


# Verificar que el namespace existe
try {
    kubectl get namespace $Namespace | Out-Null
    if ($LASTEXITCODE -ne 0) {
        throw "Namespace no existe"
    }
    Write-Success "Namespace '$Namespace' encontrado"
}
catch {
    Write-Error "El namespace '$Namespace' no existe."
    exit 1
}

# Mostrar estado actual
Write-Status "Verificando estado actual del cluster..."
Write-Host ""
Write-ColorOutput "📋 Pods en el namespace $Namespace" $Yellow
kubectl get pods -n $Namespace

Write-Host ""
Write-ColorOutput "📋 Servicios en el namespace $Namespace" $Yellow  
kubectl get services -n $Namespace

Write-Host ""
Write-ColorOutput "📋 Deployments en el namespace $Namespace" $Yellow
kubectl get deployments -n $Namespace

# Lista de servicios a eliminar (todos excepto jenkins)
$ServicesToDelete = @(
    "api-gateway",
    "cloud-config", 
    "favourite-service",
    "order-service",
    "payment-service",
    "product-service",
    "proxy-client",
    "service-discovery",
    "shipping-service", 
    "user-service",
    "zipkin"
)

Write-Host ""
Write-ColorOutput "🎯 Servicios que serán eliminados:" $Yellow
foreach ($service in $ServicesToDelete) {
    Write-Host "   • $service"
}

# Confirmación si no se usa -Force
if (-not $Force) {
    Write-Host ""
    $confirm = Read-Host "¿Estás seguro de que quieres eliminar todos los microservicios excepto Jenkins? (y/N)"
    if ($confirm -notmatch "^[Yy]$") {
        Write-Warning "Operación cancelada por el usuario."
        exit 0
    }
}

Write-Host ""
Write-Status "Iniciando eliminación de microservicios..."

# Contadores para el resumen
$DeploymentsDeleted = 0
$ServicesDeleted = 0
$ConfigMapsDeleted = 0
$Errors = 0

# Eliminar deployments
Write-Host ""
Write-Status "🗑️  Eliminando deployments..."
foreach ($service in $ServicesToDelete) {
    try {
        $deploymentExists = kubectl get deployment $service -n $Namespace 2>$null
        if ($LASTEXITCODE -eq 0) {
            Write-Status "Eliminando deployment: $service"
            kubectl delete deployment $service -n $Namespace | Out-Null
            if ($LASTEXITCODE -eq 0) {
                Write-Success "Deployment $service eliminado"
                $DeploymentsDeleted++
            } else {
                Write-Error "Error eliminando deployment $service"
                $Errors++
            }
        } else {
            Write-Warning "Deployment $service no encontrado"
        }
    }
    catch {
        Write-Error "Error procesando deployment $service`: $_"
        $Errors++
    }
}

# Eliminar servicios
Write-Host ""
Write-Status "🌐 Eliminando servicios..."
foreach ($service in $ServicesToDelete) {
    try {
        $serviceExists = kubectl get service $service -n $Namespace 2>$null
        if ($LASTEXITCODE -eq 0) {
            Write-Status "Eliminando service: $service"
            kubectl delete service $service -n $Namespace | Out-Null
            if ($LASTEXITCODE -eq 0) {
                Write-Success "Service $service eliminado"
                $ServicesDeleted++
            } else {
                Write-Error "Error eliminando service $service"
                $Errors++
            }
        } else {
            Write-Warning "Service $service no encontrado"
        }
    }
    catch {
        Write-Error "Error procesando service $service`: $_"
        $Errors++
    }
}

# Eliminar ConfigMaps relacionados
Write-Host ""
Write-Status "🗂️  Eliminando ConfigMaps relacionados..."
foreach ($service in $ServicesToDelete) {
    try {
        $configMapExists = kubectl get configmap $service -n $Namespace 2>$null
        if ($LASTEXITCODE -eq 0) {
            Write-Status "Eliminando configmap: $service"
            kubectl delete configmap $service -n $Namespace | Out-Null
            if ($LASTEXITCODE -eq 0) {
                Write-Success "ConfigMap $service eliminado"
                $ConfigMapsDeleted++
            } else {
                Write-Error "Error eliminando configmap $service"
                $Errors++
            }
        }
    }
    catch {
        Write-Error "Error procesando configmap $service`: $_"
        $Errors++
    }
}

# Esperar a que los pods terminen
Write-Host ""
Write-Status "⏳ Esperando a que los pods terminen de eliminarse..."
Start-Sleep -Seconds 10

# Verificar el estado final
Write-Host ""
Write-Success "🎉 Eliminación completada. Estado final del cluster:"
Write-Host ""

Write-ColorOutput "📋 Pods restantes en el namespace $Namespace" $Yellow
kubectl get pods -n $Namespace

Write-Host ""
Write-ColorOutput "📋 Servicios restantes en el namespace $Namespace" $Yellow
kubectl get services -n $Namespace

Write-Host ""
Write-ColorOutput "📋 Deployments restantes en el namespace $Namespace" $Yellow
kubectl get deployments -n $Namespace

# Verificar que solo Jenkins esté corriendo
$remainingPods = kubectl get pods -n $Namespace --no-headers 2>$null
$jenkinsPods = $remainingPods | Where-Object { $_ -match "jenkins" }
$otherPods = $remainingPods | Where-Object { $_ -notmatch "jenkins" }

Write-Host ""
if ($otherPods) {
    Write-Warning "⚠️  Aún hay pods no-Jenkins corriendo:"
    $otherPods | ForEach-Object { Write-Host "   • $_" }
} else {
    Write-Success "✅ Solo Jenkins está corriendo ahora."
}

Write-Status "🚀 El cluster está listo para ejecutar el pipeline nuevamente."

# Mostrar resumen final
Write-Host ""
Write-ColorOutput "================================================" $Blue
Write-ColorOutput "📊 RESUMEN DE LA OPERACIÓN" $Blue
Write-ColorOutput "================================================" $Blue
Write-Host "🗑️  Deployments eliminados: $DeploymentsDeleted"
Write-Host "🌐 Services eliminados: $ServicesDeleted"  
Write-Host "🗂️  ConfigMaps eliminados: $ConfigMapsDeleted"
Write-Host "❌ Errores encontrados: $Errors"
Write-Host "✅ Jenkins mantenido: Sí"
Write-Host "🚀 Listo para pipeline: Sí"
Write-ColorOutput "================================================" $Blue

if ($Errors -eq 0) {
    Write-Success "🎉 Operación completada exitosamente!"
} else {
    Write-Warning "⚠️  Operación completada con algunos errores. Revisa los logs arriba."
}

Write-Host ""
Write-ColorOutput "💡 Comandos útiles:" $Yellow
Write-Host "   • Ver estado: kubectl get all -n $Namespace"
Write-Host "   • Ejecutar pipeline: Ir a Jenkins y ejecutar el job"
Write-Host "   • Ver logs Jenkins: kubectl logs deployment/jenkins -n $Namespace -f"
Write-Host ""
