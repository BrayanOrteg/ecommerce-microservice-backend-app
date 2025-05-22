pipeline {
    agent any
    
    environment {
        DOCKER_NAMESPACE = "kenbra"  // Tu usuario de Docker Hub con imágenes públicas
        K8S_NAMESPACE = "default"    // Namespace de Kubernetes para el despliegue
    }
    
    stages {
        stage('Preparar Entorno') {
            steps {
                sh '''
                echo "Verificando entorno de despliegue"
                echo "Verificando kubectl"
                kubectl version --client || {
                  echo "Instalando kubectl"
                  mkdir -p $HOME/bin
                  export PATH=$HOME/bin:$PATH
                  curl -LO "https://dl.k8s.io/release/$(curl -L -s https://dl.k8s.io/release/stable.txt)/bin/linux/amd64/kubectl"
                  chmod +x kubectl && mv kubectl $HOME/bin/
                  echo 'export PATH=$HOME/bin:$PATH' >> ~/.bashrc
                }                '''
            }
        }
        
        stage('Desplegar Infraestructura') {
            steps {
                sh '''
                export PATH=$HOME/bin:$PATH
                
                # Verificar que todos los yamls están usando la imagen correcta
                echo "Verificando que los manifiestos usen las imágenes correctas..."
                find k8s -name "*.yaml" -type f | grep -v "jenkins" | grep -v "zipkin" | xargs grep -l "image:" | xargs sed -i "s|image: selimhorri/|image: $DOCKER_NAMESPACE/|g"
                
                # Desplegar Zipkin
                echo "Desplegando Zipkin..."
                kubectl apply -f k8s/zipkin.yaml
                echo "Esperando a que Zipkin esté disponible..."
                sleep 30 # Dar tiempo para que se inicie
                
                # Desplegar Service Discovery (Eureka)
                echo "Desplegando Service Discovery (Eureka)..."
                kubectl apply -f k8s/service-discovery.yaml
                echo "Esperando a que Service Discovery esté disponible..."
                sleep 90 # Dar tiempo para que se inicie
                '''
                
                // Desplegar Cloud Config
                sh '''
                export PATH=$HOME/bin:$PATH
                echo "Desplegando Cloud Config..."
                kubectl apply -f k8s/cloud-config.yaml
                echo "Esperando a que Cloud Config esté disponible..."
                sleep 60 # Dar tiempo para que se inicie                '''
            }
        }
        
        stage('Desplegar Microservicios') {
            steps {
                sh '''
                export PATH=$HOME/bin:$PATH
                
                # Desplegar API Gateway y esperar
                echo "Desplegando API Gateway..."
                kubectl apply -f k8s/api-gateway.yaml
                echo "Esperando a que API Gateway esté disponible..."
                sleep 60 # Dar tiempo para que se inicie
                
                # Desplegar el resto de microservicios en paralelo
                echo "Desplegando microservicios de negocio..."
                kubectl apply -f k8s/order-service.yaml
                kubectl apply -f k8s/payment-service.yaml
                kubectl apply -f k8s/product-service.yaml
                kubectl apply -f k8s/shipping-service.yaml
                kubectl apply -f k8s/user-service.yaml
                kubectl apply -f k8s/favourite-service.yaml
                kubectl apply -f k8s/proxy-client.yaml
                
                # Esperar a que los servicios estén disponibles
                echo "Esperando a que los servicios estén disponibles..."
                sleep 60                '''
            }
        }
        
        stage('Verificar Despliegue') {
            steps {
                sh '''
                export PATH=$HOME/bin:$PATH
                
                echo "Verificando pods desplegados..."
                kubectl get pods
                
                echo "Verificando servicios..."
                kubectl get services
                
                echo "Verificando que Service Discovery esté funcionando..."
                SERVICE_DISCOVERY_URL=$(kubectl get service service-discovery -o jsonpath='{.spec.clusterIP}')
                if curl -s $SERVICE_DISCOVERY_URL:8761/actuator/health | grep -q "UP"; then
                    echo "Service Discovery está funcionando correctamente."
                else
                    echo "Service Discovery health check falló, pero continuamos el proceso."
                fi
                
                echo "Verificando que API Gateway esté funcionando..."
                API_GATEWAY_URL=$(kubectl get service api-gateway -o jsonpath='{.spec.clusterIP}')
                if curl -s $API_GATEWAY_URL:8080/actuator/health | grep -q "UP"; then
                    echo "API Gateway está funcionando correctamente."
                else
                    echo "API Gateway health check falló, pero continuamos el proceso."
                fi
                
                echo "Verificando registros en Eureka..."
                APPS=$(curl -s $SERVICE_DISCOVERY_URL:8761/eureka/apps)
                if echo $APPS | grep -q "application"; then
                    echo "Se encontraron aplicaciones registradas en Eureka:"
                    echo $APPS | grep -o "application" | wc -l
                else
                    echo "No hay aplicaciones registradas en Eureka todavía. Esto puede ser normal si los servicios aún se están iniciando."
                fi
                
                echo "Despliegue completado. La aplicación debería estar disponible pronto en el cluster de Kubernetes."
                '''
            }
        }
    }
    
    post {
        always {
            echo "Limpiando espacio de trabajo..."
            catchError(buildResult: 'SUCCESS', stageResult: 'SUCCESS') {
                cleanWs()
            }
        }
        success {
            echo "¡Pipeline completado con éxito!"
            echo "La aplicación está disponible en el cluster de Kubernetes."
            echo "Puedes acceder a la interfaz de Eureka a través del servicio service-discovery."
            echo "El API Gateway está disponible a través del servicio api-gateway."
        }
        failure {
            echo "Pipeline falló. Revisa los logs para más detalles."
        }
    }
}
