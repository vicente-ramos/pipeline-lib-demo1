#!/usr/bin/env groovy

def call() {
    sh(returnStdout: true, script: "git log -1 --pretty=format:%ae").trim()
}
