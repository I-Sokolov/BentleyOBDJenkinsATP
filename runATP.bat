call %~dp0sharedshell.bat
@echo on

REM ******** save first log files and clean to next run
%SrcRoot%Building\BuildingDesigner\ATP\RunTest.bat %1
IF NOT "%ERRORLEVEL%" == "0" EXIT %ERRORLEVEL%
