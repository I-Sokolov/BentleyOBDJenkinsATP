call %~dp0sharedshell.bat
@echo on

set GETTAG=%1
if .%1==. set GETTAG=HEAD

pushd %SrcRoot%Building\

REM ---------------------------- to create directory if missed
IF NOT EXIST %SrcRoot%Building\atp\building\development git clone https://bentleycs@dev.azure.com/bentleycs/Facilities%%20Engineering/_git/BuildingDev_ATPs atp/building/development
IF NOT EXIST %SrcRoot%Building\atp\building\development\tfrfa git clone https://bentleycs@dev.azure.com/bentleycs/Facilities%%20Engineering/_git/BuildingRFA_ATPs atp/building/development/tfrfa
IF NOT "%ERRORLEVEL%" == "0" EXIT %ERRORLEVEL%

popd

