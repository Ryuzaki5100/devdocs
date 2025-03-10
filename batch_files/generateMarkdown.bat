@echo off
setlocal enabledelayedexpansion

:: Define paths
set "INPUT_FILE=batch_files\output.json"
set "OUTPUT_DIR=docs"

:: Ensure the output directory exists
if not exist "%OUTPUT_DIR%" mkdir "%OUTPUT_DIR%"

:: Read JSON file and parse key-value pairs
for /f "usebackq delims=" %%A in (%INPUT_FILE%) do (
    set "line=%%A"

    :: Extract key (file path) and value (markdown content)
    for /f "tokens=1,2 delims=:" %%B in ("!line!") do (
        set "file_path=%%B"
        set "md_content=%%C"

        :: Remove quotes and trim spaces
        set "file_path=!file_path:~1,-1!"
        set "md_content=!md_content:~2,-2!"

        :: Extract file name
        for %%F in (!file_path!) do set "file_name=%%~nxF"

        :: Write content to markdown file
        echo !md_content! > "%OUTPUT_DIR%\!file_name!.md"
    )
)

echo Markdown files created in %OUTPUT_DIR%
endlocal