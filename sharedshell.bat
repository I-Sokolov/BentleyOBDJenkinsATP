SET BsiSrc=%SOURCECODE_HOME%%SRCTREE_NAME%\src\
SET BsiOut=%SOURCECODE_HOME%%SRCTREE_NAME%\out\
set NDEBUG=1
CALL %BSISRC%bsicommon\shell\SharedShellEnv.bat

:: QA18-BBSERVER doesn't have the VS2017 which is the default toolset currently.
:: Forcing the toolset to use VS2015 (which IS installed), and all that is dummy 
:: anyway as PRG atp server does NOT build anything.
SET BUILD_USING_VS2015=1
