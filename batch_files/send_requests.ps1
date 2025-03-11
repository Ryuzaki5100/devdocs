# Load the JSON file containing key-value pairs
$chunks = Get-Content -Raw -Path "batch_files\chunks.json" | ConvertFrom-Json

# API endpoint
$apiUrl = "https://2bf9-2401-4900-65a7-e0c8-1428-e71c-3cf-3631.ngrok-free.app/postChunks"

# Progress tracking
$totalRequests = $chunks.PSObject.Properties.Count
$processedRequests = 0

# Ensure output.json exists and is an array
$outputFilePath = "batch_files\output.json"
if (-Not (Test-Path $outputFilePath)) {
    "[]" | Set-Content -Path $outputFilePath
}

# Load existing output.json data
try {
    $existingData = Get-Content -Raw -Path $outputFilePath | ConvertFrom-Json
    if ($existingData -isnot [System.Array]) {
        $existingData = @()
    }
} catch {
    Write-Host "Error reading output.json. Resetting it to an empty array."
    $existingData = @()
}

# Create a JSON object containing all key-value pairs in the expected format
$formattedData = @{}
foreach ($pair in $chunks.PSObject.Properties) {
    $formattedData["$($pair.Name)"] = $pair.Value
}

$jsonBody = $formattedData | ConvertTo-Json -Depth 10

# Send API request
try {
    $response = Invoke-RestMethod -Uri $apiUrl -Method Post -Body $jsonBody -ContentType "application/json"

    # Print the response to check if it's null
    Write-Host "Response:`n$response"

    # Append the new response
    $newEntry = @{ "request" = $formattedData; "response" = $response }
    $existingData += $newEntry

    # Save the updated JSON back to output.json
    $existingData | ConvertTo-Json -Depth 10 | Set-Content -Path $outputFilePath
} catch {
    Write-Host "Failed to process request. Error: $_"
}

# Update progress
Write-Host "Processed 1 / 1 request."
