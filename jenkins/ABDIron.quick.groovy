@Library('atpLib') _

pipeline {
    agent any
    environment{
       SRCTREE_NAME    = 'BuildingIron'
       BUILDSTRATEGY   = 'Building'
       WipPartEnable_ABD_DATASET_US = '1'	   
       DONT_BUILD_DOC	     = '1'
       SuppressCodeAnalysis  = '1'
    }

    stages {        
        //**************************************************************************
        //
        stage ('bootstrap'){
            steps{
                bat 'bootstrap.bat'
            }
        }

        //**************************************************************************
        //
        stage ('pull'){
            steps {
                script{
                    def consoleOut = bat script:'pull.bat', returnStdout : true
                    echo consoleOut
                    if (consoleOut.contains("Pull Succeeded")){
                        echo "Pull Succeeded"
                        return
                        }
                    currentBuild.result = 'FAILURE'
                    throw new hudson.AbortException("Pull Failed")
                }
            }
        }

        //**************************************************************************
        //
        stage ('build') {
            steps {
                bat 'tmrbuild.bat'
            }
        }        

        //**************************************************************************
        //
        stage ('ATP'){
            steps{
                atp true
            }
        }
    }

   
    post {
        always {
            mail  to: "${env.DEFAULT_RECIPIENTS}",
                  subject: "${currentBuild.fullDisplayName} finished ${currentBuild.result} at ${env.BUILD_URL}",
                  body: "Please see report ${env.BUILD_URL}"                           
            //bat 'shutdown /h'
        }
    }
    
}
