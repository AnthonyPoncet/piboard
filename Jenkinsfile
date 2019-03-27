pipeline {
    agent any

    options {
        disableConcurrentBuilds()
        buildDiscarder(logRotator(daysToKeepStr: '5', artifactDaysToKeepStr: '5'))
    }

    stages {
        stage ("build") {
            steps {
                script {
                    sh './gradlew build'
                }
            }
        }
        stage ("test") {
            steps {
                script {
                    sh './gradlew test'
                }
            }
        }
        stage ("package") {
            steps {
                script {
                    sh './gradlew jar'
                }
            }
        }
    }
}