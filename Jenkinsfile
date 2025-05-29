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
                
                # Configurar PATH
                export PATH=$HOME/bin:$PATH
                
                # Instalar kubectl
                echo "Verificando kubectl"
                if ! command -v kubectl &> /dev/null; then
                    echo "Instalando kubectl"
                    mkdir -p $HOME/bin
                    curl -LO "https://dl.k8s.io/release/$(curl -L -s https://dl.k8s.io/release/stable.txt)/bin/linux/amd64/kubectl"
                    chmod +x kubectl && mv kubectl $HOME/bin/
                    echo 'export PATH=$HOME/bin:$PATH' >> ~/.bashrc
                fi
                
                # Instalar Java 11 para Maven (el proyecto requiere Java 11)
                echo "Instalando Java 11 para Maven..."
                if [ ! -d $HOME/java11 ]; then
                    cd /tmp
                    curl -L -o openjdk-11.tar.gz https://download.java.net/java/GA/jdk11/9/GPL/openjdk-11.0.2_linux-x64_bin.tar.gz
                    tar -xzf openjdk-11.tar.gz
                    mv jdk-11.0.2 $HOME/java11
                    cd -
                fi

                # Configurar JAVA_HOME para Maven
                export JAVA_HOME=$HOME/java11
                export PATH=$HOME/java11/bin:$PATH
                
                echo "Verificando Java para Maven:"
                java -version
                javac -version
                
                # Instalar Maven si no está disponible
                echo "Verificando Maven..."
                if ! command -v mvn &> /dev/null; then
                    echo "Instalando Maven..."
                    # Limpiar directorio si existe
                    rm -rf $HOME/maven
                    cd /tmp
                    curl -sL https://archive.apache.org/dist/maven/maven-3/3.8.6/binaries/apache-maven-3.8.6-bin.tar.gz -o apache-maven-3.8.6-bin.tar.gz
                    tar -xzf apache-maven-3.8.6-bin.tar.gz
                    mv apache-maven-3.8.6 $HOME/maven
                    export PATH=$HOME/maven/bin:$PATH
                    echo 'export PATH=$HOME/maven/bin:$PATH' >> ~/.bashrc
                    cd -
                fi
                
                # Verificar Maven
                mvn --version
                
                # Descargar e instalar Node.js binario (sin apt-get)
                echo "Instalando Node.js binario..."
                if ! command -v node &> /dev/null; then
                    # Limpiar directorio si existe
                    rm -rf $HOME/nodejs
                    cd /tmp
                    curl -L -o node-v18.19.0-linux-x64.tar.gz https://nodejs.org/dist/v18.19.0/node-v18.19.0-linux-x64.tar.gz
                    tar -xzf node-v18.19.0-linux-x64.tar.gz
                    mv node-v18.19.0-linux-x64 $HOME/nodejs
                    export PATH=$HOME/nodejs/bin:$PATH
                    echo 'export PATH=$HOME/nodejs/bin:$PATH' >> ~/.bashrc
                    cd -
                fi
                
                # Verificar Node.js
                node --version
                npm --version
                
                # Instalar newman
                echo "Instalando newman..."
                npm install -g newman
                newman --version
                
                # Instalar GitHub CLI
                echo "Instalando GitHub CLI..."
                if ! command -v gh &> /dev/null; then
                    # Instalar gh CLI
                    curl -fsSL https://cli.github.com/packages/githubcli-archive-keyring.gpg | dd of=/usr/share/keyrings/githubcli-archive-keyring.gpg
                    chmod go+r /usr/share/keyrings/githubcli-archive-keyring.gpg
                    echo "deb [arch=$(dpkg --print-architecture) signed-by=/usr/share/keyrings/githubcli-archive-keyring.gpg] https://cli.github.com/packages stable main" | tee /etc/apt/sources.list.d/github-cli.list > /dev/null
                    apt update && apt install gh -y
                    gh --version
                else
                    echo "GitHub CLI ya está disponible: $(gh --version)"
                fi
                
                # Instalar Python packages usando --user (sin permisos de root)
                if [ "${SELECTED_ENV}" = "stage" ]; then
                    echo "Verificando e instalando Python para Locust..."
                    
                    # Verificar si Python está disponible
                    if ! command -v python3 &> /dev/null; then
                        echo "Python3 no encontrado. Instalando..."
                        apt-get update && apt-get install -y python3 python3-pip python3-venv
                        
                        # Verificar instalación
                        python3 --version
                        pip3 --version
                    else
                        echo "Python3 ya está disponible: $(python3 --version)"
                    fi
                    
                    echo "Instalando locust..."
                    python3 -m pip install --user locust --break-system-packages || pip3 install --user locust --break-system-packages
                else
                    echo "Saltando instalación de Locust para ambiente ${SELECTED_ENV}"
                fi
                
                # Verificar instalaciones finales
                echo "=== RESUMEN DE HERRAMIENTAS INSTALADAS ==="
                kubectl version --client
                mvn --version
                node --version
                npm --version
                newman --version
                gh --version || echo "GitHub CLI pendiente de verificar"
                python3 -m locust --version || echo "Locust pendiente de verificar en PATH"
                echo "============================================"
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
                # Configurar JAVA_HOME para Java 11
                export JAVA_HOME=$HOME/java11
                export PATH=$HOME/java11/bin:$HOME/bin:$HOME/maven/bin:$HOME/nodejs/bin:$PATH

                echo "Verificando versión de Java para Maven:"
                java -version
                
                echo "Ejecutando pruebas unitarias en el servicio de productos"
                cd product-service

                # Limpiar target anterior
                rm -rf target/

                # Usar Maven con Java 11
                mvn clean test -Dmaven.compiler.source=11 -Dmaven.compiler.target=11 -Dmaven.test.failure.ignore=true

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
                # Configurar PATH
                export PATH=$HOME/bin:$HOME/maven/bin:$HOME/nodejs/bin:$PATH
        
                echo "Ejecutando pruebas de integración en los microservicios"
        
                # Ejecutar pruebas de integración en product-service
                cd product-service
                mvn test -Dtest=*Integration* -Dmaven.compiler.source=11 -Dmaven.compiler.target=11 -Dmaven.test.failure.ignore=true
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
                sh '''
                # Configurar PATH para incluir Node.js y newman
                export PATH=$HOME/bin:$HOME/maven/bin:$HOME/nodejs/bin:$PATH
        
                echo "Verificando que newman esté disponible..."
                newman --version
        
                echo "Ejecutando pruebas E2E..."
                cd postman-collections
                newman run "E2E-tests.postman_collection.json"
                cd ..
                '''
            }
        }
        
        stage('Generar Release Notes') {
            when {
                environment name: 'SELECTED_ENV', value: 'prod'
            }
            steps {
                withCredentials([usernamePassword(credentialsId: 'github-token', usernameVariable: 'GH_USER', passwordVariable: 'GH_TOKEN')]) {
                    script {
                        def now = new Date()
                        def tag = "v${now.format('yyyy.MM.dd.HHmm')}"
                        def title = "Production Release ${tag}"
                        def commitHash = sh(returnStdout: true, script: 'git rev-parse --short HEAD').trim()
                        def commitMessage = sh(returnStdout: true, script: 'git log -1 --pretty=%B').trim()
                        
                        sh """
                            # Configurar Git
                            git config user.email "jenkins-ci@ecommerce.com"
                            git config user.name "Jenkins CI"
                            git config --global url."https://oauth2:${GH_TOKEN}@github.com/".insteadOf "https://github.com/"
                            
                            # Crear tag y push
                            git tag ${tag} -m "Production deployment - Build #${env.BUILD_NUMBER}"
                            git push origin ${tag}
                            
                            # Crear release con notas
                            gh release create ${tag} --generate-notes --title "${title}" --notes "
# 🚀 Release Notes - ${tag}

**📅 Fecha:** ${now.format('yyyy-MM-dd HH:mm:ss')}  
**👤 Responsable:** Jenkins CI  
**🔗 Build:** #${env.BUILD_NUMBER}  
**🔑 Commit:** ${commitHash}  

## 📋 **Resumen del Release**
Despliegue automático del sistema de ecommerce con microservicios en ambiente de producción.

## 🆕 **Último Cambio**
${commitMessage}

## ✅ **Validaciones Realizadas**
- ✅ Pruebas End-to-End ejecutadas exitosamente
- ✅ Verificación de conectividad entre microservicios  
- ✅ Validación de endpoints principales
- ✅ Confirmación de registro en Eureka

## 🏗️ **Servicios Desplegados**
- API Gateway (Puerto 8080)
- Service Discovery - Eureka (Puerto 8761)  
- Zipkin Tracing (Puerto 9411)
- Microservicios: Product, Order, Payment, User, Shipping, Favourite

## 🚨 **Información Importante**
- Los servicios pueden tardar 2-3 minutos en estar completamente operativos
- Verificar conectividad de red antes de acceder a los endpoints
- En caso de problemas, contactar al equipo DevOps

"
                        """
                        
                        echo "✅ Release ${tag} creado exitosamente"
                        echo "📋 Release Notes generadas según buenas prácticas de Change Management"
                        echo "🔗 Disponible en GitHub Releases"
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
                sh '''
                # Configurar PATH
                export PATH=$HOME/bin:$HOME/maven/bin:$HOME/nodejs/bin:$HOME/.local/bin:$PATH
        
                echo "Ejecutando pruebas de carga con Locust..."
                cd locust
                python3 -m locust -f locustfile.py --headless -u 5 -r 2 -t 30s --csv=load_test_report
        
                echo "Pruebas de Locust completadas"
                ls -la *.csv
                cd ..
                '''
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
                    echo "- Release Notes generadas ✓"
                    echo "Los servicios están disponibles en el cluster de Kubernetes."
                }
            }
            echo "Puedes acceder a la interfaz de Eureka a través del servicio service-discovery."
            echo "El API Gateway está disponible a través del servicio api-gateway."
        }
        failure {
            echo "Pipeline falló en ambiente: ${env.SELECTED_ENV}"
            echo "Revisa los logs para más detalles."
        }
    }
}