call "%~dp0sharedshell.bat"

@echo .
@echo ************************* Clean ATP output **************
@echo on

IF EXIST %OutRoot%Winx64 rd %OutRoot%Winx64 /s /q

