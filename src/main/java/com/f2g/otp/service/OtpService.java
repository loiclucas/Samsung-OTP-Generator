package com.f2g.otp.service;

import com.f2g.otp.model.GenerateResponse;
import com.f2g.otp.service.algorithms.Android15PlusOtpAlgorithm;
import com.f2g.otp.service.algorithms.DisabledOtpAlgorithm;
import com.f2g.otp.service.algorithms.LegacyPreAndroid15OtpAlgorithm;
import com.f2g.otp.service.algorithms.OldAndroidCompatOtpAlgorithm;
import com.f2g.otp.service.algorithms.OtpAlgorithm;

import java.time.Instant;
import java.util.Collections;

public class OtpService {
    private final OtpAlgorithm oldAndroidCompatAlgorithm;
    private final OtpAlgorithm oldAndroidScreenshotAlgorithm;
    private final OtpAlgorithm android15PlusAlgorithm;

    public OtpService() {
        this(AppConfig.fromEnv());
    }

    public OtpService(AppConfig config) {
        if (config.otpEnabled) {
            this.oldAndroidCompatAlgorithm = new OldAndroidCompatOtpAlgorithm();
            this.oldAndroidScreenshotAlgorithm = new LegacyPreAndroid15OtpAlgorithm();
            this.android15PlusAlgorithm = new Android15PlusOtpAlgorithm(config.android15SecretKeyHex);
        } else {
            this.oldAndroidCompatAlgorithm = new DisabledOtpAlgorithm(
                "OTP generation is disabled. Set OTP_ENABLED=true in an authorized environment."
            );
            this.oldAndroidScreenshotAlgorithm = new DisabledOtpAlgorithm(
                "OTP generation is disabled. Set OTP_ENABLED=true in an authorized environment."
            );
            this.android15PlusAlgorithm = new DisabledOtpAlgorithm(
                "OTP generation is disabled. Set OTP_ENABLED=true and configure ANDROID15_SECRET_KEY_HEX."
            );
        }
    }

    public GenerateResponse generate(String rawSeed, String rawFormatHint) {
        String seed = rawSeed == null ? "" : rawSeed.trim();
        FormatHint formatHint = FormatHint.from(rawFormatHint);
        KeyFormat format = resolveFormat(seed, formatHint);

        if (format == KeyFormat.UNKNOWN) {
            return new GenerateResponse(
                false,
                format.name(),
                "Unsupported key format. Expected a 5-character old Android key or an Android 15+ 8-digit numeric key.",
                "",
                Collections.emptyList(),
                Instant.now().toString()
            );
        }

        String validationError = validateSeedForFormat(seed, format);
        if (!validationError.isBlank()) {
            return new GenerateResponse(
                false,
                format.name(),
                validationError,
                "",
                Collections.emptyList(),
                Instant.now().toString()
            );
        }

        OtpResult result;
        result = algorithmFor(format).generate(seed);

        return new GenerateResponse(
            result.success,
            format.name(),
            result.message,
            result.recommended,
            result.suggestions,
            Instant.now().toString()
        );
    }

    public KeyFormat resolveFormat(String seed, FormatHint hint) {
        if (hint == FormatHint.ANDROID_15_PLUS) {
            return KeyFormat.ANDROID_15_PLUS;
        }
        if (hint == FormatHint.OLD_ANDROID_SCREENSHOT) {
            return KeyFormat.OLD_ANDROID_SCREENSHOT;
        }
        if (hint == FormatHint.OLD_ANDROID_COMPAT || hint == FormatHint.LEGACY_PRE_ANDROID_15) {
            return KeyFormat.OLD_ANDROID_COMPAT;
        }
        return detectFormat(seed);
    }

    public KeyFormat detectFormat(String seed) {
        if (seed == null) {
            return KeyFormat.UNKNOWN;
        }

        String trimmed = seed.trim();

        if (trimmed.matches("\\d{8}")) {
            return KeyFormat.ANDROID_15_PLUS;
        }

        boolean hasLetter = trimmed.matches(".*[A-Za-z].*");
        boolean hasDigit = trimmed.matches(".*\\d.*");

        if (trimmed.length() == 5 && hasLetter && hasDigit) {
            return KeyFormat.OLD_ANDROID_COMPAT;
        }

        if (trimmed.length() >= 3 && trimmed.length() <= 32 && hasLetter && hasDigit) {
            return KeyFormat.OLD_ANDROID_SCREENSHOT;
        }

        return KeyFormat.UNKNOWN;
    }

    private String validateSeedForFormat(String seed, KeyFormat format) {
        if (seed == null || seed.isBlank()) {
            return "Input key is required.";
        }

        if (seed.length() > 32) {
            return "Input key is too long.";
        }

        if (!seed.matches("[A-Za-z0-9]+")) {
            return "Input key may only contain letters and digits.";
        }

        if (format == KeyFormat.ANDROID_15_PLUS && !seed.matches("\\d{8}")) {
            return "Android 15+ keys must be exactly 8 numeric digits.";
        }

        if (format == KeyFormat.OLD_ANDROID_COMPAT && seed.length() != 5) {
            return "Old Android compatibility keys must be exactly 5 characters.";
        }

        if (format == KeyFormat.OLD_ANDROID_SCREENSHOT && seed.length() < 3) {
            return "Old Android screenshot keys must contain at least 3 characters.";
        }

        return "";
    }

    private OtpAlgorithm algorithmFor(KeyFormat format) {
        if (format == KeyFormat.ANDROID_15_PLUS) {
            return android15PlusAlgorithm;
        }
        if (format == KeyFormat.OLD_ANDROID_SCREENSHOT) {
            return oldAndroidScreenshotAlgorithm;
        }
        return oldAndroidCompatAlgorithm;
    }
}
