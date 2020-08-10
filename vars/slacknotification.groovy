#!/usr/bin/env groovy

def call(String buildResult,String teamDomain,String username,String channel,String tokenCredentialId, String testResults) {
    def jenkinsIcon = 'https://wiki.jenkins.io/download/attachments/2916393/logo.png'
    def committerUserID = slackUserIdFromEmail("${getAuthorEmailid()}")
    def message = "Job: ${env.JOB_NAME}\n" +
                  "Git Commit Author: <@$committerUserID> ${getGitAuthor()}\nLast Git Commit Message: ${getLastCommitMessage()}\n" +
                  "\nBuild Number: ${env.BUILD_NUMBER}\nBuild Status: ${buildResult}." +
                  "\nTest Results: ${testResults} (<${env.BUILD_URL}|Open>)"
    def color = '#000000'
    if ( buildResult == "SUCCESS" ) {
        color = 'good'
    }else if( buildResult == "FAILURE" ) {
        color = 'danger'
    }else if( buildResult == "UNSTABLE" ) {
        color = "warning"
    }else {
        color = 'danger'
    }
    slackSend color: color, botUser: true, notifyCommitters: true, teamDomain: teamDomain, username: username,
              channel: "$channel", tokenCredentialId: tokenCredentialId, message: message
}
