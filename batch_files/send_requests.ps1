$chunksFile = "batch_files\chunks.json"
$outputFile = "batch_files\output.json"

if (!(Test-Path $chunksFile)) {
    Write-Host "Error: chunks.json not found!"
    exit 1
}

$jsonData = Get-Content -Raw $chunksFile | ConvertFrom-Json
$totalRequests = $jsonData.PSObject.Properties.Count
$processedRequests = 0

$outputJson = @{}

foreach ($pair in $jsonData.PSObject.Properties) {
    $requestBody = @{ $pair.Name = $pair.Value } | ConvertTo-Json -Compress

    Write-Host "Sending API request for file: $($pair.Name)"

    $response = Invoke-RestMethod -Uri "https://2bf9-2401-4900-65a7-e0c8-1428-e71c-3cf-3631.ngrok-free.app/postChunks" `
                                  -Method Post `
                                  -ContentType "application/json" `
                                  -Body $requestBody

    # Store result
    $outputJson[$pair.Name] = $response

    # Save to output.json after each request
    $outputJson | ConvertTo-Json -Depth 10 | Set-Content -Path $outputFile -Encoding UTF8

    $processedRequests++
    Write-Host "Processed $processedRequests / $totalRequests requests."
}

Write-Host "All API requests completed!"
