@Library('atpLib') _

pipeline {
    agent any
    environment{
       ABD_PATH	       = 'C:\\Program Files\\Bentley\\AECOsim CONNECT Edition\\AECOsimBuildingDesigner\\'
       SRCTREE_NAME    = 'ABD_prg'
       BUILDSTRATEGY   = 'Building'
    }

    parameters {
        string(name: 'atpTag', defaultValue: '', description: 'ATP tag')
        string(name: 'atpBranch', defaultValue: '', description: 'ATP branch (specify both tag and branch to set tag)')
        string(name: 'atpPart', defaultValue: '', description: 'ATP part to run (blank to run all)')
        booleanParam(name: 'fastRun', defaultValue: false, description: 'Only run one ATP cicle (no update and iterations)')
    }

    stages {        
        //**************************************************************************
        //
        stage ('bootstrap'){
            steps {
                script {
                    if (!params.fastRun) {
                        //clean Hg repos (before switch to buildingToday)
                        bat 'bootstrap.bat BuildingIron'
                    }
                    else {
                        echo 'skip bootstrap'
                    }
                }
            }
        }

        //**************************************************************************
        //
        stage ('pull ATP script'){
            steps {
                script {
                    if (!params.fastRun) {
                        bat 'pullATP.bat'    
                    }
                    else {
                        echo 'skip pull scripts'
                    }
                }
                
            }
        }

        //**************************************************************************
        //
        stage ('run ATP tests') {
            steps {
                atp 'NoCoverage', 'NoReport', !params.fastRun, !params.fastRun, params.atpTag, params.atpBranch, params.atpTag
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
            }
        }
    }
}
