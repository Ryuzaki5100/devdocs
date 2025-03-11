# Load the JSON file containing key-value pairs
$chunks = Get-Content -Raw -Path "batch_files\chunks.json" | ConvertFrom-Json

# API endpoint
$apiUrl = "https://2bf9-2401-4900-65a7-e0c8-1428-e71c-3cf-3631.ngrok-free.app/postChunks"

# Progress tracking
$totalRequests = $chunks.PSObject.Properties.Count
$processedRequests = 0

# Path to output.json
$outputFilePath = "batch_files\output.json"

# Ensure output.json is reset to an empty object `{}` at the start
"{}" | Set-Content -Path $outputFilePath

# Load existing output.json as an empty object
$existingData = @{}

# Iterate through each key-value pair and send an API request using curl
foreach ($pair in $chunks.PSObject.Properties) {
    $key = $pair.Name
    $value = $pair.Value

    # Create a JSON object containing only the current key-value pair
    $jsonBody = @{ "$key" = $value } | ConvertTo-Json -Depth 10

    # Send API request using curl with -k (ignore SSL issues)
    try {
        $responseJson = curl.exe -s -k -X POST $apiUrl -H "Content-Type: application/json" -d $jsonBody
        $response = $responseJson | ConvertFrom-Json

        # Print the response
        Write-Host "Response for ${key}:`n$responseJson"

        # Extract the single key-value pair from the response
        $responseKey = ($response.PSObject.Properties | Select-Object -First 1).Name
        $responseValue = $response.$responseKey

        # Append the new key-value pair to output.json
        $existingData[$responseKey] = $responseValue

        # Save the updated JSON back to output.json
        $existingData | ConvertTo-Json -Depth 10 | Set-Content -Path $outputFilePath
    }
    catch {
        Write-Host "Failed to process $key. Error: $_"
    }

    # Update progress
    $processedRequests++
    Write-Host "Processed $processedRequests / $totalRequests requests."

    # Delay for 10 seconds
    Start-Sleep -Seconds 10
}
