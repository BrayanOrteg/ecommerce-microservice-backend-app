pipeline {
    agent any
    
    parameters {
        choice(
            name: 'ENVIRONMENT',
            choices: ['dev', 'stage', 'prod'],
            description: 'Selecciona el ambiente de despliegue'
        )
    }
    
    environment {
        DOCKER_NAMESPACE = "kenbra"  // Tu usuario de Docker Hub con imágenes públicas
        K8S_NAMESPACE = "default"    // Namespace de Kubernetes para el despliegue
        SELECTED_ENV = "${params.ENVIRONMENT}"
    }
      stages {
        stage('Preparar Entorno') {
            steps {
                sh '''
                echo "Verificando entorno de despliegue para: ${SELECTED_ENV}"
                echo "Verificando kubectl"
                kubectl version --client || {
                  echo "Instalando kubectl"
                  mkdir -p $HOME/bin
                  export PATH=$HOME/bin:$PATH
                  curl -LO "https://dl.k8s.io/release/$(curl -L -s https://dl.k8s.io/release/stable.txt)/bin/linux/amd64/kubectl"
                  chmod +x kubectl && mv kubectl $HOME/bin/
                  echo 'export PATH=$HOME/bin:$PATH' >> ~/.bashrc
                }
                
                # Instalar newman para tests E2E si no está instalado
                which newman || {
                    echo "Instalando newman para tests E2E"
                    npm install -g newman
                }
                
                # Instalar python y locust si no están instalados
                which python3 || which python || {
                    echo "Python no encontrado. Asegúrate de que Python esté instalado."
                }
                '''
            }
        }
        
        stage('Ejecutar Pruebas Unitarias') {
            when {
                anyOf {
                    environment name: 'SELECTED_ENV', value: 'stage'
                }
            }
            steps {
                sh '''
                echo "Ejecutando pruebas unitarias en el servicio de productos"
                cd product-service
                ./mvnw test || mvn test
                cd ..
                '''
            }
            post {
                always {
                    junit allowEmptyResults: true, testResults: '**/target/surefire-reports/*.xml'
                }
            }
        }
        
        stage('Ejecutar Pruebas de Integración') {
            when {
                anyOf {
                    environment name: 'SELECTED_ENV', value: 'stage'
                }
            }
            steps {
                sh '''
                echo "Ejecutando pruebas de integración en los microservicios"
                
                # Configurar la base de datos de pruebas si es necesario
                
                # Ejecutar pruebas de integración en product-service
                cd product-service
                ./mvnw test -Dtest=*Integration* || mvn test -Dtest=*Integration*
                cd ..
                '''
            }
            post {
                always {
                    junit allowEmptyResults: true, testResults: '**/target/surefire-reports/*Integration*.xml'
                }
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
                
                # Esperar un poco más para que los servicios estén completamente listos
                echo "Esperando 60 segundos adicionales para que los servicios estén completamente listos..."
                sleep 60
                '''
            }
        }
        
        stage('Ejecutar Pruebas E2E') {
            when {
                anyOf {
                    environment name: 'SELECTED_ENV', value: 'stage'
                    environment name: 'SELECTED_ENV', value: 'prod'
                }
            }
            steps {
                script {
                    // Iniciar port-forward en background
                    def portForwardProcess = sh(
                        script: '''
                        export PATH=$HOME/bin:$PATH
                        echo "Iniciando port-forward para API Gateway..."
                        nohup kubectl port-forward svc/api-gateway 8080:8080 > port-forward.log 2>&1 &
                        echo $! > port-forward.pid
                        sleep 10
                        echo "Port-forward iniciado"
                        ''',
                        returnStdout: false
                    )
                    
                    try {
                        // Ejecutar tests E2E
                        sh '''
                        echo "Esperando a que el port-forward esté listo..."
                        sleep 15
                        
                        echo "Ejecutando pruebas E2E..."
                        cd postman-collections
                        newman run "E2E-tests.postman_collection.json"
                        cd ..
                        '''
                    } finally {
                        // Limpiar port-forward
                        sh '''
                        echo "Terminando port-forward..."
                        if [ -f port-forward.pid ]; then
                            PID=$(cat port-forward.pid)
                            kill $PID || echo "Port-forward ya había terminado"
                            rm -f port-forward.pid
                        fi
                        pkill -f "kubectl port-forward svc/api-gateway" || echo "No hay procesos port-forward activos"
                        '''
                    }
                }
            }
        }
        
        stage('Ejecutar Pruebas de Carga (Locust)') {
            when {
                anyOf {
                    environment name: 'SELECTED_ENV', value: 'stage'
                }
            }
            steps {
                script {
                    // Iniciar port-forward en background
                    def portForwardProcess = sh(
                        script: '''
                        export PATH=$HOME/bin:$PATH
                        echo "Iniciando port-forward para API Gateway (Locust)..."
                        nohup kubectl port-forward svc/api-gateway 8080:8080 > port-forward-locust.log 2>&1 &
                        echo $! > port-forward-locust.pid
                        sleep 10
                        echo "Port-forward para Locust iniciado"
                        ''',
                        returnStdout: false
                    )
                    
                    try {
                        // Ejecutar tests de Locust
                        sh '''
                        echo "Esperando a que el port-forward esté listo..."
                        sleep 15
                        
                        echo "Instalando dependencias de Locust..."
                        cd locust
                        pip install -r requirements.txt
                        
                        echo "Ejecutando pruebas de carga con Locust..."
                        python -m locust -f locustfile.py --headless -u 5 -r 2 -t 30s --csv=load_test_report
                        
                        echo "Pruebas de Locust completadas"
                        ls -la *.csv
                        cd ..
                        '''
                    } finally {
                        // Limpiar port-forward
                        sh '''
                        echo "Terminando port-forward de Locust..."
                        if [ -f port-forward-locust.pid ]; then
                            PID=$(cat port-forward-locust.pid)
                            kill $PID || echo "Port-forward de Locust ya había terminado"
                            rm -f port-forward-locust.pid
                        fi
                        pkill -f "kubectl port-forward svc/api-gateway" || echo "No hay procesos port-forward activos"
                        '''
                    }
                }
            }
            post {
                always {
                    // Archivar reportes de Locust
                    archiveArtifacts artifacts: 'locust/load_test_report*.csv', allowEmptyArchive: true
                }
            }
        }
    }
      post {
        always {
            echo "Limpiando espacio de trabajo..."
            
            // Limpiar cualquier port-forward que pueda haber quedado
            sh '''
            echo "Limpiando port-forwards restantes..."
            pkill -f "kubectl port-forward" || echo "No hay port-forwards activos"
            rm -f port-forward*.pid port-forward*.log || echo "No hay archivos de port-forward para limpiar"
            '''
            
            catchError(buildResult: 'SUCCESS', stageResult: 'SUCCESS') {
                cleanWs()
            }
        }
        success {
            script {
                if (env.SELECTED_ENV == 'dev') {
                    echo "¡Pipeline DEV completado con éxito!"
                    echo "Los microservicios están desplegados y listos para desarrollo."
                } else if (env.SELECTED_ENV == 'stage') {
                    echo "¡Pipeline STAGE completado con éxito!"
                    echo "Todas las pruebas han pasado exitosamente:"
                    echo "- Pruebas unitarias ✓"
                    echo "- Pruebas de integración ✓"
                    echo "- Pruebas E2E ✓"
                    echo "- Pruebas de carga (Locust) ✓"
                    echo "La aplicación está lista para producción."
                } else if (env.SELECTED_ENV == 'prod') {
                    echo "¡Pipeline PROD completado con éxito!"
                    echo "La aplicación está desplegada en producción."
                    echo "- Pruebas E2E ✓"
                    echo "Los servicios están disponibles en el cluster de Kubernetes."
                }
            }
            echo "Puedes acceder a la interfaz de Eureka a través del servicio service-discovery."
            echo "El API Gateway está disponible a través del servicio api-gateway."
        }
        failure {
            script {
                echo "Pipeline falló en ambiente: ${env.SELECTED_ENV}"
                echo "Revisa los logs para más detalles."
                
                // Limpiar port-forwards en caso de fallo
                sh '''
                echo "Limpiando port-forwards debido a fallo..."
                pkill -f "kubectl port-forward" || echo "No hay port-forwards activos"
                rm -f port-forward*.pid port-forward*.log || echo "No hay archivos de port-forward para limpiar"
                '''
            }
        }
    }
}
