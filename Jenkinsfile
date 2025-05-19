pipeline {
    agent {
        docker {
            image 'maven:3.9.6-eclipse-temurin-17'
            args '-v /var/run/docker.sock:/var/run/docker.sock'
        }
    }
    environment {
        REGISTRY = "tu-registro-docker"
        // KUBE_CONFIG = credentials('kubeconfig-jenkins') // Comentado para evitar error si la credencial no existe
    }
    // Se elimina la sección tools para evitar errores de configuración
    stages {
        stage('Preparar herramientas') {
            steps {
                sh '''
                echo "Verificando Java y Maven"
                java -version
                mvn -version
                echo "Instalando kubectl y minikube si es necesario"
                if ! command -v kubectl; then
                  curl -LO https://storage.googleapis.com/kubernetes-release/release/$(curl -s https://storage.googleapis.com/kubernetes-release/release/stable.txt)/bin/linux/amd64/kubectl
                  chmod +x kubectl && mv kubectl /usr/local/bin/
                fi
                if ! command -v minikube; then
                  curl -Lo minikube https://storage.googleapis.com/minikube/releases/latest/minikube-linux-amd64
                  chmod +x minikube && mv minikube /usr/local/bin/
                fi
                '''
            }
        }
        stage('Build') {
            steps {
                sh 'mvn clean package -DskipTests'
            }
        }
        stage('Deploy Jenkins en K8s') {
            steps {
                sh '''
                echo "Aplicando PV y Deployment de Jenkins en Kubernetes"
                kubectl apply -f k8s/jenkins-pv.yaml
                kubectl apply -f k8s/jenkins-deployment.yaml
                '''
            }
        }
        stage('Deploy Microservicios en Minikube') {
            steps {
                sh '''
                echo "Aplicando todos los manifiestos de la carpeta k8s (excepto Jenkins)"
                for file in k8s/*.yaml; do
                  if [[ "$file" != *jenkins* ]]; then
                    kubectl apply -f "$file"
                  fi
                done
                '''
            }
        }
    }
}
