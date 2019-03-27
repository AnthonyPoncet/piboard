pipeline {
    options {
        disableConcurrentBuilds()
        buildDiscarder(logRotator(daysToKeepStr: '5', artifactDaysToKeepStr: '5'))
    }

    stages {
        stage ("build") {
            sh './gradlew build'
        }
        stage ("test") {
            sh './gradlew test'
        }
        stage ("package") {
            sh './gradlew jar'
        }
    }
}