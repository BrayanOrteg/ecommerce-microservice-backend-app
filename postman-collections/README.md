# E2E Tests for E-commerce Microservices

Este directorio contiene **5 colecciones completas de Postman** para realizar pruebas End-to-End (E2E) del sistema de microservicios de e-commerce a través del API Gateway.

## 📋 Resumen de las Colecciones

### 1. User Registration and Login Flow
**Archivo:** `1-user-registration-login-flow.json`
- **Propósito:** Probar el flujo completo de registro de usuarios y autenticación
- **Microservicios involucrados:** user-service, proxy-client
- **Flujo de prueba:**
  1. Health check del API Gateway
  2. Crear nuevo usuario
  3. Crear credenciales de acceso
  4. Agregar dirección del usuario
  5. Verificar login
  6. Obtener credenciales
  7. Actualizar perfil de usuario
  8. Listar todos los usuarios

### 2. Product Catalog and Category Management
**Archivo:** `2-product-catalog-management.json`
- **Propósito:** Probar la gestión de catálogo de productos y categorías
- **Microservicios involucrados:** product-service, proxy-client
- **Flujo de prueba:**
  1. Health check del API Gateway
  2. Crear categoría de productos
  3. Crear múltiples productos
  4. Navegar catálogo completo
  5. Ver detalles de productos específicos
  6. Actualizar precios
  7. Buscar productos por ID
  8. Verificar inventario y stock

### 3. Shopping Cart and Order Process
**Archivo:** `3-shopping-cart-order-process.json`
- **Propósito:** Probar el flujo completo de carrito de compras y creación de órdenes
- **Microservicios involucrados:** user-service, product-service, order-service, proxy-client
- **Flujo de prueba:**
  1. Health check del API Gateway
  2. Crear usuario para compras
  3. Crear categoría y productos
  4. Crear carrito de compras
  5. Agregar productos al carrito
  6. Ver contenido del carrito
  7. Crear orden desde el carrito
  8. Verificar detalles de la orden
  9. Actualizar estado de la orden

### 4. Payment and Shipping Integration
**Archivo:** `4-payment-shipping-integration.json`
- **Propósito:** Probar la integración de pagos y gestión de envíos
- **Microservicios involucrados:** user-service, order-service, payment-service, shipping-service, proxy-client
- **Flujo de prueba:**
  1. Health check del API Gateway
  2. Crear usuario para test de pagos
  3. Agregar dirección de envío
  4. Crear orden para pago
  5. Procesar pago con tarjeta de crédito
  6. Verificar estado del pago
  7. Actualizar pago a completado
  8. Crear registro de envío
  9. Rastrear estado del envío
  10. Actualizar envío a "en tránsito"
  11. Marcar envío como "entregado"
  12. Verificar estado final de la orden

### 5. User Favorites and Recommendations
**Archivo:** `5-user-favorites-recommendations.json`
- **Propósito:** Probar el sistema de productos favoritos y recomendaciones
- **Microservicios involucrados:** user-service, product-service, favourite-service, proxy-client
- **Flujo de prueba:**
  1. Health check del API Gateway
  2. Crear usuario para test de favoritos
  3. Crear múltiples categorías
  4. Crear productos en diferentes categorías
  5. Agregar productos a favoritos
  6. Obtener lista de favoritos del usuario
  7. Ver detalles de favoritos específicos
  8. Obtener productos por categoría (para recomendaciones)
  9. Remover productos de favoritos
  10. Verificar lista actualizada de favoritos

## 🚀 Cómo Ejecutar las Pruebas

### Prerrequisitos

1. **Newman (Postman CLI)** instalado:
   ```bash
   npm install -g newman
   ```

2. **Microservicios ejecutándose** en Kubernetes o Docker
3. **API Gateway** disponible en `http://localhost:8080` (o ajustar URL base)

### Ejecución Automática (Recomendado)

Usar el script de PowerShell incluido:

```powershell
# Ejecutar todas las pruebas con configuración por defecto
.\run-e2e-tests.ps1

# Ejecutar con URL personalizada
.\run-e2e-tests.ps1 -BaseUrl "http://localhost:9090"

# Ejecutar sin generar reportes
.\run-e2e-tests.ps1 -GenerateReports:$false

# Detener en el primer fallo
.\run-e2e-tests.ps1 -StopOnFailure
```

### Ejecución Manual

Ejecutar cada colección individualmente:

```bash
# Colección 1: User Registration and Login
newman run 1-user-registration-login-flow.json --env-var base_url=http://localhost:8080

# Colección 2: Product Catalog Management
newman run 2-product-catalog-management.json --env-var base_url=http://localhost:8080

# Colección 3: Shopping Cart and Order Process
newman run 3-shopping-cart-order-process.json --env-var base_url=http://localhost:8080

# Colección 4: Payment and Shipping Integration
newman run 4-payment-shipping-integration.json --env-var base_url=http://localhost:8080

# Colección 5: User Favorites and Recommendations
newman run 5-user-favorites-recommendations.json --env-var base_url=http://localhost:8080
```

### Ejecución con Reportes

```bash
# Generar reporte HTML
newman run 1-user-registration-login-flow.json \
  --env-var base_url=http://localhost:8080 \
  --reporter html \
  --reporter-html-export report.html

# Generar múltiples tipos de reportes
newman run 1-user-registration-login-flow.json \
  --env-var base_url=http://localhost:8080 \
  --reporter cli,html,json \
  --reporter-html-export report.html \
  --reporter-json-export report.json
```

## 📊 Validaciones y Tests Automáticos

Cada colección incluye tests automáticos que verifican:

### ✅ Validaciones HTTP
- Códigos de estado correctos (200, 201, 404, etc.)
- Tiempos de respuesta aceptables
- Headers de respuesta apropiados

### ✅ Validaciones de Datos
- Estructura correcta de JSON responses
- Tipos de datos apropiados
- Valores esperados en campos específicos
- Consistencia de IDs entre requests

### ✅ Validaciones de Negocio
- Flujos de usuario completos
- Integridad de datos entre microservicios
- Estados correctos de órdenes, pagos y envíos
- Relaciones correctas entre entidades

### ✅ Validaciones de Integración
- Comunicación correcta entre microservicios
- Funcionamiento del API Gateway
- Persistencia de datos a través del flujo completo

## 🏗️ Arquitectura del Sistema Probada

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Postman E2E   │────│   API Gateway   │────│ proxy-client    │
│   Collections   │    │  (Port 8080)    │    │                 │
└─────────────────┘    └─────────────────┘    └─────────────────┘
                                                       │
                       ┌────────────────────────────────┼─────────────────────────────┐
                       │                                │                             │
                ┌──────▼──────┐  ┌──────▼──────┐  ┌────▼──────┐  ┌──────▼──────┐
                │user-service │  │product-service│  │order-service│  │payment-service│
                └─────────────┘  └─────────────┘  └───────────┘  └─────────────┘
                       │                                │                             │
                ┌──────▼──────┐                 ┌──────▼──────┐  ┌──────▼──────┐
                │shipping-    │                 │favourite-   │  │             │
                │service      │                 │service      │  │  Database   │
                └─────────────┘                 └─────────────┘  └─────────────┘
```

## 🔧 Configuración de Variables

Cada colección utiliza variables de colección que se configuran automáticamente:

- `base_url`: URL base del API Gateway
- `user_id`: ID del usuario creado durante las pruebas
- `product_id_*`: IDs de productos creados
- `category_id_*`: IDs de categorías creadas
- `order_id`: ID de órdenes creadas
- `payment_id`: ID de pagos procesados
- `shipping_id`: ID de envíos creados
- `favourite_id_*`: IDs de favoritos

## 📁 Estructura de Archivos

```
postman-collections/
├── 1-user-registration-login-flow.json       # Colección 1: Usuarios y Login
├── 2-product-catalog-management.json         # Colección 2: Catálogo de Productos
├── 3-shopping-cart-order-process.json        # Colección 3: Carrito y Órdenes
├── 4-payment-shipping-integration.json       # Colección 4: Pagos y Envíos
├── 5-user-favorites-recommendations.json     # Colección 5: Favoritos
├── run-e2e-tests.ps1                         # Script de automatización
└── README.md                                 # Esta documentación
```

## 🚨 Solución de Problemas

### Error: "Newman command not found"
```bash
npm install -g newman
```

### Error: "API Gateway not accessible"
- Verificar que los microservicios estén ejecutándose
- Verificar la URL del API Gateway (puerto 8080 por defecto)
- Comprobar conectividad de red

### Error: "Test timeouts"
- Aumentar el timeout en Newman: `--timeout-request 30000`
- Verificar rendimiento de los microservicios
- Revisar logs de los contenedores/pods

### Fallos en tests específicos
- Revisar los logs detallados de Newman
- Verificar que los microservicios tengan datos consistentes
- Comprobar que las bases de datos estén accesibles

## 📈 Resultados Esperados

Al ejecutar todas las colecciones exitosamente, deberías ver:

- ✅ **60+ requests ejecutados** exitosamente
- ✅ **150+ tests automáticos** pasados
- ✅ **Todas las APIs** del sistema funcionando
- ✅ **Integración completa** entre microservicios validada
- ✅ **Flujos de negocio** E2E completados

## 🎯 Casos de Uso Cubiertos

Las pruebas E2E cubren los siguientes casos de uso del e-commerce:

1. **Gestión de Usuarios**: Registro, autenticación, perfil
2. **Catálogo de Productos**: Navegación, búsqueda, gestión
3. **Proceso de Compra**: Carrito, checkout, órdenes
4. **Procesamiento de Pagos**: Múltiples métodos, estados
5. **Gestión de Envíos**: Tracking, estados, entrega
6. **Experiencia del Usuario**: Favoritos, recomendaciones

Estas pruebas garantizan que todo el ecosistema de microservicios funciona correctamente en conjunto para proporcionar una experiencia de e-commerce completa.
