# Reporte de Operación - Ecommerce Microservices Backend

## 📋 Resumen
Este reporte proporciona instrucciones completas para desplegar y operar la aplicación de microservicios de ecommerce en un entorno Kubernetes local usando Minikube.

## 🛠️ Tecnologías Requeridas

### Pre-requisitos
- **Docker Desktop** - Para construcción de imágenes y contenedores
- **Minikube** - Cluster Kubernetes local
- **kubectl** - Cliente de línea de comandos de Kubernetes
- **Postman** (opcional) - Para pruebas de API
- **Git** - Control de versiones

### Stack Tecnológico
- **Java 17+ & Spring Boot** - Microservicios backend
- **Spring Cloud** - Service Discovery (Eureka), API Gateway, Config Server
- **RabbitMQ** - Message Broker para comunicación asíncrona
- **Zipkin** - Distributed Tracing
- **Docker** - Containerización
- **Kubernetes** - Orquestación de contenedores

## 🚀 Instrucciones de Despliegue

### 1. Iniciar Minikube
```bash
# Iniciar Minikube con recursos ilimitados
minikube start --cpus=no-limit --memory=no-limit

# Verificar estado del cluster
minikube status
```

### 2. Desplegar Manifiestos Kubernetes

#### Para Entorno de Desarrollo (dev):
```bash
# Navegar al directorio k8s/dev
cd k8s/dev

# Desplegar en el orden correcto respetando dependencias:

# 1. Zipkin (sin dependencias)
kubectl apply -f zipkin.yaml
echo "Esperando 30 segundos para que Zipkin inicie..."
sleep 30

# 2. RabbitMQ (sin dependencias)
kubectl apply -f rabbitmq.yaml
echo "Esperando 45 segundos para que RabbitMQ inicie completamente..."
sleep 45

# 3. Service Discovery (Eureka)
kubectl apply -f service-discovery.yaml
echo "Esperando 90 segundos para que Eureka esté listo..."
sleep 90

# 4. Cloud Config (depende de Eureka y RabbitMQ)
kubectl apply -f cloud-config.yaml
echo "Esperando 60 segundos para que Cloud Config esté disponible..."
sleep 60

# 5. API Gateway (depende de Eureka y Cloud Config)
kubectl apply -f api-gateway.yaml
echo "Esperando 45 segundos para que API Gateway inicie..."
sleep 45

# 6. Servicios de negocio (pueden desplegarse en paralelo)
kubectl apply -f user-service.yaml
kubectl apply -f product-service.yaml
kubectl apply -f order-service.yaml
kubectl apply -f payment-service.yaml
kubectl apply -f favourite-service.yaml
kubectl apply -f shipping-service.yaml
kubectl apply -f proxy-client.yaml

echo "Esperando 120 segundos para que todos los servicios se registren en Eureka..."
sleep 120
```

#### Para Entornos de Producción (prod) o Staging (stage):
```bash
# Para producción
cd k8s/prod

# Para staging
cd k8s/stage

# Usar los mismos comandos pero cambiando la ruta del directorio
```

### 3. Verificar Estado de los Pods
```bash
# Ver todos los pods
kubectl get pods

# Ver pods con más detalles
kubectl get pods -o wide

# Ver logs de un pod específico si hay problemas
kubectl logs <nombre-del-pod>

# Ver estado de los servicios
kubectl get services
```

### 4. Configurar Port Forwarding

#### Para API Gateway (Puerto principal):
```bash
# Port forward para API Gateway
kubectl port-forward service/api-gateway 8080:8080

# Mantener este comando ejecutándose en una terminal separada
```

#### Ports Forwarding Adicionales (opcional):
```bash
# Eureka Dashboard
kubectl port-forward service/service-discovery 8761:8761

# Zipkin UI
kubectl port-forward service/zipkin 9411:9411

# RabbitMQ Management UI
kubectl port-forward service/rabbitmq 15672:15672

# Cloud Config
kubectl port-forward service/cloud-config 9296:9296
```

## 🧪 Pruebas con Postman

### Importar Colección
1. Abrir Postman
2. Importar la colección: `postman-collections/E2E-tests.postman_collection.json`
3. La colección contiene pruebas E2E completas y tests de validación

### Configurar Variables de Entorno
**Configuración requerida:**
```
Variable: base_url
Valor: http://localhost:8080
```

### Endpoints Principales Disponibles
- **Users**: `http://localhost:8080/user-service/api/users`
- **Products**: `http://localhost:8080/product-service/api/products`
- **Orders**: `http://localhost:8080/order-service/api/orders`
- **Payments**: `http://localhost:8080/payment-service/api/payments`
- **Favourites**: `http://localhost:8080/favourite-service/api/favourites`
- **Shipping**: `http://localhost:8080/shipping-service/api/shippings`

## 📊 Monitoreo y Observabilidad

### Dashboards Disponibles
- **Eureka Dashboard**: http://localhost:8761
- **Zipkin Tracing**: http://localhost:9411
- **RabbitMQ Management**: http://localhost:15672 (guest/guest)

## 🔧 Comandos Útiles

### Kubernetes
```bash
# Eliminar todos los recursos
kubectl delete -f k8s/dev/

# Reiniciar un deployment
kubectl rollout restart deployment/<nombre-deployment>

# Escalar un servicio
kubectl scale deployment <nombre-deployment> --replicas=2

# Ver logs en tiempo real
kubectl logs -f <nombre-pod>
```

### Minikube
```bash
# Ver dashboard de Kubernetes
minikube dashboard

# Parar Minikube
minikube stop

# Eliminar cluster
minikube delete

# Ver IP del cluster
minikube ip
```

## ⚠️ Tiempos de Espera Críticos

| Servicio | Tiempo de Espera | Motivo |
|----------|------------------|---------|
| Zipkin | 30s | Inicialización básica |
| RabbitMQ | 45s | Configuración de colas y exchanges |
| Service Discovery | 90s | Registro de servicios y health checks |
| Cloud Config | 60s | Carga de configuraciones |
| API Gateway | 45s | Configuración de rutas |
| Microservicios | 120s | Registro en Eureka y configuración |

## 📝 Notas Importantes

1. **Orden de Despliegue**: Es crítico seguir el orden especificado debido a las dependencias entre servicios
2. **Recursos**: Minikube necesita suficientes recursos (CPU/RAM) para todos los servicios
3. **Health Checks**: Los servicios tienen health checks configurados que pueden tardar en activarse
4. **RabbitMQ**: Solo cloud-config y product-service requieren RabbitMQ según la configuración actual
5. **Variables de Entorno**: Cada entorno (dev/prod/stage) tiene sus propias configuraciones

## ✅ Verificación de Despliegue Exitoso

El despliegue es exitoso cuando:
- Todos los pods están en estado `Running`
- Los health checks pasan (visible en `kubectl get pods`)
- API Gateway responde en http://localhost:8080
- Los servicios aparecen registrados en Eureka Dashboard
- Las pruebas de Postman ejecutan correctamente
