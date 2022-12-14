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
        booleanParam(name: 'wantRepeatATP', defaultValue: true, description: 'twice ATP run to get failure logs')
        string (name: 'atpPart', defaultValue: '', description: 'ATP part to run (blank to run all)')
        choice (name: 'coverageMode', choices: ['NoCoverage', 'AppendCoverage', 'RewriteCoverage', 'OnlyReportCoverage'], description: 'Select how to gerenate coverage data')
        choice (name: 'coverageReport', choices: ['NoReport', 'Cobertura', 'Html'], description: 'Coverage presentation data format')
        booleanParam(name: 'wantShutdown', defaultValue: false, description: 'hibernate the station when finished')
    }

    stages {        
        //**************************************************************************
        //
        stage ('bootstrap'){
            steps {
                script {
                    atp.verifyParams ('', '')
                    print params

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
        /* 
        //as single stage
        stage ('ATP'){
            steps {
                atp params.coverageMode, params.coverageReport, params.wantResetATP, params.wantUpdateATP, '', '', params.atpPart
            }
        }
        */
        //in multi stages

        stage ('prepare tests') {
            steps {                
                script { atp.prepare (params.wantResetATP, params.wantUpdateATP, '', '') }
            }
        }

        stage ('run#1') {
            steps {
                script { atp.run1 (params.coverageMode, params.coverageReport, params.atpPart) }
            }
        }

        stage ('run#2') {
            steps {
                script {
                    if (params.wantRepeatATP) {
                        atp.run2 (params.atpPart)
                    }
                }
            }
        }

        stage ('check result') {
            steps {
                script { atp.checkResult () }
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
                    bat 'shutdown /h'
                }
            }
        }
    }
    
}
