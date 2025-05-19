pipeline {
    agent any
    environment {
        REGISTRY = "tu-registro-docker"
        // KUBE_CONFIG = credentials('kubeconfig-jenkins') // Comentado para evitar error si la credencial no existe
    }
    stages {
        stage('Preparar herramientas') {
            steps {
                sh '''
                echo "Verificando Java"
                java -version
                echo "Verificando Maven Wrapper"
                ./mvnw -version || { echo 'Maven Wrapper no encontrado'; exit 1; }
                echo "Instalando kubectl y minikube si es necesarioo"
                mkdir -p $HOME/bin
                export PATH=$HOME/bin:$PATH
                if ! command -v kubectl; then
                  curl -LO https://storage.googleapis.com/kubernetes-release/release/$(curl -s https://storage.googleapis.com/kubernetes-release/release/stable.txt)/bin/linux/amd64/kubectl
                  chmod +x kubectl && mv kubectl $HOME/bin/
                fi
                if ! command -v minikube; then
                  curl -Lo minikube https://storage.googleapis.com/minikube/releases/latest/minikube-linux-amd64
                  chmod +x minikube && mv minikube $HOME/bin/
                fi
                echo 'export PATH=$HOME/bin:$PATH' >> ~/.bashrc
                '''
            }
        }
        stage('Build') {
            steps {
                sh './mvnw clean package -DskipTests'
            }
        }
        stage('Deploy Jenkins en K8s') {
            steps {
                sh '''
                export PATH=$HOME/bin:$PATH
                echo "Aplicando PV y Deployment de Jenkins en Kubernetes"
                kubectl apply -f k8s/jenkins-pv.yaml
                kubectl apply -f k8s/jenkins-deployment.yaml
                '''
            }
        }
        stage('Deploy Microservicios en Minikube') {
            steps {
                sh '''
                export PATH=$HOME/bin:$PATH
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
