call "%~dp0sharedshell.bat"
@echo on
REM ----------------------------- pull OpenBuildings

pushd %SrcRoot%

REM ------ some parts are required by bb
call bb -r bsicommon -f SharedTools -p NuGet.CommandLine pull
IF NOT "%ERRORLEVEL%" == "0" EXIT %ERRORLEVEL%

IF NOT EXIST Building mkdir Building
cd Building

IF EXIST OpenBuildings goto UpdateOpenBuildings

REM -----------------
REM Init OpenBuildings
git clone --no-checkout https://dev.azure.com/bentleycs/Facilities%%20Engineering/_git/OpenBuildings
IF NOT "%ERRORLEVEL%" == "0" EXIT %ERRORLEVEL%

cd OpenBuildings
git sparse-checkout init
git sparse-checkout set -cone
git sparse-checkout set build
IF NOT "%ERRORLEVEL%" == "0" EXIT %ERRORLEVEL%

cd ..

REM -----------------
:UpdateOpenBuildings
cd OpenBuildings
git reset --hard
IF NOT "%ERRORLEVEL%" == "0" EXIT %ERRORLEVEL%

git checkout igor.sokolov/PBI974286.ATP
IF NOT "%ERRORLEVEL%" == "0" EXIT %ERRORLEVEL%

git sparse-checkout set build buildingdesigner/atp buildingdesigner/privmki Triforma/privmki
IF NOT "%ERRORLEVEL%" == "0" EXIT %ERRORLEVEL%

git pull
IF NOT "%ERRORLEVEL%" == "0" EXIT %ERRORLEVEL%

popd

