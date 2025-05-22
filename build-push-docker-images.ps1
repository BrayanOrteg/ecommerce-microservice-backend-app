# Script para construir y subir todas las imágenes Docker a DockerHub
# Este script construye las imágenes basadas en los Dockerfiles de cada microservicio
# y las sube a DockerHub para que puedan ser utilizadas por Jenkins

# Configuración
$DOCKER_NAMESPACE = Read-Host -Prompt "Introduce tu usuario de DockerHub"
if ([string]::IsNullOrWhiteSpace($DOCKER_NAMESPACE)) {
    $DOCKER_NAMESPACE = "kenbra"
    Write-Host "Usando el valor por defecto: $DOCKER_NAMESPACE"
}
$VERSION = "0.1.0"
$SERVICES = @(
    "service-discovery",
    "cloud-config",
    "api-gateway",
    "order-service",
    "payment-service",
    "product-service",
    "shipping-service",
    "user-service",
    "favourite-service",
    "proxy-client"
)

# Asegúrate de estar en la raíz del proyecto
Set-Location -Path $PSScriptRoot

# Compilar el proyecto con Maven primero
Write-Host "===== Compilando el proyecto con Maven =====" -ForegroundColor Cyan
Write-Host "Esto puede tardar unos minutos..." -ForegroundColor Yellow
try {
    # Verificar si existe mvnw
    if (Test-Path -Path ".\mvnw") {
        .\mvnw clean package -DskipTests
    } else {
        # Si no existe mvnw, usar maven directamente si está instalado
        mvn clean package -DskipTests
    }
    if ($LASTEXITCODE -ne 0) {
        Write-Host "Error al compilar el proyecto con Maven. Verifique los errores y vuelva a intentarlo." -ForegroundColor Red
        exit 1
    }
} catch {
    Write-Host "Error al ejecutar Maven: $_" -ForegroundColor Red
    Write-Host "Asegúrese de tener Maven instalado o que el wrapper de Maven (mvnw) esté disponible." -ForegroundColor Red
    exit 1
}

# Iniciar sesión en DockerHub
Write-Host "===== Iniciando sesión en DockerHub =====" -ForegroundColor Cyan
docker login

# Construir y subir cada imagen
foreach ($service in $SERVICES) {
    Write-Host "===== Procesando $service =====" -ForegroundColor Green
    
    # Verificar que el JAR existe
    $jarPath = "$PSScriptRoot\$service\target\$service-v$VERSION.jar"
    if (-not (Test-Path -Path $jarPath)) {
        Write-Host "No se encontró el archivo JAR en $jarPath. Intentando compilar el servicio..." -ForegroundColor Yellow
        $compilationSuccess = Compile-Service -service $service
        
        # Verificar nuevamente si el JAR existe después de la compilación
        if (-not $compilationSuccess -or -not (Test-Path -Path $jarPath)) {
            Write-Host "No se pudo generar el archivo JAR para $service. Saltando este servicio." -ForegroundColor Red
            continue
        }
    }
    
    # Crear un Dockerfile temporal simplificado
    $dockerfilePath = "$PSScriptRoot\$service\Dockerfile.temp"
    
    # Determinar el puerto según el servicio
    $port = switch ($service) {
        "service-discovery" { "8761" }
        "cloud-config" { "9296" }
        "api-gateway" { "8080" }
        "order-service" { "8300" }
        "payment-service" { "8400" }
        "product-service" { "8500" }
        "shipping-service" { "8600" }
        "user-service" { "8700" }
        "favourite-service" { "8800" }
        "proxy-client" { "8900" }
        default { "8080" }
    }
    
    # Crear contenido del Dockerfile temporal
    $dockerfileContent = @"
FROM openjdk:11
ARG PROJECT_VERSION=$VERSION
RUN mkdir -p /home/app
WORKDIR /home/app
ENV SPRING_PROFILES_ACTIVE=dev
COPY target/$service-v$VERSION.jar $service.jar
EXPOSE $port
ENTRYPOINT ["java", "-Dspring.profiles.active=dev", "-jar", "$service.jar"]
"@
    
    # Guardar el Dockerfile temporal
    $dockerfileContent | Set-Content -Path $dockerfilePath -NoNewline
    
    # Ir al directorio del servicio
    Set-Location -Path "$PSScriptRoot\$service"
    
    # Nombre de la imagen
    $imageName = "$DOCKER_NAMESPACE/$service-ecommerce-boot:$VERSION"
    
    # Construir la imagen usando el Dockerfile temporal
    Write-Host "Construyendo imagen para $service..." -ForegroundColor Yellow
    docker build -t $imageName -f Dockerfile.temp .
    
    # Si la construcción fue exitosa, subir la imagen
    if ($LASTEXITCODE -eq 0) {
        # Subir la imagen a DockerHub
        Write-Host "Subiendo imagen $imageName a DockerHub..." -ForegroundColor Yellow
        docker push $imageName
        
        if ($LASTEXITCODE -eq 0) {
            Write-Host "Imagen $imageName subida correctamente a DockerHub" -ForegroundColor Green
        } else {
            Write-Host "Error al subir la imagen $imageName a DockerHub" -ForegroundColor Red
        }
    } else {
        Write-Host "Error al construir la imagen para $service. No se subirá a DockerHub." -ForegroundColor Red
    }
    
    # Eliminar el Dockerfile temporal
    if (Test-Path -Path $dockerfilePath) {
        Remove-Item -Path $dockerfilePath -Force
    }
    
    # Volver al directorio raíz
    Set-Location -Path $PSScriptRoot
}

# Cerrar sesión de DockerHub
docker logout

Write-Host "===== Proceso completado =====" -ForegroundColor Cyan
Write-Host "Todas las imágenes han sido construidas y subidas a DockerHub." -ForegroundColor Green
Write-Host "Usuario DockerHub: $DOCKER_NAMESPACE" -ForegroundColor Green
Write-Host "Versión de las imágenes: $VERSION" -ForegroundColor Green

# Mostrar instrucciones para actualizar archivos
Write-Host "`nPara utilizar estas imágenes:" -ForegroundColor Magenta
Write-Host "1. Actualiza los archivos K8s reemplazando 'selimhorri' por '$DOCKER_NAMESPACE'" -ForegroundColor White
Write-Host "2. Actualiza el compose.yml reemplazando 'selimhorri' por '$DOCKER_NAMESPACE'" -ForegroundColor White
Write-Host "3. Actualiza el Jenkinsfile estableciendo DOCKER_NAMESPACE=$DOCKER_NAMESPACE" -ForegroundColor White
