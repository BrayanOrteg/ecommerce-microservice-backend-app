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
                echo "Instalando Maven si es necesario"
                if ! command -v mvn; then
                  MAVEN_VERSION=3.9.6
                  curl -fsSL https://dlcdn.apache.org/maven/maven-3/$MAVEN_VERSION/binaries/apache-maven-$MAVEN_VERSION-bin.tar.gz -o maven.tar.gz
                  tar -xzf maven.tar.gz
                  mv apache-maven-$MAVEN_VERSION /opt/maven
                  export PATH=/opt/maven/bin:$PATH
                  echo 'export PATH=/opt/maven/bin:$PATH' >> ~/.bashrc
                fi
                export PATH=/opt/maven/bin:$PATH
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
                sh '''
                export PATH=/opt/maven/bin:$PATH
                mvn clean package -DskipTests
                '''
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
