pipeline {
    agent any
    
    environment {
        DOCKER_REGISTRY = "docker.io"  // Puedes cambiar a tu registro si usas uno privado
        DOCKER_NAMESPACE = "tuusuario"  // Reemplaza con tu usuario de Docker Hub o namespace
        VERSION = "0.1.0"
        DOCKERHUB_CREDENTIALS = credentials('dockerhub')  // Crear estas credenciales en Jenkins
        K8S_NAMESPACE = "default"  // Namespace de Kubernetes para el despliegue
    }
    
    stages {
        stage('Preparar Entorno') {
            steps {
                sh '''
                echo "Verificando Java"
                java -version
                echo "Verificando Docker"
                docker --version
                echo "Verificando kubectl"
                kubectl version --client || {
                  echo "Instalando kubectl"
                  mkdir -p $HOME/bin
                  export PATH=$HOME/bin:$PATH
                  curl -LO "https://dl.k8s.io/release/$(curl -L -s https://dl.k8s.io/release/stable.txt)/bin/linux/amd64/kubectl"
                  chmod +x kubectl && mv kubectl $HOME/bin/
                  echo 'export PATH=$HOME/bin:$PATH' >> ~/.bashrc
                }
                '''
            }
        }
        
        stage('Verificar Imágenes Docker') {
            steps {
                sh 'echo $DOCKERHUB_CREDENTIALS_PSW | docker login -u $DOCKERHUB_CREDENTIALS_USR --password-stdin'
                
                sh '''
                # Verificar que las imágenes existen en DockerHub
                echo "Verificando disponibilidad de imágenes en DockerHub..."
                
                SERVICES=(
                    "service-discovery"
                    "cloud-config"
                    "api-gateway"
                    "order-service"
                    "payment-service"
                    "product-service"
                    "shipping-service"
                    "user-service"
                    "favourite-service"
                    "proxy-client"
                )
                
                for SERVICE in "${SERVICES[@]}"; do
                    IMAGE_NAME="$DOCKER_NAMESPACE/${SERVICE}-ecommerce-boot:$VERSION"
                    echo "Verificando imagen: $IMAGE_NAME"
                    
                    # Intentar obtener información de la imagen
                    docker pull $IMAGE_NAME || {
                        echo "ERROR: No se pudo encontrar o acceder a la imagen $IMAGE_NAME"
                        echo "Por favor, ejecuta build-push-docker-images.ps1 primero para construir y subir todas las imágenes."
                        exit 1
                    }
                done
                
                echo "Todas las imágenes están disponibles en DockerHub"
                '''
                
                sh 'docker logout'
            }
        }
          stage('Desplegar Infraestructura') {
            steps {
                // Actualizar los archivos YAML con las imágenes personalizadas
                sh '''
                export PATH=$HOME/bin:$PATH
                
                # Actualizar todos los archivos Kubernetes para usar las imágenes personalizadas
                find k8s -name "*.yaml" -type f | grep -v "jenkins\|zipkin" | xargs sed -i "s|image: selimhorri/|image: $DOCKER_NAMESPACE/|g"
                
                # Desplegar Zipkin
                kubectl apply -f k8s/zipkin.yaml
                sleep 30 # Dar tiempo para que se inicie
                '''
                
                // Desplegar Service Discovery (Eureka)
                sh '''
                export PATH=$HOME/bin:$PATH
                kubectl apply -f k8s/service-discovery.yaml
                sleep 180 # Dar tiempo para que se inicie
                '''
                
                // Verificar que Service Discovery está listo
                sh '''
                export PATH=$HOME/bin:$PATH
                READY=false
                for i in {1..12}; do
                  if kubectl get pods -l app=service-discovery -o jsonpath='{.items[0].status.conditions[?(@.type=="Ready")].status}' | grep -q "True"; then
                    READY=true
                    break
                  fi
                  echo "Esperando a que Service Discovery esté listo..."
                  sleep 10
                done
                if [ "$READY" = false ]; then
                  echo "Service Discovery no está listo después de 2 minutos"
                  exit 1
                fi
                '''
                
                // Desplegar Cloud Config
                sh '''
                export PATH=$HOME/bin:$PATH
                kubectl apply -f k8s/cloud-config.yaml
                sleep 90 # Dar tiempo para que se inicie
                '''
            }
        }
        
        stage('Desplegar Microservicios') {
            steps {
                sh '''
                export PATH=$HOME/bin:$PATH
                # Desplegar API Gateway y esperar
                kubectl apply -f k8s/api-gateway.yaml
                sleep 60 # Dar tiempo para que se inicie
                
                # Desplegar el resto de microservicios
                kubectl apply -f k8s/order-service.yaml
                kubectl apply -f k8s/payment-service.yaml
                kubectl apply -f k8s/product-service.yaml
                kubectl apply -f k8s/shipping-service.yaml
                kubectl apply -f k8s/user-service.yaml
                kubectl apply -f k8s/favourite-service.yaml
                kubectl apply -f k8s/proxy-client.yaml
                '''
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
                SERVICE_DISCOVERY_URL=$(minikube service service-discovery --url 2>/dev/null || kubectl get service service-discovery -o jsonpath='{.spec.clusterIP}')
                curl -s $SERVICE_DISCOVERY_URL:8761/actuator/health | grep "UP" || echo "Service Discovery health check falló"
                
                echo "Verificando que API Gateway esté funcionando..."
                API_GATEWAY_URL=$(minikube service api-gateway --url 2>/dev/null || kubectl get service api-gateway -o jsonpath='{.spec.clusterIP}')
                curl -s $API_GATEWAY_URL:8080/actuator/health | grep "UP" || echo "API Gateway health check falló"
                '''
            }
        }
    }
    
    post {
        always {
            cleanWs()
        }
        success {
            echo "¡Pipeline completado con éxito!"
        }
        failure {
            echo "Pipeline falló. Revisa los logs para más detalles."
        }
    }
}
