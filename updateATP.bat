call %~dp0sharedshell.bat
@echo on

set GETTAG=%1
if .%1==. set GETTAG=HEAD

pushd %SrcRoot%Building\

REM ----------------------------- update with subfolders and clean

pushd atp/building/development
dir .

git reset --hard
IF NOT "%ERRORLEVEL%" == "0" EXIT %ERRORLEVEL%

git pull https://bentleycs@dev.azure.com/bentleycs/Facilities%%20Engineering/_git/BuildingDev_ATPs 
IF NOT "%ERRORLEVEL%" == "0" EXIT %ERRORLEVEL%

popd

pushd atp/building/development/tfrfa
dir .

git reset --hard
IF NOT "%ERRORLEVEL%" == "0" EXIT %ERRORLEVEL%

git pull https://bentleycs@dev.azure.com/bentleycs/Facilities%%20Engineering/_git/BuildingRFA_ATPs
IF NOT "%ERRORLEVEL%" == "0" EXIT %ERRORLEVEL%

popd

popd

