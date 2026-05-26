# Samsung OTP Generator

Private Java/Javalin web application for generating Samsung OTP authentication codes from two authorized Android flows:

- Old Android legacy mixed keys.
- Android 15+ 8-digit numeric keys.

Keep this repository private. Do not expose the app publicly without TLS, access control, and an approved operational policy.

## Runtime configuration

OTP execution is disabled by default.

| Variable | Required | Description |
|---|---:|---|
| `OTP_ENABLED` | no | Set to `true` to enable real OTP generation. Defaults to `false`. |
| `APP_AUTH_TOKEN` | yes when enabled | Required token for `POST /api/generate`. Send as `X-App-Auth-Token` or `Authorization: Bearer ...`. |
| `ANDROID15_SECRET_KEY_HEX` | yes when enabled | Hex HMAC key for Android 15+ generation. Do not commit this value. |
| `PORT` | no | HTTP port. Defaults to `8080`. |

When `OTP_ENABLED=false`, the app still serves the UI and identifies key formats, but generation returns a disabled/configuration message.

## Build

```powershell
.\scripts\build.ps1
```

Equivalent Docker command:

```bash
docker build -t samsung-otp-generator:latest .
```

## Run

Disabled mode:

```powershell
.\scripts\run.ps1
```

Enabled mode:

```bash
docker run -d --name samsung-otp-generator -p 8080:8080 \
  -e OTP_ENABLED=true \
  -e APP_AUTH_TOKEN="<set-private-token>" \
  -e ANDROID15_SECRET_KEY_HEX="<set-private-hex-key>" \
  samsung-otp-generator:latest
```

Open:

```text
http://localhost:8080
```

## API

```bash
curl -X POST http://localhost:8080/api/generate \
  -H "Content-Type: application/json" \
  -H "X-App-Auth-Token: <set-private-token>" \
  -d "{\"seed\":\"12345678\",\"formatHint\":\"ANDROID_15_PLUS\"}"
```

`formatHint` is optional:

- `AUTO`
- `LEGACY_PRE_ANDROID_15`
- `ANDROID_15_PLUS`

## Export image for another server

```powershell
.\scripts\export-image.ps1 -OutputDir ".\docker-image"
```

This writes:

- `docker-image\samsung-otp-generator.tar`
- `docker-image\samsung-otp-generator.tar.sha256`

On the server:

```bash
docker load -i samsung-otp-generator.tar
docker run -d --name samsung-otp-generator -p 8080:8080 \
  -e OTP_ENABLED=true \
  -e APP_AUTH_TOKEN="<set-private-token>" \
  -e ANDROID15_SECRET_KEY_HEX="<set-private-hex-key>" \
  samsung-otp-generator:latest
```

## Security notes

- Never commit `ANDROID15_SECRET_KEY_HEX`.
- Never log input keys, generated OTPs, or runtime secrets.
- Keep generated Docker tar files out of Git. `.gitignore` already excludes `*.tar` and `docker-image/`.
- Rotate `APP_AUTH_TOKEN` and `ANDROID15_SECRET_KEY_HEX` if they are shared outside the authorized environment.
