# Codex Continuation Summary

## Project status

A safe Dockerized Java web app scaffold has been prepared for an internal OTP generator workflow.

The app is intentionally structured so Codex or a developer can continue work cleanly:

- Backend: Java 17, Javalin 5, Jackson, SLF4J/Logback.
- Frontend: static `index.html` served by Javalin.
- Packaging: Docker multi-stage build.
- Distribution: helper scripts for building, running, saving, and loading the Docker image.

## Critical safety boundary

Do **not** add, infer, reconstruct, or distribute code whose purpose is to bypass, defeat, or automate access through a third-party authentication challenge unless this is explicitly authorized and documented by the owning organization.

This scaffold currently contains only:
- input validation,
- key format detection,
- response models,
- logging,
- runtime-gated algorithm classes.

The actual authentication-challenge logic is deliberately omitted.

## Repo purpose

The repository is intended to be used as a controlled internal scaffold where authorized developers can add approved proprietary logic later.

## Core workflow

1. User opens the web UI at `/`.
2. User enters an input key.
3. Frontend sends:

```json
{
  "seed": "12345678"
}
```

to:

```text
POST /api/generate
```

4. Backend trims the value and detects format:
   - exactly 8 numeric digits → `ANDROID_15_PLUS`
   - length 6 to 8 and contains at least one letter and one digit → `LEGACY_PRE_ANDROID_15`
   - otherwise → `UNKNOWN`

5. Backend calls an algorithm implementation:
   - `Android15PlusOtpAlgorithm`
   - `LegacyPreAndroid15OtpAlgorithm`

6. OTP execution is disabled by default unless `OTP_ENABLED=true`.

7. Frontend displays the detected format and message.

## Main files

### `OtpWebApp.java`

Entry point. Creates Javalin app, serves static files, registers API endpoint:

```java
app.post("/api/generate", OtpWebApp::handleGenerate);
```

It logs:
- client IP,
- key format,
- success/failure,
- request timestamp,
- message.

### `OtpService.java`

Business logic coordinator.

Important methods:
- `generate(String rawSeed, String clientIp)`
- `detectFormat(String seed)`

This is the main extension point for routing new algorithm implementations.

### `KeyFormat.java`

Enum:

```java
LEGACY_PRE_ANDROID_15
ANDROID_15_PLUS
UNKNOWN
```

### `OtpAlgorithm.java`

Interface for algorithm classes:

```java
OtpResult generate(String seed);
```

### Algorithm implementations

- `LegacyPreAndroid15OtpAlgorithm`
- `Android15PlusOtpAlgorithm`

These implementations are gated by runtime configuration. The Android 15+ HMAC key is loaded from `ANDROID15_SECRET_KEY_HEX`.

### `GenerateRequest.java`

Incoming API DTO:

```java
public String seed;
```

### `GenerateResponse.java`

Outgoing API DTO:
- `success`
- `keyFormat`
- `message`
- `recommended`
- `suggestions`
- `timestampUtc`

### `Suggestion.java`

Represents a code suggestion row:
- `offset`
- `code`
- `recommended`

## Frontend

File:

```text
src/main/resources/public/index.html
```

Features:
- F2G-themed colors.
- Top-left logo.
- Input supports up to 8 characters.
- Button: "Generate OTP authentication code".
- Shows detected mode, success/failure message, recommended value if present, and suggestions table if present.

## Docker

File:

```text
Dockerfile
```

Build:

```bash
docker build -t samsung-otp-generator:latest .
```

Run:

```bash
docker run -d --name samsung-otp-generator -p 8080:8080 samsung-otp-generator:latest
```

Export:

```bash
docker save -o samsung-otp-generator.tar samsung-otp-generator:latest
```

## Logging

File:

```text
src/main/resources/logback.xml
```

Logs write to:

```text
/app/logs/otp.log
```

Rolling policy:
- 1 MB max file size,
- 5 retained files.

## Recommended next tasks for Codex

1. Add unit tests for key format detection.
2. Add explicit input constraints to frontend and backend.
3. Add a health endpoint:
   - `GET /health`
   - response: `{"status":"ok"}`
4. Add Docker Compose for easier run commands.
5. Add optional mounted volume for logs:
   - host path → `/app/logs`
6. If authorized, add proprietary algorithm implementations behind the `OtpAlgorithm` interface.
7. Add environment-controlled mode:
   - `SAFE_MODE=true` by default.
   - Refuse algorithm execution when not explicitly enabled.
8. Add a `README_INTERNAL.md` with authorization, audit, and release process.
9. Add GitHub Actions workflow:
   - Maven build
   - Docker build
   - optional image artifact upload.
10. Add server deployment notes for:
    - `docker save`
    - `docker load`
    - `docker run`
    - firewall/port checks.

## Suggested test cases

### Format detection

| Input | Expected |
|---|---|
| `12345678` | ANDROID_15_PLUS |
| `yy182a` | LEGACY_PRE_ANDROID_15 |
| `yy182` | UNKNOWN or adjust rule depending on business need |
| `abcdef` | UNKNOWN |
| `123456` | UNKNOWN |
| empty string | UNKNOWN |

### API behavior

Request:

```bash
curl -X POST http://localhost:8080/api/generate \
  -H "Content-Type: application/json" \
  -d "{\"seed\":\"12345678\"}"
```

Expected:
- HTTP 200
- `success=false`
- `keyFormat=ANDROID_15_PLUS`
- message says algorithm not configured.

## Notes for migration from previous version

The earlier app had a direct algorithm implementation embedded in Java. This scaffold separates routing and algorithm execution so Android-version-specific logic can be maintained independently.

Important design change:
- `code` and `recommended` are strings, not integers.
- This supports 6-digit codes with leading zeros.
- It also avoids overflow problems in future algorithms.

## Security guidance

- Keep the repository private.
- Do not hardcode sensitive secrets in source.
- Use environment variables or secret management for any internal secret material.
- Never write generated OTPs to logs in production unless policy explicitly allows it.
- Consider masking input keys in logs.
- Add authentication before exposing outside localhost.
