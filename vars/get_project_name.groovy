#!/usr/bin/env groovy

def call() {
    sh (label: '------Getting project name now ------',
        script: '''basename -s .git `git config --get remote.origin.url`''',
        returnStdout: true
    ).trim()
}