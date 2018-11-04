/*--------------------------------------------------------------------------------------+
|
|     $Source: jenkins/params.groovy $
|
|  $Copyright: (c) 2018 Bentley Systems, Incorporated. All rights reserved. $
|
+--------------------------------------------------------------------------------------*/

@Library('atpLib') _

pipeline {
    agent any
    environment{
       ABD_PATH	       = 'C:\\Program Files\\Bentley\\AECOsim CONNECT Edition\\AECOsimBuildingDesigner\\'
       SRCTREE_NAME    = 'ABD_prg'
       BUILDSTRATEGY   = 'Building'
    }

    parameters {
        string(name: 'atpTag', defaultValue: "${atp.getParam('atpTag', 'test', 'defaultTag')}", description: 'ATP tag')
        string(name: 'atpBranch', defaultValue: '', description: 'ATP branch (specify both tag and branch to set tag)')
        string(name: 'atpPart', defaultValue: '', description: 'ATP part to run (blank to run all)')
        booleanParam(name: 'fastRun', defaultValue: "${atp.getParam('fastRun', 'test', false)}", description: 'Only run one ATP cicle (no update and iterations)')
        booleanParam(name: 'shutDown', defaultValue: true, description: 'Hybernate as finished')
    }

    stages {        
        //**************************************************************************
        //
        stage ('show'){
            steps {
                script {
                    atp.saveParam ('atpTag', 'test', params.atpTag)
                    atp.saveParam ('fastRun', 'test', params.fastRun)

                    print params
                }
            }
        }
    }

}
