#!/usr/bin/env groovy

def call(String language, String token, String file) {
    def command = "set +x\ncurl https://coverage.codacy.com/get.sh | bash -s report -t ${token}  "
    command = command + "-l ${language} -r ${file}\nset -x" 
    
    sh(returnStdout: false, script: command)
}


