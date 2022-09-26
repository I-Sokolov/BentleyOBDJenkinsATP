call "%~dp0sharedshell.bat"
@echo on

call %SrcRoot%Building\OpenBuildings\buildingdesigner\atp\RunTest.bat %1
IF NOT "%ERRORLEVEL%" == "0" EXIT %ERRORLEVEL%
