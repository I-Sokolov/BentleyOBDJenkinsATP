
//def call (script, String name = 'human')
def call (boolean watchCoverage, boolean resetATP, boolean updateATP, String tagATP)
 {
    script {
        if (!env.SRCTREE_NAME) {
            echo "Set SRCTREE_NAME environment variable"            
            currentBuild.result = 'UNSTABLE'
            return
            }

        //---------------------------------------------------------------------------
        //        
        if (resetATP) {
            echo '**************************** ATP reset ******************'
            bat 'resetATP.bat'
        }

        if (updateATP) {
            echo '**************************** ATP update, tag: ' + tagATP
            if (tagATP != '') {
                bat 'tagATP ' + tagATP
            }
            bat 'updateATP.bat ' + tagATP
        }

        //-----------------------------------------------------------------------------
        //
        echo '**************************** ATP run #1 *****************************'
        
        if (watchCoverage) {
            echo 'Init coverage data'
            env.ATP_OPENCPPCOVERAGE = env.WORKSPACE+'\\ATP_coverage\\'
            bat "IF EXIST ${env.ATP_OPENCPPCOVERAGE} rd  ${env.ATP_OPENCPPCOVERAGE} /s /q"
            bat "mkdir ${env.ATP_OPENCPPCOVERAGE}" 
            }

        //
        bat 'runATP.bat' 
        
        //
        if (watchCoverage) {
            bat "ATPhelper.bat OpenCppCoverageMerge ${env.ATP_OPENCPPCOVERAGE}OpenCPPCoverage.xml"
            step $class: 'CoberturaPublisher', coberturaReportFile: 'ATP_coverage\\OpenCPPCoverage.xml'             
        }

        env.ATP_OPENCPPCOVERAGE = ''

        //-----------------------------------------------------------------------------
        //
        
        if (resetATP) {
            echo '**************************** ATP run #2 *****************************'
            bat 'runATP.bat'
            echo '**************************** ATP run #3 *****************************'
            bat 'runATP.bat'
        }
                
        //-----------------------------------------------------------------------------
        //
        echo '**************************** ATP check errors **********************'
        def status = bat script: 'ATPhelper.bat GetAtpLogs ATP_ErrorLogs', returnStatus: true       
        if (status == 0) {
            echo "ATP found no errors"
        }
        else if (status == 1) {
            currentBuild.result = 'UNSTABLE'
            archiveArtifacts 'ATP_ErrorLogs/*'
        }
        else {
            echo "Checking for errors fails with status = " + status
            throw new hudson.AbortException("Checking for errors fails with status = " + status)
        }

    }
}
