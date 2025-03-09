@echo off
call setEnv.bat
setlocal EnableDelayedExpansion

:: Set UTF-8 encoding
chcp 65001 >nul

:: Check if required tools are available
where curl >nul 2>&1
if %ERRORLEVEL% neq 0 (
    echo Error: curl is not installed or not in PATH. Please install curl.
    exit /b 1
)

:: Check if java-files.txt exists
if not exist "java_files.txt" (
    echo Error: java_files.txt not found in the current directory.
    exit /b 1
)

:: Check if environment variables are set
if "%GH_OWNER%"=="" (
    echo Error: Environment variable GH_OWNER is not set.
    exit /b 1
)
if "%GH_REPO%"=="" (
    echo Error: Environment variable GH_REPO is not set.
    exit /b 1
)
if "%GH_BRANCH%"=="" (
    echo Error: Environment variable GH_BRANCH is not set.
    exit /b 1
)

:: Initialize chunks.json
echo { > chunks.json

set "first_entry=1"

for /f "usebackq tokens=*" %%i in ("java_files.txt") do (
    set "filePath=%%i"
    echo -----------------------------------------
    echo Processing: !filePath!
    echo GH_OWNER  = %GH_OWNER%
    echo GH_REPO   = %GH_REPO%
    echo GH_BRANCH = %GH_BRANCH%

    set "api_path=%%i"
    set "api_path=!api_path:\=/!"
    set "escaped_path=!api_path!"

    echo API Path: !escaped_path!

    :: Fetch the response
    curl -G -s "http://localhost:8080/parseJavaCodeToJSON" ^
        --data-urlencode "owner=%GH_OWNER%" ^
        --data-urlencode "repo=%GH_REPO%" ^
        --data-urlencode "branch=%GH_BRANCH%" ^
        --data-urlencode "filePath=!escaped_path!" -o temp.json

    echo API Response for !filePath!:
    type temp.json
    echo.

    set "json_key=!filePath!"
    set "json_key=!json_key:\=\\!"

    if exist "temp.json" (
        for %%F in ("temp.json") do set "size=%%~zF"
        if !size! gtr 0 (
            if !first_entry! equ 1 (
                echo   "!json_key!": >> chunks.json
                type temp.json >> chunks.json
                set "first_entry=0"
            ) else (
                echo   , "!json_key!": >> chunks.json
                type temp.json >> chunks.json
            )
        ) else (
            if !first_entry! equ 1 (
                echo   "!json_key!": {"error": "Empty response from API"} >> chunks.json
                set "first_entry=0"
            ) else (
                echo   , "!json_key!": {"error": "Empty response from API"} >> chunks.json
            )
        )
    ) else (
        if !first_entry! equ 1 (
            echo   "!json_key!": {"error": "API call failed"} >> chunks.json
            set "first_entry=0"
        ) else (
            echo   , "!json_key!": {"error": "API call failed"} >> chunks.json
        )
    )

    if exist "temp.json" del "temp.json"
)

echo } >> chunks.json
echo Done! Results written to chunks.json
endlocal
exit /b 0