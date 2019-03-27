pipeline {
    options {
        disableConcurrentBuilds()
        buildDiscarder(logRotator(daysToKeepStr: '5', artifactDaysToKeepStr: '5'))
    }

    stages {
        stage ("Synchronize") {
            git url: 'https://github.com/AnthonyPoncet/piboard.git'
            sh 'git checkout add_tests'
        }
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