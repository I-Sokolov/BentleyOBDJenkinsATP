call "%~dp0sharedshell.bat"
@echo on

set GETTAG=%1
if .%1==. set GETTAG=HEAD

pushd %SrcRoot%
cd Building\

REM ---------------------------- to create directory if missed
IF NOT EXIST %SrcRoot%Building\atp\building\development git clone https://bentleycs@dev.azure.com/bentleycs/Facilities%%20Engineering/_git/BuildingDev_ATPs atp/building/development
IF NOT "%ERRORLEVEL%" == "0" EXIT %ERRORLEVEL%

REM init sparse-checkout for local testing
REM IF EXIST %SrcRoot%Building\atp\building\development goto BuildingDev_ATPs_init_done
REM git clone --no-checkout https://bentleycs@dev.azure.com/bentleycs/Facilities%%20Engineering/_git/BuildingDev_ATPs atp/building/development
REM pushd atp\building\development
REM git sparse-checkout init
REM git sparse-checkout set -cone
REM git sparse-checkout set ProStructures/dv
REM git checkout
REM popd
REM :BuildingDev_ATPs_init_done

IF NOT EXIST %SrcRoot%Building\atp\building\development\tfrfa git clone https://bentleycs@dev.azure.com/bentleycs/Facilities%%20Engineering/_git/BuildingRFA_ATPs atp/building/development/tfrfa
IF NOT "%ERRORLEVEL%" == "0" EXIT %ERRORLEVEL%

popd

