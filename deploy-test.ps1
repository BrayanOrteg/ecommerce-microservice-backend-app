# Script para desplegar y probar microservicios en Minikube (PowerShell)

Write-Host "==============================================`nIniciando prueba completa de despliegue`n==============================================" -ForegroundColor Cyan

# Detener y eliminar Minikube si ya está en ejecución
Write-Host "Deteniendo y eliminando Minikube existente (si existe)..." -ForegroundColor Yellow
minikube delete

# Iniciar un nuevo Minikube
Write-Host "Iniciando un nuevo Minikube..." -ForegroundColor Yellow
minikube start --cpus=no-limit --memory=no-limit

# Habilitar el addon de ingress
Write-Host "Habilitando addons de Minikube..." -ForegroundColor Yellow
minikube addons enable ingress
minikube addons enable metrics-server

# Verificar estado de Minikube
Write-Host "Verificando estado de Minikube..." -ForegroundColor Yellow
minikube status

# Cambiar al directorio de los manifiestos de Kubernetes
cd k8s

Write-Host "==============================================`nDesplegando servicios base`n==============================================" -ForegroundColor Cyan

# Desplegar Zipkin primero
Write-Host "Desplegando Zipkin..." -ForegroundColor Yellow
kubectl apply -f zipkin.yaml
kubectl wait --for=condition=available --timeout=10s deployment/zipkin

# Desplegar Service Discovery (Eureka)
Write-Host "Desplegando Service Discovery (Eureka)..." -ForegroundColor Yellow
kubectl apply -f service-discovery.yaml
kubectl wait --for=condition=available --timeout=140s deployment/service-discovery

# Desplegar Cloud Config
Write-Host "Desplegando Cloud Config..." -ForegroundColor Yellow
kubectl apply -f cloud-config.yaml
kubectl wait --for=condition=available --timeout=60s deployment/cloud-config



# Pausa para asegurarse de que los servicios básicos estén listos
Write-Host "Esperando 30 segundos para que los servicios base estén totalmente disponibles..." -ForegroundColor Yellow
Start-Sleep -Seconds 30

Write-Host "==============================================`nDesplegando servicios de negocio`n==============================================" -ForegroundColor Cyan

# Desplegar servicios de negocio
Write-Host "Desplegando User Service..." -ForegroundColor Yellow
kubectl apply -f user-service.yaml

Write-Host "Desplegando Product Service..." -ForegroundColor Yellow
kubectl apply -f product-service.yaml

Write-Host "Desplegando Order Service..." -ForegroundColor Yellow
kubectl apply -f order-service.yaml

Write-Host "Desplegando Payment Service..." -ForegroundColor Yellow
kubectl apply -f payment-service.yaml

Write-Host "Desplegando Shipping Service..." -ForegroundColor Yellow
kubectl apply -f shipping-service.yaml

Write-Host "Desplegando Favourite Service..." -ForegroundColor Yellow
kubectl apply -f favourite-service.yaml

# Pausa para asegurarse de que los servicios de negocio estén iniciados
Write-Host "Esperando 30 segundos para permitir que los servicios de negocio comiencen..." -ForegroundColor Yellow
Start-Sleep -Seconds 30

# Desplegar API Gateway y Proxy Client al final
Write-Host "Desplegando API Gateway..." -ForegroundColor Yellow
kubectl apply -f api-gateway.yaml

Write-Host "Desplegando Proxy Client..." -ForegroundColor Yellow
kubectl apply -f proxy-client.yaml

Write-Host "==============================================`nEsperando que todos los servicios estén disponibles...`n==============================================" -ForegroundColor Cyan
Start-Sleep -Seconds 30

# Verificar el estado de todos los pods
Write-Host "Estado de los pods:" -ForegroundColor Green
kubectl get pods

# Verificar el estado de todos los servicios
Write-Host "Estado de los servicios:" -ForegroundColor Green
kubectl get services

# Esperar a que todos los pods estén en estado Running
Write-Host "Esperando a que todos los pods estén en estado Running..." -ForegroundColor Yellow
kubectl wait --for=condition=ready pods --all --timeout=120s

Write-Host "==============================================`nVerificando acceso a interfaces web`n==============================================" -ForegroundColor Cyan

# Crear acceso a los servicios principales
Write-Host "Configurando port-forwarding para acceso a los servicios..." -ForegroundColor Yellow

Write-Host "`nPara acceder a Eureka (Service Discovery), ejecuta en una nueva ventana PowerShell:" -ForegroundColor Green
Write-Host "kubectl port-forward svc/service-discovery 8761:8761" -ForegroundColor White
Write-Host "Luego navega a: http://localhost:8761" -ForegroundColor Cyan

Write-Host "`nPara acceder a Zipkin, ejecuta en una nueva ventana PowerShell:" -ForegroundColor Green
Write-Host "kubectl port-forward svc/zipkin 9411:9411" -ForegroundColor White
Write-Host "Luego navega a: http://localhost:9411" -ForegroundColor Cyan

Write-Host "`nPara acceder al API Gateway, ejecuta en una nueva ventana PowerShell:" -ForegroundColor Green
Write-Host "kubectl port-forward svc/api-gateway 9191:9191" -ForegroundColor White
Write-Host "Luego navega a: http://localhost:9191" -ForegroundColor Cyan

Write-Host "`n==============================================`nDespliegue completado. Puedes usar los comandos indicados para acceder a las interfaces.`n==============================================" -ForegroundColor Magenta

# Volver al directorio principal
cd ..

Write-Host "`nPara eliminar todo el despliegue, ejecuta: kubectl delete -f k8s/" -ForegroundColor Red
Write-Host "Para detener Minikube, ejecuta: minikube stop" -ForegroundColor Red
