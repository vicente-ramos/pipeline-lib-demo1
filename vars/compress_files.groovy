#!/usr/bin/env groovy

// Process any number of arguments.
def call(String... args) {
    sh label: 'Create Directory to store tar files.', returnStdout: true,
        script: """ mkdir -p "$WORKSPACE/${env.PROJECT_NAME}" """
    args.each {
        sh label: 'Coping contents to project directory.', returnStdout: true,
            script: """ cp -rv ${it} "$WORKSPACE/${env.PROJECT_NAME}/." """
    }
    sh label: 'Compressing project directory to a tar file.', returnStdout: true,
    script: """ tar -czf "${env.PROJECT_NAME}.tar.gz" "${env.PROJECT_NAME}" """
    sh label: 'Remove the Project directory..', returnStdout: true,
    script: """ rm -rf "$WORKSPACE/${env.PROJECT_NAME}" """    
}
