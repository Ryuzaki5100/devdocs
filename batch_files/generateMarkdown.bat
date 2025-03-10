@echo on
setlocal enabledelayedexpansion

:: Define absolute docs path
set "DOCS_DIR=%GITHUB_WORKSPACE%\docs"

:: Ensure docs directory exists
if not exist "%DOCS_DIR%" mkdir "%DOCS_DIR%"

:: Check if output.json exists
if not exist "%GITHUB_WORKSPACE%\batch_files\output.json" (
    echo Error: output.json not found!
    exit /b 1
)

:: Read output.json and generate Markdown files
for /f "tokens=1,2 delims=:" %%a in (%GITHUB_WORKSPACE%\batch_files\output.json) do (
    set "filename=%%a"
    set "content=%%b"

    :: Remove extra characters (quotes, spaces)
    set "filename=!filename:"=!"
    set "content=!content:"=!"

    :: Convert Java filename to Markdown
    set "filename=!filename:.java=.md!"

    echo !content! > "%DOCS_DIR%\!filename!"
    echo Created "%DOCS_DIR%\!filename!"
)

echo Markdown files generated successfully.
exit /b 0
