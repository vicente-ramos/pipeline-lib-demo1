#!/usr/bin/env groovy

def call() {
    sh(returnStdout: true, script: 'git rev-parse HEAD').trim()
}