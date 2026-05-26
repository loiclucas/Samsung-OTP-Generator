package com.f2g.otp.service.algorithms;

import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LegacyPreAndroid15OtpAlgorithmTest {
    @Test
    void portsLegacyHashCodeExactly() {
        LegacyPreAndroid15OtpAlgorithm algorithm = new LegacyPreAndroid15OtpAlgorithm();

        assertEquals(91318, algorithm.makeHashCode("yp810"));
    }

    @Test
    void generatesOffsetZeroFromPinnedGmtClock() {
        LegacyPreAndroid15OtpAlgorithm algorithm = new LegacyPreAndroid15OtpAlgorithm(
            Clock.fixed(Instant.parse("2026-05-25T00:00:00Z"), ZoneOffset.UTC)
        );

        assertEquals("2605250000", algorithm.getDateString(0));
        assertEquals("1452328830", algorithm.generateForOffset("yp810", 0));
    }

    @Test
    void positiveOffsetsUseEarlierMinutesLikeScreenshotCode() {
        LegacyPreAndroid15OtpAlgorithm algorithm = new LegacyPreAndroid15OtpAlgorithm(
            Clock.fixed(Instant.parse("2026-05-25T00:00:00Z"), ZoneOffset.UTC)
        );

        assertEquals("2605242355", algorithm.getDateString(5));
        assertEquals("2605250001", algorithm.getDateString(-1));
    }
}
