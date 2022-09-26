call "%~dp0sharedshell.bat"
@echo on

pushd %SrcRoot%Building\atp\building\development\

cvs rtag -F -R %1 %2 %3 %4 %5 %6 %7 %8 %9 atp/building/development
IF NOT "%ERRORLEVEL%" == "0" EXIT %ERRORLEVEL%

popd

