#!/usr/bin/env groovy

import groovy.json.JsonOutput

def call() {
    def commitMessage = sh(returnStdout: true, script: 'git log -1 HEAD --pretty=format:%s')
    def projectName = sh(returnStdout: true, script: 'basename -s .git `git config --get remote.origin.url`').trim()
    def buildNumber = env.BUILD_NUMBER
    def gitCommitHash = sh(returnStdout: true, script: 'git rev-parse HEAD').trim()
    def branchName = sh(returnStdout: true, script: 'git rev-parse --abbrev-ref HEAD').trim()
    def json_str = JsonOutput.toJson([commitMessage: "$commitMessage", gitCommitHash: "$gitCommitHash", projectName: "$projectName", buildNumber:"$buildNumber", branchName: "$branchName"])
    def json_beauty = JsonOutput.prettyPrint(json_str)
    return json_beauty
}
