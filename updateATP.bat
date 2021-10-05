call %~dp0sharedshell.bat
@echo on

set GETTAG=%1
if .%1==. set GETTAG=HEAD

pushd %SrcRoot%Building\

REM ----------------------------- update with subfolders and clean -C
git pull "https://bentleycs@dev.azure.com/bentleycs/Facilities%20Engineering/_git/BuildingDev_ATPs" 
IF NOT "%ERRORLEVEL%" == "0" EXIT %ERRORLEVEL%

popd

