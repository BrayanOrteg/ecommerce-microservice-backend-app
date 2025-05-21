#!/bin/bash

echo "=============================================="
echo "Iniciando prueba completa de despliegue"
echo "=============================================="

# Detener y eliminar Minikube si ya está en ejecución
echo "Deteniendo y eliminando Minikube existente (si existe)..."
minikube delete

# Iniciar un nuevo Minikube
echo "Iniciando un nuevo Minikube..."
minikube start --memory=4096 --cpus=2

# Habilitar el addon de ingress
echo "Habilitando addons de Minikube..."
minikube addons enable ingress
minikube addons enable metrics-server

# Verificar estado de Minikube
echo "Verificando estado de Minikube..."
minikube status

# Cambiar al directorio de los manifiestos de Kubernetes
cd k8s

echo "=============================================="
echo "Desplegando servicios base"
echo "=============================================="

# Desplegar Zipkin primero
echo "Desplegando Zipkin..."
kubectl apply -f zipkin.yaml
kubectl wait --for=condition=available --timeout=300s deployment/zipkin

# Desplegar Cloud Config
echo "Desplegando Cloud Config..."
kubectl apply -f cloud-config.yaml
kubectl wait --for=condition=available --timeout=300s deployment/cloud-config

# Desplegar Service Discovery (Eureka)
echo "Desplegando Service Discovery (Eureka)..."
kubectl apply -f service-discovery.yaml
kubectl wait --for=condition=available --timeout=300s deployment/service-discovery

# Pausa para asegurarse de que los servicios básicos estén listos
echo "Esperando 30 segundos para que los servicios base estén totalmente disponibles..."
sleep 30

echo "=============================================="
echo "Desplegando servicios de negocio"
echo "=============================================="

# Desplegar servicios de negocio
echo "Desplegando User Service..."
kubectl apply -f user-service.yaml

echo "Desplegando Product Service..."
kubectl apply -f product-service.yaml

echo "Desplegando Order Service..."
kubectl apply -f order-service.yaml

echo "Desplegando Payment Service..."
kubectl apply -f payment-service.yaml

echo "Desplegando Shipping Service..."
kubectl apply -f shipping-service.yaml

echo "Desplegando Favourite Service..."
kubectl apply -f favourite-service.yaml

# Pausa para asegurarse de que los servicios de negocio estén iniciados
echo "Esperando 30 segundos para permitir que los servicios de negocio comiencen..."
sleep 30

# Desplegar API Gateway y Proxy Client al final
echo "Desplegando API Gateway..."
kubectl apply -f api-gateway.yaml

echo "Desplegando Proxy Client..."
kubectl apply -f proxy-client.yaml

echo "=============================================="
echo "Esperando que todos los servicios estén disponibles..."
echo "=============================================="
sleep 30

# Verificar el estado de todos los pods
echo "Estado de los pods:"
kubectl get pods

# Verificar el estado de todos los servicios
echo "Estado de los servicios:"
kubectl get services

# Esperar a que todos los pods estén en estado Running
echo "Esperando a que todos los pods estén en estado Running..."
kubectl wait --for=condition=ready pods --all --timeout=300s

echo "=============================================="
echo "Verificando acceso a interfaces web"
echo "=============================================="

# Crear acceso al Service Discovery (Eureka)
echo "Creando acceso al Service Discovery (Eureka) en el puerto 8761..."
kubectl port-forward svc/service-discovery 8761:8761 &
EUREKA_PID=$!
echo "Eureka debería estar accesible en: http://localhost:8761"

# Crear acceso a Zipkin
echo "Creando acceso a Zipkin en el puerto 9411..."
kubectl port-forward svc/zipkin 9411:9411 &
ZIPKIN_PID=$!
echo "Zipkin debería estar accesible en: http://localhost:9411"

# Crear acceso al API Gateway
echo "Creando acceso al API Gateway en el puerto 9191..."
kubectl port-forward svc/api-gateway 9191:9191 &
API_GATEWAY_PID=$!
echo "API Gateway debería estar accesible en: http://localhost:9191"

echo "=============================================="
echo "Despliegue completado. Las interfaces web estarán disponibles durante 5 minutos."
echo "Para detener los port-forwards, presiona Ctrl+C."
echo "=============================================="

# Mantener los port-forwards activos durante 5 minutos
echo "Esperando 5 minutos antes de finalizar..."
sleep 300

# Limpiar los procesos de port-forward
kill $EUREKA_PID $ZIPKIN_PID $API_GATEWAY_PID

echo "=============================================="
echo "Prueba finalizada. Para eliminar todo el despliegue, ejecuta: kubectl delete -f ."
echo "Para detener Minikube, ejecuta: minikube stop"
echo "=============================================="
