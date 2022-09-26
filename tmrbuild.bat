call "%~dp0sharedshell.bat"
@echo on
bb %JENKINS_BB_BUILD_PARAMS% build --tmrbuild --noprompt
