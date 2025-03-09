@echo off
setlocal enabledelayedexpansion

REM Define the output file
set output_file=java_files.txt

REM Clear the output file if it already exists
type nul > %output_file%

REM Get the project root (assuming batch file is in a subfolder of project root)
for %%I in ("%~dp0..") do set "project_root=%%~fI"
echo Project root set to: %project_root%

REM Verify the project root exists
if not exist "%project_root%" (
    echo ERROR: Project root directory does not exist: %project_root%
    pause
    exit /b 1
)

REM Find all .java files and store their paths relative to project root
for /r "%project_root%" %%f in (*.java) do (
    set "full_path=%%f"
    set "relative_path=!full_path:%project_root%\=!"

    REM Convert backslashes to forward slashes
    set "relative_path=!relative_path:\=/!"

    echo(!relative_path! >> %output_file%
)

REM Check if anything was written
if %errorlevel% neq 0 (
    echo ERROR: No .java files found or issue with writing to %output_file%
) else (
    echo Paths of all .java files have been stored in %output_file%
)

endlocal