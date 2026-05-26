param(
    [string]$ContainerName = "samsung-otp-generator",
    [string]$ImageName = "samsung-otp-generator",
    [string]$Tag = "latest",
    [int]$HostPort = 8080,
    [bool]$OtpEnabled = $false,
    [string]$AppAuthToken = "",
    [string]$Android15SecretKeyHex = ""
)

docker rm -f $ContainerName 2>$null
$EnvArgs = @("-e", "OTP_ENABLED=$OtpEnabled")
if ($AppAuthToken) {
    $EnvArgs += @("-e", "APP_AUTH_TOKEN=$AppAuthToken")
}
if ($Android15SecretKeyHex) {
    $EnvArgs += @("-e", "ANDROID15_SECRET_KEY_HEX=$Android15SecretKeyHex")
}

docker run -d --name $ContainerName -p "$HostPort`:8080" @EnvArgs "$ImageName`:$Tag"
Write-Host "App should be available at http://localhost:$HostPort"
