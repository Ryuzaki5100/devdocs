@echo off
setlocal EnableDelayedExpansion

REM Define paths
set "BATCH_DIR=%~dp0"
set "INPUT_FILE=%BATCH_DIR%output.json"
set "OUTPUT_DIR=%BATCH_DIR%..\docs"
set "IMAGE_DIR=%OUTPUT_DIR%\images"

REM Ensure the docs and image directories exist
if not exist "%OUTPUT_DIR%" mkdir "%OUTPUT_DIR%"
if not exist "%IMAGE_DIR%" mkdir "%IMAGE_DIR%"

REM Check if JSON file exists
if not exist "%INPUT_FILE%" (
    echo Error: JSON file not found at %INPUT_FILE%
    exit /b 1
)

REM Read JSON and parse key-value pairs using PowerShell
powershell -NoProfile -ExecutionPolicy Bypass -Command "& {
    $json = Get-Content -Raw '%INPUT_FILE%' | ConvertFrom-Json;

    foreach ($pair in $json.PSObject.Properties) {
        $filePath = $pair.Name;
        $fileName = [System.IO.Path]::GetFileNameWithoutExtension($filePath) + '.md';
        $outputPath = Join-Path '%OUTPUT_DIR%' $fileName;
        $content = $pair.Value;

        # Extract base64 images and replace them with file paths
        $imageMatches = [regex]::Matches($content, '!\\[.*?\\]\\(data:image/png;base64,([^)]*)\\)');
        $index = 1;

        foreach ($match in $imageMatches) {
            $base64String = $match.Groups[1].Value;
            $imageFileName = [System.IO.Path]::GetFileNameWithoutExtension($fileName) + '_img' + $index + '.png';
            $imagePath = Join-Path '%IMAGE_DIR%' $imageFileName;
            [System.IO.File]::WriteAllBytes($imagePath, [Convert]::FromBase64String($base64String));

            $content = $content -replace [regex]::Escape($match.Value), '![' + $imageFileName + '](images/' + $imageFileName + ')';
            $index++;
        }

        # Write markdown file
        $content | Set-Content -Path $outputPath -Encoding UTF8;
    }
}"

REM Check for errors
if %ERRORLEVEL% neq 0 (
    echo Error: Failed to parse JSON or create markdown files.
    exit /b 1
)

echo Markdown files created successfully in %OUTPUT_DIR%
echo Extracted images saved in %IMAGE_DIR%
exit /b 0
