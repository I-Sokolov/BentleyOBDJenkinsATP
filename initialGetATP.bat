call %~dp0sharedshell.bat
@echo on

set GETTAG=%1
if .%1==. set GETTAG=HEAD

pushd %SrcRoot%Building\

REM ---------------------------- to create directory if missed
IF NOT EXIST %SrcRoot%Building\atp\building\development cvs -d :sspi:%CVS_USER%@atp-serv.bentley.com:/atp-root co -A -P -l -r %GETTAG% atp/building/development
IF NOT "%ERRORLEVEL%" == "0" EXIT %ERRORLEVEL%

popd

