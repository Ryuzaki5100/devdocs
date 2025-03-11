# Load JSON data from chunks.json
$jsonData = Get-Content -Raw -Path "chunks.json" | ConvertFrom-Json

# Define API endpoint
$apiUrl = "https://your-api-endpoint.com"

# Counter for processed requests
$counter = 0

# Iterate through each key-value pair in JSON
foreach ($key in $jsonData.PSObject.Properties.Name) {
    $value = $jsonData.$key

    # Create JSON body
    $body = @{
        $key = $value
    } | ConvertTo-Json -Compress

    # Send PUT request
    try {
        $response = Invoke-RestMethod -Uri $apiUrl -Method Put -Body $body -ContentType "application/json"

        # Increment and print counter
        $counter++
        Write-Output "Processed: $counter / $($jsonData.PSObject.Properties.Count)"

        # Optionally, print response for debugging
        Write-Output "Response: $response"
    } catch {
        Write-Output "Error processing $key: $_"
    }
}
