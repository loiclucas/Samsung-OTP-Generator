package com.f2g.otp.service;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OtpServiceTest {
    @Test
    void detectsAndroid15NumericEightDigitKeys() {
        OtpService service = new OtpService(AppConfig.from(Map.of()));

        assertEquals(KeyFormat.ANDROID_15_PLUS, service.detectFormat("12345678"));
    }

    @Test
    void detectsLegacyMixedKeys() {
        OtpService service = new OtpService(AppConfig.from(Map.of()));

        assertEquals(KeyFormat.LEGACY_PRE_ANDROID_15, service.detectFormat("yp810"));
    }

    @Test
    void formatHintOverridesAutoDetection() {
        OtpService service = new OtpService(AppConfig.from(Map.of()));

        assertEquals(
            KeyFormat.LEGACY_PRE_ANDROID_15,
            service.resolveFormat("12345678", FormatHint.LEGACY_PRE_ANDROID_15)
        );
    }
}
