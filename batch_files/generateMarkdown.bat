@echo off
setlocal EnableDelayedExpansion

REM Define paths
set "BATCH_DIR=%~dp0"
set "INPUT_FILE=%BATCH_DIR%output.json"
set "OUTPUT_DIR=%BATCH_DIR%..\docs"

REM Ensure the docs directory exists
if not exist "%OUTPUT_DIR%" mkdir "%OUTPUT_DIR%"

REM Check if JSON file exists
if not exist "%INPUT_FILE%" (
    echo Error: JSON file not found at %INPUT_FILE%
    exit /b 1
)

REM Read JSON and parse key-value pairs using PowerShell
powershell -Command ^
    "$json = Get-Content -Raw '%INPUT_FILE%' | ConvertFrom-Json;" ^
    "foreach ($pair in $json.PSObject.Properties) {" ^
    "    $filePath = Split-Path -Leaf $pair.Name -Resolve;" ^
    "    $fileName = [System.IO.Path]::GetFileNameWithoutExtension($filePath) + '.md';" ^
    "    $content = $pair.Value;" ^
    "    $outputPath = Join-Path '%OUTPUT_DIR%' $fileName;" ^
    "    $content | Set-Content -Path $outputPath -Encoding UTF8;" ^
    "}"

REM Verify files were created
if %ERRORLEVEL% neq 0 (
    echo Error: Failed to parse JSON or create markdown files.
    exit /b 1
)

echo Markdown files created in %OUTPUT_DIR%
exit /b 0
