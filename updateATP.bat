call %~dp0sharedshell.bat
@echo on

set GETTAG=%1
if .%1==. set GETTAG=HEAD

pushd %SrcRoot%Building\

REM ----------------------------- to create directory if missed
cvs -d :sspi:%CVS_USER%@atp-serv.bentley.com:/atp-root co -A -P -l -r %GETTAG% atp/building/development
IF NOT "%ERRORLEVEL%" == "0" EXIT %ERRORLEVEL%

REM ----------------------------- update with subfolders and clean -C
cvs -d :sspi:%CVS_USER%@atp-serv.bentley.com:/atp-root update  -A -P -d -C -r %GETTAG% atp/building/development
IF NOT "%ERRORLEVEL%" == "0" EXIT %ERRORLEVEL%

popd

