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

        assertEquals(KeyFormat.OLD_ANDROID_COMPAT, service.detectFormat("yz7h5"));
    }

    @Test
    void formatHintOverridesAutoDetection() {
        OtpService service = new OtpService(AppConfig.from(Map.of()));

        assertEquals(
            KeyFormat.OLD_ANDROID_SCREENSHOT,
            service.resolveFormat("yz7h5", FormatHint.OLD_ANDROID_SCREENSHOT)
        );
    }

    @Test
    void legacyPreAndroid15HintRemainsBackwardCompatible() {
        OtpService service = new OtpService(AppConfig.from(Map.of()));

        assertEquals(
            KeyFormat.OLD_ANDROID_COMPAT,
            service.resolveFormat("yz7h5", FormatHint.LEGACY_PRE_ANDROID_15)
        );
    }
}
