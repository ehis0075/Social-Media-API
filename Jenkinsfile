pipeline {
  agent any
  stages {
    stage("build") {
      steps {
        sh """
          docker build -t social-service-docker:latest .
        """
      }
    }
    stage("remove-old") {
      steps {
        sh """
          docker rm -f social-service
        """
      }
    }
    stage("run") {
      steps {
        sh """
          docker run -d -p 8082:8082 -e TZ=Africa/Lagos --name social-service social-service-docker
        """
      }
    }
  }
}