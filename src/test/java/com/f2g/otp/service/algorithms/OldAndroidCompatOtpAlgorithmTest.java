package com.f2g.otp.service.algorithms;

import com.f2g.otp.model.Suggestion;
import com.f2g.otp.service.OtpResult;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OldAndroidCompatOtpAlgorithmTest {
    @Test
    void portsOldHashCodeExactly() {
        OldAndroidCompatOtpAlgorithm algorithm = new OldAndroidCompatOtpAlgorithm();

        assertEquals(147584034, algorithm.makeHashCode("yp810"));
    }

    @Test
    void usesOldDateStringOrderExactly() {
        OldAndroidCompatOtpAlgorithm algorithm = new OldAndroidCompatOtpAlgorithm(
            Clock.fixed(Instant.parse("2026-05-26T12:34:00Z"), ZoneOffset.UTC)
        );

        assertEquals("2605342612", algorithm.getDateString(0));
    }

    @Test
    void reproducesMobileGoldenOutputForYz7h5() {
        OldAndroidCompatOtpAlgorithm algorithm = new OldAndroidCompatOtpAlgorithm(
            Clock.fixed(Instant.parse("2026-05-26T12:34:00Z"), ZoneOffset.UTC)
        );

        OtpResult result = algorithm.generate("yz7h5");
        List<Suggestion> suggestions = result.suggestions;

        assertEquals("152038854", result.recommended);
        assertEquals(6, suggestions.size());
        assertEquals(5, suggestions.get(0).offset);
        assertEquals("118833066", suggestions.get(0).code);
        assertEquals(4, suggestions.get(1).offset);
        assertEquals("147295170", suggestions.get(1).code);
        assertEquals(3, suggestions.get(2).offset);
        assertEquals("148481091", suggestions.get(2).code);
        assertEquals(2, suggestions.get(3).offset);
        assertEquals("149667012", suggestions.get(3).code);
        assertEquals(1, suggestions.get(4).offset);
        assertEquals("150852933", suggestions.get(4).code);
        assertEquals(0, suggestions.get(5).offset);
        assertEquals("152038854", suggestions.get(5).code);
    }
}
