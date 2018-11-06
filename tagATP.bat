call %~dp0sharedshell.bat
@echo on

pushd %SrcRoot%Building\atp\building\development\

cvs rtag -F -R %1 %2 %3 %4 %5 atp/building/development
IF NOT "%ERRORLEVEL%" == "0" EXIT %ERRORLEVEL%

REM stick ABD 10.3 to date
REM cvs rtag -F -R -D"06 Mar 2018" %1 atp/building/development

REM stick ABD 10.4 to date
REM cvs rtag -F -R -D"19 Sept 2018" %1 atp/building/development

popd

