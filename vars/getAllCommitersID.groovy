#!/usr/bin/env groovy


def call() {
    def all_commiters = sh(returnStdout: true, script: 'git shortlog -sne --all').trim()
    def commiters_list = []
    for(String value: all_commiters) {
    	commiter_data = value.split('<')
    	commiter_email = commiter_data[1].replace(">", "")
    	commiters_list.add(slackUserIdFromEmail(commiter_email))
    }
    return commiters_list
}
