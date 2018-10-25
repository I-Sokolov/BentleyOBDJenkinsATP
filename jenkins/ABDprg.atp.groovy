@Library('atpLib') _

pipeline {
    agent any
    environment{
       ABD_PATH	       = 'C:\\Program Files\\Bentley\\AECOsim CONNECT Edition\\AECOsimBuildingDesigner\\'
       SRCTREE_NAME    = 'ABD_prg'
       BUILDSTRATEGY   = 'Building'
    }

    parameters {
        string(name: 'tagATP', defaultValue: '', description: 'Tag ATP before updating (black to get from tip)')
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
                atp 'NoCoverage', !params.fastRun, !params.fastRun, params.tagATP
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
