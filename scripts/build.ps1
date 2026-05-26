param(
    [string]$ImageName = "samsung-otp-generator",
    [string]$Tag = "latest"
)

docker build -t "$ImageName`:$Tag" .
