package com.f2g.otp.service;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AppConfigTest {
    @Test
    void allowsDisabledOtpWithoutSecrets() {
        assertDoesNotThrow(() -> AppConfig.from(Map.of()));
    }

    @Test
    void requiresAuthTokenWhenOtpEnabled() {
        assertThrows(IllegalStateException.class, () -> AppConfig.from(Map.of(
            "OTP_ENABLED", "true",
            "ANDROID15_SECRET_KEY_HEX", "1200"
        )));
    }

    @Test
    void requiresAndroid15SecretWhenOtpEnabled() {
        assertThrows(IllegalStateException.class, () -> AppConfig.from(Map.of(
            "OTP_ENABLED", "true",
            "APP_AUTH_TOKEN", "token"
        )));
    }

    @Test
    void rejectsMalformedSecretHex() {
        assertThrows(IllegalStateException.class, () -> AppConfig.from(Map.of(
            "ANDROID15_SECRET_KEY_HEX", "not-hex"
        )));
    }
}
