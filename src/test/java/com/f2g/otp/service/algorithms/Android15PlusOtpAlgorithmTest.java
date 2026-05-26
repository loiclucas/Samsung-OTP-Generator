package com.f2g.otp.service.algorithms;

import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class Android15PlusOtpAlgorithmTest {
    private static final String SECRET_KEY_HEX =
        "000102030405060708090A0B0C0D0E0F101112131415161718191A1B1C1D1E1F";

    @Test
    void generatesKnownCodeAtFixedUnixEpoch() {
        Android15PlusOtpAlgorithm algorithm = new Android15PlusOtpAlgorithm(
            SECRET_KEY_HEX,
            Clock.fixed(Instant.EPOCH, ZoneOffset.UTC)
        );

        assertEquals("738717", algorithm.generateAtOffset("12345678", 0));
    }

    @Test
    void preservesLeadingZerosByReturningStrings() {
        Android15PlusOtpAlgorithm algorithm = new Android15PlusOtpAlgorithm(
            SECRET_KEY_HEX,
            Clock.fixed(Instant.ofEpochSecond(1_710_000_000L), ZoneOffset.UTC)
        );

        assertEquals(6, algorithm.generateAtOffset("12345678", 0).length());
    }

    @Test
    void rejectsInvalidAndroid15Keys() {
        Android15PlusOtpAlgorithm algorithm = new Android15PlusOtpAlgorithm(
            SECRET_KEY_HEX,
            Clock.fixed(Instant.EPOCH, ZoneOffset.UTC)
        );

        assertThrows(IllegalArgumentException.class, () -> algorithm.generateAtOffset("yp810", 0));
    }
}
