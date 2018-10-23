call %~dp0sharedshell.bat

@echo .
@echo ************************* Clean ATP output **************
@echo on

IF EXIST %OutRoot%Winx64\LogFiles\BuildingDesignerATP rd %OutRoot%Winx64\LogFiles\BuildingDesignerATP /s /q

IF EXIST %OutRoot%Winx64\build\triforma\atp rd  %OutRoot%Winx64\build\triforma\atp /s /q

