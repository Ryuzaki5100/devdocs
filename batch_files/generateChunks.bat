@echo off
call setEnv.bat
setlocal EnableDelayedExpansion

:: Check if required tools are available
where curl >nul 2>&1
if %ERRORLEVEL% neq 0 (
    echo Error: curl is not installed or not in PATH. Please install curl.
    exit /b 1
)

:: Check if java-files.txt exists
if not exist "java_files.txt" (
    echo Error: java-files.txt not found in the current directory.
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

:: Initialize chunks.json (start with an empty object)
echo { > chunks.json

:: Flag to determine if it's the first entry (to handle commas)
set "first_entry=1"

:: Read each line from java-files.txt
for /f "usebackq tokens=*" %%i in ("java_files.txt") do (
    set "filePath=%%i"

    echo -----------------------------------------
    echo Processing: !filePath!
    echo GH_OWNER  = %GH_OWNER%
    echo GH_REPO   = %GH_REPO%
    echo GH_BRANCH = %GH_BRANCH%

    :: Convert backslashes (\) to forward slashes (/) for API
    set "api_path=%%i"
    set "api_path=!api_path:\=/!"

    :: Escape spaces and special characters for URL
    set "escaped_path=!api_path!"
    set "escaped_path=!escaped_path!"

    echo API Path: !escaped_path!

    :: Make the API call using forward slashes
    curl -G -s "http://localhost:8080/parseJavaCodeToJSON" ^
        --data-urlencode "owner=%GH_OWNER%" ^
        --data-urlencode "repo=%GH_REPO%" ^
        --data-urlencode "branch=%GH_BRANCH%" ^
        --data-urlencode "filePath=!escaped_path!" > temp.json

    :: Print response for debugging
    echo API Response for !filePath!:
    type temp.json
    echo.

    :: Escape backslashes in the original filePath for JSON output
    set "json_key=!filePath!"
    set "json_key=!json_key:\=\\!"  :: Convert \ to \\ for JSON format

    :: Check if curl succeeded (file exists and is not empty)
    if exist "temp.json" (
        for %%F in ("temp.json") do set "size=%%~zF"
        if !size! gtr 0 (
            :: Read the JSON response
            set /p json_response=<temp.json

            :: Append to chunks.json with escaped backslashes in key
            if !first_entry! equ 1 (
                echo   "!json_key!": !json_response! >> chunks.json
                set "first_entry=0"
            ) else (
                echo   , "!json_key!": !json_response! >> chunks.json
            )
        ) else (
            echo Warning: Empty response for !filePath!
            if !first_entry! equ 1 (
                echo   "!json_key!": {"error": "Empty response from API"} >> chunks.json
                set "first_entry=0"
            ) else (
                echo   , "!json_key!": {"error": "Empty response from API"} >> chunks.json
            )
        )
    ) else (
        echo Error: Failed to retrieve response for !filePath!
        if !first_entry! equ 1 (
            echo   "!json_key!": {"error": "API call failed"} >> chunks.json
            set "first_entry=0"
        ) else (
            echo   , "!json_key!": {"error": "API call failed"} >> chunks.json
        )
    )

    :: Clean up temp file
    if exist "temp.json" del "temp.json"
)

:: Close the JSON object
echo } >> chunks.json

echo Done! Results written to chunks.json
endlocal
exit /b 0
