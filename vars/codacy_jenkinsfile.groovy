#!/usr/bin/env groovy
@Library('jenkins-shared-lib')_
import hudson.tasks.test.AbstractTestResultAction
/*
    This jenkinsfile uses shared libraries to replace slack notify, git author, getlastcommitmessage and
    get testresults
*/


pipeline {
    agent { label 'slave-agent-1' }

    environment {
        PROJECT_NAME = get_project_name()
    }

    stages{
        stage('Build') {
            steps {
                sh label: 'Debug: Print env vars', script: '''printenv | sort'''
                sh label: 'Installing bundle dependencies.', returnStdout: true, script: '''bundle install'''
                sh label: 'Packaging bundle dependencies.', returnStdout: true, script: '''bundle package'''
            }
        }

        stage('Unit Test') {
            steps {
                sh label: 'Running tests.', script: '''bundle exec rspec'''
            }
        }
        stage('Codacy Coverage Report') {
            steps {
                codacy_coverage("project token", "language", "coverage.xml")
            }
        }

        stage('Package') {
            steps {
                create_project_tar()
            }
        }

        stage('Deploy to development environment.') {
            when { not { anyOf { branch 'feature/release*'; branch 'bugfix/release*'} }; anyOf { branch 'feature/*'; branch 'bugfix/*' } }
            steps {
                sh label: """ echo Change detected on feature or bugfix branch: -- ${env.GIT_BRANCH} --. """, returnStdout: true,
                   script: """ echo "Execute steps for ${env.GIT_BRANCH} branch here." """
                   //sh label: 'Deploying project tar file to ansible server.', script: """ scp "${env.PROJECT_NAME}.tar.gz" "servername" """
                   run_ansible_update_playbook()
            }
        }
        stage('Deploy to integration & feature testing environments.') {
            when { branch 'develop'}
            steps {
                sh label: """ echo Change detected on feature or bugfix branch: -- ${env.GIT_BRANCH} --. """, returnStdout: true,
                   script: """ echo "Execute steps for ${env.GIT_BRANCH} branch here." """
                   //sh label: 'Deploying project tar file to ansible server.', script: """ scp "${env.PROJECT_NAME}.tar.gz" "servername" """
                   run_ansible_update_playbook()
            }
        }
        stage('Deploy to staging prep environment.') {
            when { branch 'master'}
            steps{
                sh label: """ echo Change detected on -- master -- branch. """, returnStdout: true,
                   script: """ echo "Execute steps for ${env.GIT_BRANCH} branch here." """
                   //sh label: 'Deploying project tar file to ansible server.', script: """ scp "${env.PROJECT_NAME}.tar.gz" "servername" """
                   run_ansible_update_playbook()
            }
        }
        stage('Deploy to release environment') {
            when { branch 'release/*'}
            steps{
                sh label: """ echo Change detected on */release/* branch. """, returnStdout: true,
                   script: """ echo "Execute steps for ${env.GIT_BRANCH} branch here." """
                   //sh label: 'Deploying project tar file to ansible server.', script: """ scp "${env.PROJECT_NAME}.tar.gz" "servername" """
                   run_ansible_update_playbook()
            }
        }
        stage('Deploy to release feature/bugfix environment.') {
            // Feature or bugfix branch created from a release branch must have "release" prefixed to the branch name
            when { anyOf { branch 'feature/release*'; branch 'bugfix/release*' } }
            steps{
                sh label: """ echo Change detected on a release feature or bugfix branch. """, returnStdout: true,
                   script: """ echo "Execute steps for ${env.GIT_BRANCH} branch here." """
                 //sh label: 'Deploying project tar file to ansible server.', script: """ scp "${env.PROJECT_NAME}.tar.gz" "servername" """
                 run_ansible_update_playbook()
            }
        }
    }

    post {
        always {
            junit 'pipeline/artifacts/test_results.xml'
            step([$class: 'CoberturaPublisher',
                          autoUpdateHealth: false,
                          autoUpdateStability: false,
                          coberturaReportFile: 'pipeline/artifacts/coverage/coverage.xml',
                          failUnhealthy: true,
                          failUnstable: true,
                          maxNumberOfBuilds: 0,
                          onlyStable: false,
                          sourceEncoding: 'ASCII',
                          zoomCoverageChart: false])
             slacknotification(currentBuild.currentResult,'workspacename','slackuser','#channel','secret_text_id_of_jenkins_creds_containing_token',getTestResultsForSlack())


        }
    }
}

/*
    Executes the ansible playbook to update the service on the target deployment server.
*/
def run_ansible_update_playbook() {
    def ansible_update_command = ""
    def playbook_call = 'ansible-playbook -i /opt/slamr/slamr-devops-automation/inventory/hosts /opt/slamr/slamr-devops-automation/update_build_example_ruby.yml --limit '
    //Determine the hosts to run the playbook on based on the branch.
    if ((env.BRANCH_NAME.startsWith('feature/') || env.BRANCH_NAME.startsWith('bugfix/')) && !(env.BRANCH_NAME.startsWith('feature/release') || env.BRANCH_NAME.startsWith('bugfix/release'))){
        ansible_update_command = "${playbook_call}" + "'servername'"
    } else if (env.BRANCH_NAME.startsWith('develop')){
        ansible_update_command = "${playbook_call}" + "'servername','servername'"
    } else if (env.BRANCH_NAME.startsWith('master')){
       //TBD
    } else if (env.BRANCH_NAME.startsWith('release')){
       //TBD
    } else if (env.BRANCH_NAME.startsWith('feature/release') || env.BRANCH_NAME.startsWith('bugfix/release')){
       //TBD
    }
    echo "ANSIBLE UPDATE COMMAND: ${ansible_update_command}"
    sshPublisher(publishers:[sshPublisherDesc(
                 configName: 'ccansible',
                 transfers:[sshTransfer
                 (execCommand: ansible_update_command,
                 execTimeout: 120000 )], verbose: true)])
}