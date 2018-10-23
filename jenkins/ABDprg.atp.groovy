@Library('atpLib') _

pipeline {
    agent any
    environment{
       ABD_PATH	       = 'C:\\Program Files\\Bentley\\AECOsim CONNECT Edition\\AECOsimBuildingDesigner\\'
       SRCTREE_NAME    = 'ABD_prg'
       BUILDSTRATEGY   = 'Building'
    }

    stages {        
        //**************************************************************************
        //
        stage ('bootstrap'){
            steps{
                bat 'bootstrap.bat BuildingIron'
            }
        }

        //**************************************************************************
        //
        stage ('pull ATP script'){
            steps{
                bat 'pullATP.bat'
            }
        }

        //**************************************************************************
        //
        stage ('run ATP tests'){
            steps{
                atp false
            }
        }
    }
   
    post {
        always {
            mail  to: "${env.DEFAULT_RECIPIENTS}",
                  subject: "${currentBuild.fullDisplayName} finished ${currentBuild.result} at ${env.BUILD_URL}",
                  body: "Please see report ${env.BUILD_URL}"                           
        }
    }
    
}
