/*--------------------------------------------------------------------------------------+
|
|     $Source: vars/atp.groovy $
|
|  $Copyright: (c) 2018 Bentley Systems, Incorporated. All rights reserved. $
|
+--------------------------------------------------------------------------------------*/
import java.io.File

//--------------------------------------------------------------------------------------
//                            Igor.Sokolov                                     2018/11
//--------------------------------------------------------------------------------------

def prepare (boolean resetATP, boolean updateATP, String atpTag, String atpBranch)
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
            if (atpTag != '' && atpBranch != '') {
                echo '**************************** ATP set tag *********'
                bat 'atpTag ' + atrBranch + ' ' + atpTag
            }
            echo '**************************** ATP update ********'
            if (atpTag != '') {
                bat 'updateATP.bat ' + atpTag
            }
            else {
                bat 'updateATP.bat ' + atpBranch            
            }
        }
    }
}

//--------------------------------------------------------------------------------------
//                            Igor.Sokolov                                     2018/11
//--------------------------------------------------------------------------------------
def run1 (String coverageMode, String coverageReportType, String atpPart)
{
    script {
        echo '**************************** ATP run #1 *****************************'
        
        if (coverageMode != 'NoCoverage') {
            env.ATP_OPENCPPCOVERAGE = env.WORKSPACE+'\\ATP_coverage\\'
            }
        if (coverageMode == 'RewriteCoverage') {
            echo 'Init coverage data'
            bat "IF EXIST ${env.ATP_OPENCPPCOVERAGE} rd  ${env.ATP_OPENCPPCOVERAGE} /s /q"
            bat "mkdir ${env.ATP_OPENCPPCOVERAGE}" 
            }

        if (coverageMode != 'OnlyReportCoverage' || resetATP) {
            bat 'runATP.bat ' + atpPart 
        }

        //
        if (coverageReportType == 'Cobertura') {
            bat "ATPhelper.bat OpenCppCoverageMerge cobertura ${env.ATP_OPENCPPCOVERAGE}OpenCPPCoverage.xml"
            step $class: 'CoberturaPublisher', coberturaReportFile: 'ATP_coverage\\OpenCPPCoverage.xml'             
        }
        else if (coverageReportType == 'Html') {
            bat "ATPhelper.bat OpenCppCoverageMerge html ${env.ATP_OPENCPPCOVERAGE}OpenCPPCoverage.htm"
            archiveArtifacts 'ATP_coverage\\OpenCPPCoverage.htm.zip'
        }

        env.ATP_OPENCPPCOVERAGE = ''
    }
}

//--------------------------------------------------------------------------------------
//                            Igor.Sokolov                                     2018/11
//--------------------------------------------------------------------------------------
def run2 (String atpPart)
{
    script {
            echo '**************************** ATP run #2 *****************************'
            bat 'runATP.bat ' + atpPart
            echo '**************************** ATP run #3 *****************************'
            bat 'runATP.bat ' + atpPart
    }
}

//--------------------------------------------------------------------------------------
//                            Igor.Sokolov                                     2018/11
//--------------------------------------------------------------------------------------
def checkResult ()
{
    script {
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

//--------------------------------------------------------------------------------------
//                            Igor.Sokolov                                     2018/11
//--------------------------------------------------------------------------------------
def call (String coverageMode, String coverageReportType, boolean resetATP, boolean updateATP, String atpTag, String atpBranch, String atpPart)
 {
    script {
        prepare (resetATP, updateATP, atpTag, atpBranch);
        
        run1 (coverageMode, coverageReportType, atpPart);

        if (resetATP) {        
            run2 (atpPart);
            }

        checkResult ();                
    }
}

//--------------------------------------------------------------------------------------
//                            Igor.Sokolov                                     2018/11
//--------------------------------------------------------------------------------------
def getSaveParamFile (String name, String job)
{
script {
    try {
        //http://www.tutorialspoint.com/groovy/groovy_file_io.htm
        
        String currentDir = new File(".").getAbsolutePath()
        String paramDir = currentDir + "\\~Jenkins.Saved.Params"

        def dir = new File(paramDir)
        dir.mkdir()

        String paramFile = paramDir + "\\" + job + "." + name + ".txt"
        return paramFile    
    }
    catch (ex) {
        echo "================= Failure in getSaveParamFile ======== "
        print ex
        return null
    }
}
}

//--------------------------------------------------------------------------------------
//                            Igor.Sokolov                                     2018/11
//--------------------------------------------------------------------------------------

def saveParam (String name, String jobName, value)
{
    script {
        String paramFile  = getSaveParamFile (name, jobName)

        try {
            File file = new File(paramFile)

            if (file.exists()) {
                file.delete()
            }

            file << value             

            echo "Save parameter " + name + "=" + value
        }
        catch (ex) {
            echo '********************* Faile to save param ' + name + ": "  
            print ex
        }
    }
}
//--------------------------------------------------------------------------------------
//                            Igor.Sokolov                                     2018/11
//--------------------------------------------------------------------------------------

def getParam (String name, String jobName, val)
{
    script {
        //http://www.tutorialspoint.com/groovy/groovy_file_io.htm
        
        String paramFile  = getSaveParamFile (name, jobName)
        
        try {
            File file = new File(paramFile) 
            val = file.text 
            echo "Read saved parameter " + name + "=" + val
        }
        catch (ex) {
        }

        saveParam (name, jobName, val)

        return val
    }    
}
