call %~dp0sharedshell.bat

echo ************************ Setting CVS ATP tag %1 ***********************

pushd %SrcRoot%Building\atp\building\development\

cvs rtag -F -R %1 atp/building/development

REM stick ABD 10.3 to date
REM cvs rtag -F -R -D"06 Mar 2018" %1 atp/building/development

REM stick ABD 10.4 to date
REM cvs rtag -F -R -D"19 Sept 2018" %1 atp/building/development

popd

