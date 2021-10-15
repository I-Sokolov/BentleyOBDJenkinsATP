call %~dp0sharedshell.bat
@echo on

set GETTAG=%1
if .%1==. set GETTAG=HEAD

pushd %SrcRoot%Building\

REM ----------------------------- update with subfolders and clean -C
git -C atp/building/development pull https://bentleycs@dev.azure.com/bentleycs/Facilities%%20Engineering/_git/BuildingDev_ATPs 
git -C atp/building/development/tfrfa pull https://bentleycs@dev.azure.com/bentleycs/Facilities%%20Engineering/_git/BuildingRFA_ATPs
IF NOT "%ERRORLEVEL%" == "0" EXIT %ERRORLEVEL%

popd

