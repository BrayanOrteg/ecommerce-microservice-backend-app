# REPORTE FINAL - ECOMMERCE MICROSERVICES
---
## Configuración de Pipelines

### Pipeline Principal (Jenkinsfile)

**Configuración Base:**
- **Agent:** any
- **Ambientes:** dev, stage, prod (seleccionable via parámetro)
- **Namespace:** jenkins (Kubernetes)
- **Docker Registry:** DockerHub (kenbra)

**Etapas del Pipeline:**

#### 1. Preparar Entorno
- Instalación de herramientas: kubectl, Java 11, Maven, Node.js, Newman, GitHub CLI
- Configuración condicional de Locust para ambiente `stage`
- Verificación de dependencias

![Configuración del Pipeline - Preparar Entorno](https://cdn.discordapp.com/attachments/895756291941740624/1378167989521616956/image.png?ex=683b9e75&is=683a4cf5&hm=61b63016b4ba71d6ccaaec2367a83b9408cf3b8e8b40809d3504e5f39616df20&)

#### 2. Pruebas Unitarias
- **Condición:** Ejecuta en ambiente `stage`
- **Herramienta:** Maven
- **Comando:** `mvn test -Dtest="**/*Test"`
- **Ubicación de archivos de prueba:**
  - **Tests de API/Resource:** `product-service/src/test/java/com/selimhorri/app/resource/ProductResourceTest.java`
  - **Tests de Lógica de Negocio:** `product-service/src/test/java/com/selimhorri/app/service/ProductServiceTest.java`  
  - **Tests de Repositorio/JPA:** `product-service/src/test/java/com/selimhorri/app/repository/`
    - `ProductRepositoryTest.java`
    - `CategoryRepositoryTest.java`

![Configuración de Pruebas Unitarias](https://media.discordapp.net/attachments/895756291941740624/1378175061608894494/image.png?ex=683ba50b&is=683a538b&hm=acfb41f3b7b246a0acaeca3e7ef5b81615b79e54e234d3005b54b00acebaeb12&=&format=webp&quality=lossless)

#### 3. Pruebas de Integración  
- **Condición:** Ejecuta en ambiente`stage`
- **Herramienta:** Maven
- **Comando:** `mvn test -Dtest="**/*IntegrationTest"`
- **Ubicación de archivos de prueba:** `product-service/src/test/java/com/selimhorri/app/integration/`
  - **Tests End-to-End del servicio:** `ProductServiceIntegrationTest.java`
  - **Tests de Integración entre microservicios:** `ProductServiceIntegrationWithOtherServicesTest.java`

![Configuración de Pruebas de Integración](https://media.discordapp.net/attachments/895756291941740624/1378175106798325832/image.png?ex=683ba516&is=683a5396&hm=1d8fb0f7a51859754112e18f2c15caf9ae3ebe0c971064aa3e9b6bd7c44f39e2&=&format=webp&quality=lossless)

#### 4. Despliegue de Infraestructura
- **Zipkin:** 30 segundos de espera para estabilización
- **Service Discovery (Eureka):** 90 segundos de espera para inicialización
- **Cloud Config:** 60 segundos de espera para disponibilidad

![Despliegue de Infraestructura](https://media.discordapp.net/attachments/895756291941740624/1378175215338782801/image.png?ex=683ba530&is=683a53b0&hm=c6f3eeff8e669a3ddd688f192f787550df6908338e062dee0a387da902853a77&=&format=webp&quality=lossless)

#### 5. Despliegue de Microservicios
- **API Gateway:** 60 segundos de espera para inicialización
- **Microservicios de negocio:** 60 segundos de espera final para estabilización
- Despliegue en paralelo de: order-service, payment-service, product-service, shipping-service, user-service, favourite-service, proxy-client

![Despliegue de Microservicios](https://media.discordapp.net/attachments/895756291941740624/1378175275296227368/image.png?ex=683ba53e&is=683a53be&hm=dd7e016e30bbf557b7e54e2d2c392dc2519c8835679d3b5f5b40482eac59d9be&=&format=webp&quality=lossless)

#### 6. Pruebas E2E
- **Condición:** Ejecuta en ambientes `stage` y `prod`
- **Herramienta:** Newman (Postman CLI)
- **Archivo:** `postman-collections/E2E-tests.postman_collection.json`

![Configuración de Pruebas E2E](https://media.discordapp.net/attachments/895756291941740624/1378175868395851867/image.png?ex=683ba5cc&is=683a544c&hm=ee1ac3faf15e4256a934db8216371629d70f9e39b6952abe3ed81d3b616a32a5&=&format=webp&quality=lossless)

#### 7. Pruebas de Carga (Locust) - en pipeline
- **Condición:** Solo ambiente `stage`
- **Configuración:** 100 usuarios, 20 usuarios/seg, 30 segundos
- **Archivo:** `locust/locustfile.py`
- **Reportes:** CSV exportados como artefactos

![Configuración de Pruebas de Carga Locust](https://media.discordapp.net/attachments/895756291941740624/1378175989124960286/image.png?ex=683ba5e9&is=683a5469&hm=d53b8428710c6d870f54a127bceae5c99336c8b84657022dcaa51b2c6631a9b7&=&format=webp&quality=lossless)

#### 7.1 Pruebas de Carga (Locust) -  manualmente usando interfaz gráfica.
- **Condición:** Se corren sobre los microservicios en minikube pero se corre el locust manualmente desde su interfaz gráfica.
- 1000 usuarios concurrentes, 200 usuarios/seg, 3 minutos y 9 segundos de duración
- 8 endpoints probados simultáneamente
- **Archivo:** `locust/locustfile.py`
- **Reportes:** archivo html con todo el resultado de las pruebas

#### 8. Generación de Release Notes
- **Condición:** Solo ambiente `prod`
- **Herramienta:** GitHub CLI
- **Función:** Crear release automático con notas detalladas

![Configuración de Release Notes](https://media.discordapp.net/attachments/895756291941740624/1378176234659381379/image.png?ex=683ba623&is=683a54a3&hm=35f3ce7dffe7d410a772248ee775dc869d0491c08d31bcd66395858d3370a844&=&format=webp&quality=lossless)

### Configuración de Kubernetes

**Manifiestos creados:**
- Jenkins (PV, RBAC, Deployment)
- Microservicios (Deployment + Service para cada uno)
- Variables de entorno configuradas para Eureka discovery

**Modificaciones realizadas:**
- Actualización de URLs en application.yaml de cloud-config.
- Configuración de las variables de entorno en application.yaml de cloud-config.
- Cambio de imágenes Docker de `selimhorri` a `kenbra`.

![Configuración de Kubernetes](https://media.discordapp.net/attachments/895756291941740624/1378176539954253904/image.png?ex=683ba66c&is=683a54ec&hm=8c343717366c7b2aaa70a1898b6fb3b25a62fdb4ece5ad9fb25b462e52ce38a2&=&format=webp&quality=lossless)

---

## Resultados de Ejecución

### Pipeline DEV
**Estado:** Exitoso
**Etapas ejecutadas:**
- Preparar Entorno
- Desplegar Infraestructura
- Desplegar Microservicios
- Verificar Despliegue

#### Verificación de despliegue
![Verificación de Despliegue DEV](https://media.discordapp.net/attachments/895756291941740624/1378176993002127522/image.png?ex=683ba6d8&is=683a5558&hm=5a4deb2c2c9ac3de8c34126cb5ea3ce80e4a7c79b2d92c543268208493fce737&=&format=webp&quality=lossless&width=332&height=350)

#### Omisión de etapas
![Omisión de Etapas DEV](https://media.discordapp.net/attachments/895756291941740624/1378178856514490398/image.png?ex=683ba894&is=683a5714&hm=1ab5f6808fbf41c53c51a35b4e694d1bb3bf9664bfb942420fccb7c18d32774f&=&format=webp&quality=lossless)

#### Éxito de ejecución
![Éxito de Ejecución DEV](https://media.discordapp.net/attachments/895756291941740624/1378177232475914250/image.png?ex=683ba711&is=683a5591&hm=797f9c7b86bd36ea0b40f82f264533cd04f07c42c93bcb926989f42c6862c879&=&format=webp&quality=lossless)

### Pipeline STAGE
**Estado:** Exitoso
**Etapas ejecutadas:**
- Preparar Entorno
- Pruebas Unitarias
- Pruebas de Integración  
- Desplegar Infraestructura
- Desplegar Microservicios
- Verificar Despliegue
- Pruebas E2E
- Pruebas de Carga (Locust)

![Pipeline STAGE - Resumen de Ejecución](https://media.discordapp.net/attachments/895756291941740624/1378177521367126036/image.png?ex=683ba756&is=683a55d6&hm=16deea1628ba7c0c3b4eba1a8da5199b8c33aa08e5761f786cc68a4c9c225950&=&format=webp&quality=lossless)

#### Test unitarios
![Resultados Tests Unitarios STAGE](https://media.discordapp.net/attachments/895756291941740624/1378177608813907998/image.png?ex=683ba76b&is=683a55eb&hm=b3ce6a6fc48d2c25e6cf20e69a62c3fa17bf2e23424cd28b7b4ca14e438e2c5c&=&format=webp&quality=lossless)

#### Test integración
![Resultados Tests de Integración STAGE](https://media.discordapp.net/attachments/895756291941740624/1378177795888513024/image.png?ex=683ba797&is=683a5617&hm=4e9a42093fdd9ea6614e2d5d08c63c356f87a641c763bd2516d3372af1ce522c&=&format=webp&quality=lossless)

#### Test E2E
![Resultados Tests E2E STAGE](https://media.discordapp.net/attachments/895756291941740624/1378177861197893663/image.png?ex=683ba7a7&is=683a5627&hm=740521831e1cce0d20d7c4168901a73fb89c2be5c91294222e7344a952d6f915&=&format=webp&quality=lossless)

#### Locust
![Resultados Tests de Carga Locust STAGE](https://media.discordapp.net/attachments/895756291941740624/1378177929460060320/image.png?ex=683ba7b7&is=683a5637&hm=e384933c9c72bfcd68e8a19da3b969c0c4b71d13a7520f8519b4f18789698eee&=&format=webp&quality=lossless)

#### Éxito
![Éxito de Ejecución STAGE](https://media.discordapp.net/attachments/895756291941740624/1378178090257223680/image.png?ex=683ba7de&is=683a565e&hm=f1038597ba9222e7408879f1dccffbd2b71180deaf4f91acd1bba14e8b3c7437&=&format=webp&quality=lossless)

### Pipeline PROD
**Estado:** Exitoso
**Etapas ejecutadas:**
- Preparar Entorno
- Desplegar Infraestructura
- Desplegar Microservicios
- Verificar Despliegue
- Pruebas E2E
- Generar Release Notes

#### Test E2E
![Resultados Tests E2E PROD](https://media.discordapp.net/attachments/895756291941740624/1378179122395746304/image.png?ex=683ba8d4&is=683a5754&hm=4a54fdb7edce75f482817804a0d5289a733915535664a2df06697701248cf92a&=&format=webp&quality=lossless)

#### Release Notes
![Generación de Release Notes PROD](https://media.discordapp.net/attachments/895756291941740624/1378179278075728002/image.png?ex=683ba8f9&is=683a5779&hm=6ccb22ed58374028ec1e808b76654e8fd6a2bef00a6a2321afa7c26fbc473840&=&format=webp&quality=lossless&width=867&height=960)

#### Éxito
![Éxito de Ejecución PROD](https://media.discordapp.net/attachments/895756291941740624/1378179407415345303/image.png?ex=683ba918&is=683a5798&hm=a7c1b39206b46d787a3c5310ec213acdb4922209378e2734da626542ccd4567b&=&format=webp&quality=lossless)

### Resultados de Pruebas

#### Pruebas Unitarias (Product Service)
**Framework:** JUnit 5 + Mockito
**Cobertura:**
- **ProductResourceTest:** Tests de endpoints REST (GET, POST, PUT, DELETE)
- **ProductServiceTest:** Tests de lógica de negocio con mocks
- **CategoryRepositoryTest:** Tests de persistencia JPA
- **ProductRepositoryTest:** Tests CRUD de productos

#### Pruebas de Integración (Product Service)
**Framework:** Spring Boot Test + TestRestTemplate
**Cobertura:**
- **ProductServiceIntegrationTest:** Tests end-to-end del servicio
- **ProductServiceIntegrationWithOtherServicesTest:** Tests de integración entre microservicios

**Casos probados:**
- Integración con Order Service
- Integración con Payment Service  
- Integración con Shipping Service
- Integración con User/Favorites Service
- Integración con API Gateway

#### Pruebas E2E (Postman/Newman)
**Herramienta:** Newman CLI
**Colección:** E2E-tests.postman_collection.json
**Flujos probados:**
- Creación de usuarios
- Gestión de productos
- Procesamiento de órdenes
- Gestión de favoritos
- Flujos completos de ecommerce

#### Pruebas de Carga (Locust) - en pipeline
**Configuración:**
- 100 usuarios concurrentes
- 20 usuarios/segundo de incremento
- 30 segundos de duración
- 8 endpoints probados simultáneamente

#### Pruebas de Carga (Locust) - manualmente usando interfaz gráfica.
**Configuración:**
- 1000 usuarios concurrentes
- 200 usuarios/segundo de incremento
- 3 minutos y 9 segundos de duración
- 8 endpoints probados simultáneamente

**Microservicios probados:**
- user-service/api/users
- product-service/api/products
- product-service/api/categories
- order-service/api/orders
- payment-service/api/payments
- shipping-service/api/shippings
- favourite-service/api/favourites

---

## Análisis de Resultados

### Pruebas de Rendimiento (Locust)
*El reporte completo de pruebas HTML se encuentra en la carpeta de entrega*

**Configuración del Test:** 1000 usuarios concurrentes, 3 minutos 9 segundos, 21,342 requests totales con 22.6% de fallos.

#### Clasificación de Servicios por Rendimiento

**🟢 Servicios Estables (0% fallos):**
- **order-service, user-service, product-service:** Latencias entre 170-264ms promedio, percentil 99 bajo 1,600ms

**🟡 Servicio con Optimización Requerida:**
- **favourite-service:** 0% fallos pero latencia excesiva (17s promedio), requiere optimización de consultas

**🔴 Servicios Críticos (>70% fallos):**
- **payment-service y shipping-service:** Tasas de fallo de 73.6% y 87.9% respectivamente, bloquean flujos de negocio críticos

#### Métricas Generales
- **Throughput:** 156 RPS máximo, ~112 RPS promedio
- **Distribución:** Carga uniforme entre endpoints (8.3% cada uno)
- **Impacto de Negocio:** Los fallos en payment y shipping impiden completar transacciones

#### Recomendaciones Principales

1. **Solucionar servicios críticos:** Revisar configuraciones de timeout y conexiones DB en payment-service y shipping-service
2. **Implementar resiliencia:** Circuit breakers y políticas de retry para prevenir cascadas de fallos
3. **Optimizar favourite-service:** Implementar caching y revisar consultas SQL

### Resumen de Pruebas Funcionales
- **Unitarias e Integración:** Exitosas en product-service con cobertura completa
- **E2E:** Validación exitosa de flujos completos de usuario  
- **Infraestructura:** Despliegue funcional en Kubernetes con service discovery operativo

---

## Release Notes

### Implementación Realizada
Se implementó un sistema completo de Release Notes automático que:

- **Genera automáticamente** releases en GitHub para ambiente PROD
- **Incluye información detallada** de build, commit, y validaciones
- **Documenta servicios desplegados** y puertos de acceso
- **Proporciona comandos útiles** para operaciones post-despliegue

#### Release Note en GitHub
![Release Note Generado en GitHub](https://media.discordapp.net/attachments/895756291941740624/1378181386569252864/image.png?ex=683baaef&is=683a596f&hm=89bc0157ca35f27141a88a4ec31b7dcc90e7060bf785cea233ca305b24606289&=&format=webp&quality=lossless)
