@Library('atpLib') _

pipeline {
    agent any
    environment {
       SRCTREE_NAME    = 'BuildingIron'
       BUILDSTRATEGY   = 'Building'
       WipPartEnable_ABD_DATASET_US = '1'	   
       DONT_BUILD_DOC	     = '1'
       SuppressCodeAnalysis  = '1'
    }

    parameters {
        booleanParam(name: 'wantBootstrap', defaultValue: false, description: 'Run bootstrap stange (initial build)')
        booleanParam(name: 'wantPull', defaultValue: false, description: 'Update ABD sources')
        booleanParam(name: 'wantBuild', defaultValue: false, description: 'Rebuild ABD')
        booleanParam(name: 'wantResetATP', defaultValue: false, description: 'clean ATP results and run full cycle')
        booleanParam(name: 'wantUpdateATP', defaultValue: false, description: 'update ATP database')
        choice (name: 'coverage', choices: ['NoCoverage', 'AppendCoverage', 'RewriteCoverage'], description: 'Select coverage mode')
        booleanParam(name: 'wantShutdown', defaultValue: false, description: 'hibernate the station when finished')
    }

    stages {        
        //**************************************************************************
        //
        stage ('bootstrap'){
            steps {
                script {
                    if (params.wantBootstrap) {
                        bat 'bootstrap.bat'
                    }
                    else {
                        echo 'Skip bootstrap stage'
                    }
                }
            }
        }
        
        //**************************************************************************
        //
        stage ('pull') {
            steps {
                script {
                    if (params.wantPull) {
                        def consoleOut = bat script:'pull.bat', returnStdout : true
                        echo consoleOut
                        if (consoleOut.contains("Pull Succeeded")){
                            echo "Pull Succeeded"
                            return
                            }
                        currentBuild.result = 'FAILURE'
                        throw new hudson.AbortException("Pull Failed")
                    }
                    else {
                        echo 'Skip bootstrap stage'                    
                    }
                }
            }
        }

        //**************************************************************************
        //
        stage ('build') {
            steps {
                script {
                    if (params.wantBuild) {
                        bat 'tmrbuild.bat'
                    }
                    else {
                        echo 'Skip build stage'
                    }
                }
            }
        }        

        //**************************************************************************
        //
        stage ('ATP'){
            steps {
                atp params.coverage, params.wantResetATP, params.wantUpdateATP, ''
            }
        }
    }

   
    post {
        always {
            script {
                try {
                    mail  to: "${env.DEFAULT_RECIPIENTS}",
                          subject: "${currentBuild.fullDisplayName} finished ${currentBuild.result} at ${env.BUILD_URL}",
                          body: "Please see report ${env.BUILD_URL}"                           
                }
                catch (exc) {
                    echo 'Failed to send email: ' + exc
                }

                if (params.wantShutdown) {
                    but 'shutdown /h'
                }
            }
        }
    }
    
}
