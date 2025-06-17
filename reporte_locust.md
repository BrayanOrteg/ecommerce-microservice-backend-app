# 📊 Observaciones sobre Pruebas de Estrés

El reporte generado por Locust se encuentra incluido en la entrega, correspondiente a la simulación de carga sobre el sistema desplegado en Azure Kubernetes Service (AKS), con infraestructura aprovisionada mediante Terraform. A continuación, se presentan los hallazgos y análisis más relevantes:

Actualización

---

## ⏱️ Tiempo de Respuesta

### **Promedio**
Se observaron valores promedio de respuesta en torno a los **279.82 milisegundos (0.28 segundos)**, lo cual sugiere latencias relativamente bajas bajo la carga aplicada. Esto es considerado aceptable para la mayoría de aplicaciones web interactivas.

### **Mediana (P50)**
Se ubicó alrededor de los **17 ms**, indicando que la mayoría de las solicitudes tuvieron respuestas muy rápidas.

### **Pico (P95)**
Alcanza hasta **1,900 ms**, lo cual indica que al menos el 5% de las peticiones se tardaron más de 1.9 segundos.

**Análisis:** Los tiempos de respuesta muestran una distribución variable, con la mayoría de solicitudes siendo muy rápidas, pero algunas experimentando latencias más altas, posiblemente debido a consultas más complejas o carga en servicios específicos.

---

## 🚀 Throughput (Solicitudes por Segundo)

### **Máximo observado**
Aproximadamente **1.9 solicitudes por segundo** en el agregado total.

### **Estabilidad**
Se mantiene entre **1.2 y 1.9 req/s** a lo largo de la prueba, con variaciones según la fase de la prueba.

La baja cantidad de solicitudes por segundo se debe a que esta fue una prueba de carga limitada con pocos usuarios concurrentes (máximo 5 usuarios), lo que explica el throughput reducido pero estable del sistema.

---

## ✅ Tasa de Errores

### **Errores reportados**
Se identificaron **20 errores HTTP 500** (Internal Server Error) distribuidos en dos servicios:

- **Favourite-service**: 16 errores en el endpoint `/favourite-service/api/favourites`
- **Shipping-service**: 4 errores en el endpoint `/shipping-service/api/shippings`

Estos errores representan fallas internas del servidor que requieren atención inmediata, ya que indican problemas en la lógica de negocio, conexiones a base de datos o configuración de los microservicios afectados.

**Tasa de error total**: Aproximadamente **44.4%** (20 errores de 45 solicitudes totales), lo cual es una tasa crítica que requiere intervención urgente.

---

# 📋 Conclusiones

- **Problemas críticos de estabilidad:** El sistema presenta una tasa de errores del 44.4% (20 errores de 45 solicitudes), indicando fallas significativas en dos microservicios.

- **Servicios afectados:** Los servicios de favoritos y envíos muestran errores internos del servidor (HTTP 500) que requieren investigación inmediata.

- **Rendimiento mixto:** Aunque los tiempos de respuesta para las solicitudes exitosas son aceptables (279.82 ms promedio), la alta tasa de errores compromete severamente la experiencia del usuario.

- **Impacto en la funcionalidad:** Las fallas en servicios críticos como favoritos y envíos afectan directamente funcionalidades esenciales del e-commerce.

---

# 🛠️ Recomendaciones

## ⚡ Acciones Inmediatas (Críticas)

- **Investigar errores HTTP 500**: Revisar logs de los servicios `favourite-service` y `shipping-service` para identificar la causa raíz de las fallas.
- **Verificar conectividad de base de datos**: Confirmar que ambos servicios pueden acceder correctamente a sus respectivas bases de datos.
- **Revisar configuración**: Validar variables de entorno, cadenas de conexión y configuraciones de red.

## 🎯 Optimización de Backend

- **Manejo de excepciones**: Implementar un manejo robusto de errores en los microservicios afectados.
- **Logging mejorado**: Añadir logs detallados para facilitar el debugging de errores internos.
- **Validación de datos**: Verificar que los endpoints manejen correctamente las validaciones de entrada.

## 🛡️ Resiliencia del Sistema

- **Circuit Breaker**: Implementar el patrón Circuit Breaker para prevenir cascadas de fallas.
- **Retry policies**: Configurar políticas de reintento para operaciones fallidas.
- **Health checks**: Implementar verificaciones de salud más robustas para detectar problemas tempranamente.

## 🖥️ Infraestructura y Monitoreo

- **Alertas en tiempo real**: Configurar alertas inmediatas para errores HTTP 500 en Prometheus/Grafana.
- **Análisis de recursos**: Verificar que los pods tengan suficientes recursos (CPU/memoria) asignados.
- **Escalamiento**: Asegurar que los servicios puedan escalar bajo carga.

## ✅ Validación Post-Fix

- **Re-ejecutar pruebas**: Una vez corregidos los errores, repetir las pruebas de carga para validar las correcciones.
- **Pruebas unitarias**: Implementar tests que cubran los escenarios que causaron los errores HTTP 500.
- **Monitoreo continuo**: Establecer supervisión permanente de estos servicios críticos.

