call %~dp0sharedshell.bat
@echo on

set GETTAG=%1
if .%1==. set GETTAG=HEAD

pushd %SrcRoot%Building\

REM ----------------------------- update with subfolders and clean -C
cvs -d :sspi:%CVS_USER%@atp-serv.bentley.com:/atp-root update  -A -P -d -C -r %GETTAG% atp/building/development
IF NOT "%ERRORLEVEL%" == "0" EXIT %ERRORLEVEL%

popd

