param(
    [string]$TarFile = ".\samsung-otp-generator.tar",
    [string]$ContainerName = "samsung-otp-generator",
    [int]$HostPort = 8080,
    [bool]$OtpEnabled = $false,
    [string]$AppAuthToken = "",
    [string]$Android15SecretKeyHex = ""
)

docker load -i $TarFile
docker rm -f $ContainerName 2>$null
$EnvArgs = @("-e", "OTP_ENABLED=$OtpEnabled")
if ($AppAuthToken) {
    $EnvArgs += @("-e", "APP_AUTH_TOKEN=$AppAuthToken")
}
if ($Android15SecretKeyHex) {
    $EnvArgs += @("-e", "ANDROID15_SECRET_KEY_HEX=$Android15SecretKeyHex")
}

docker run -d --name $ContainerName -p "$HostPort`:8080" @EnvArgs samsung-otp-generator:latest
Write-Host "App should be available at http://localhost:$HostPort"
