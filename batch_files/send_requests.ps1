# Load the JSON file containing key-value pairs
$chunks = Get-Content -Raw -Path "batch_files\chunks.json" | ConvertFrom-Json

# API endpoint
$apiUrl = "https://2bf9-2401-4900-65a7-e0c8-1428-e71c-3cf-3631.ngrok-free.app/postChunks"

# Progress tracking
$totalRequests = $chunks.PSObject.Properties.Count
$processedRequests = 0

# Iterate through each key-value pair and send an API request
foreach ($pair in $chunks.PSObject.Properties) {
    $key = $pair.Name
    $value = $pair.Value

    # Create a JSON object containing only the current key-value pair
    $jsonBody = @{ "$key" = $value } | ConvertTo-Json -Depth 10

    # Send API request
    try {
        $response = Invoke-RestMethod -Uri $apiUrl -Method Post -Body $jsonBody -ContentType "application/json"

        # Print the response to check if it's null
        Write-Host "Response for ${key}:`n$response"

        # Ensure output.json is properly formatted
        $formattedResponse = @{ "key" = $key; "response" = $response } | ConvertTo-Json -Depth 10
        Add-Content -Path "batch_files\output.json" -Value $formattedResponse
    }
    catch {
        Write-Host "Failed to process $key. Error: $_"
    }

    # Update progress
    $processedRequests++
    Write-Host "Processed $processedRequests / $totalRequests requests."
}
