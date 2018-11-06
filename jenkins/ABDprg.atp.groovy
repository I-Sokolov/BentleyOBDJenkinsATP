/*--------------------------------------------------------------------------------------+
|
|     $Source: jenkins/ABDprg.atp.groovy $
|
|  $Copyright: (c) 2018 Bentley Systems, Incorporated. All rights reserved. $
|
+--------------------------------------------------------------------------------------*/

@Library('atpLib') _

pipeline {
    agent any
    environment{
       ABD_PATH	       = 'C:\\Program Files\\Bentley\\AECOsim CONNECT Edition\\AECOsimBuildingDesigner\\'
       PRG             = '1'
       SRCTREE_NAME    = 'ABD_prg'
       BUILDSTRATEGY   = 'Building'
    }

    parameters {
        string(name: 'atpTag',    defaultValue: "${atp.getParam('atpTag',    'ADBprg.atp', '')}", description: 'ATP tag')
        string(name: 'atpBranch', defaultValue: "${atp.getParam('atpBranch', 'ADBprg.atp', '')}", description: 'ATP branch (specify both tag and branch to set tag)')
        string(name: 'atpPart', defaultValue: '', description: 'ATP part to run (blank to run all)')
        booleanParam(name: 'fastRun', defaultValue: false, description: 'Only run one ATP cicle (no update and iterations)')
        //booleanParam(name: 'shutDown', defaultValue: true, description: 'Hybernate as finished')
    }

    stages {        
        //**************************************************************************
        //
        stage ('bootstrap'){
            steps {
                script {
                    atp.verifyParams (params.atpTag, params.atpBranch)

                    atp.saveParam('atpTag',    'ADBprg.atp', params.atpTag)
                    atp.saveParam('atpBranch', 'ADBprg.atp', params.atpBranch)

                    echo 'PARAMETERS:'
                    print params

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
        stage ('pull script'){
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
        /* 
        //as single stage
        stage ('run ATP tests') {
            steps {
                atp 'NoCoverage', 'NoReport', !params.fastRun, !params.fastRun, params.atpTag, params.atpBranch, params.atpPart
            }
        }
        */
        //in multi stages

        stage ('prepare tests') {
            steps {                
                script { atp.prepare (!params.fastRun, !params.fastRun, params.atpTag, params.atpBranch) }
            }
        }

        stage ('run#1') {
            steps {
                script { atp.run1 ('NoCoverage', 'NoReport', params.atpPart) }
            }
        }

        stage ('run#2') {
            steps {
                script {
                    if (!params.fastRun) {
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
                /*
                if (params.shutDown) {
                    bat 'shutdown /h'
                }*/
                
            }
        }
    }
}
