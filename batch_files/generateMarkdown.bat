@echo off
setlocal enabledelayedexpansion

:: Ensure the docs directory exists
if not exist docs mkdir docs

:: Read the JSON file line by line
for /f "delims=" %%i in (batch_files\output.json) do (
    set "line=%%i"

    :: Extract key (filename) and value (documentation content)
    for /f "tokens=1,2 delims=:" %%a in ("!line!") do (
        set "filename=%%a"
        set "content=%%b"

        :: Remove quotes and unwanted characters
        set "filename=!filename:"=!"
        set "content=!content:"=!"

        :: Convert Java file name to Markdown file name
        set "filename=!filename:.java=.md!"

        :: Save content to markdown file
        echo !content! > docs\!filename!
    )
)

echo Markdown files generated successfully.
