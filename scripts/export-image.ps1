param(
    [string]$ImageName = "samsung-otp-generator",
    [string]$Tag = "latest",
    [string]$OutputDir = ".\docker-image"
)

New-Item -ItemType Directory -Force -Path $OutputDir | Out-Null
$OutputFile = Join-Path $OutputDir "samsung-otp-generator.tar"

docker save -o $OutputFile "$ImageName`:$Tag"
$HashFile = "$OutputFile.sha256"
Get-FileHash -Algorithm SHA256 $OutputFile | ForEach-Object {
    "$($_.Hash.ToLower())  $(Split-Path -Leaf $OutputFile)" | Set-Content -Path $HashFile -Encoding ascii
}

Write-Host "Docker image exported to:"
Write-Host $OutputFile
Write-Host "SHA256 checksum exported to:"
Write-Host $HashFile
