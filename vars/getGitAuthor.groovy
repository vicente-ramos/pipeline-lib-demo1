#!/usr/bin/env groovy

def call() {
    def commit = sh(returnStdout: true, script: 'git rev-parse HEAD')
    sh(returnStdout: true, script: "git --no-pager show -s --format='%an' ${commit}").trim()
}