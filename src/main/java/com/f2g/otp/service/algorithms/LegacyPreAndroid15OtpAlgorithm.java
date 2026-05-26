package com.f2g.otp.service.algorithms;

import com.f2g.otp.model.Suggestion;
import com.f2g.otp.service.OtpResult;

import java.time.Clock;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class LegacyPreAndroid15OtpAlgorithm implements OtpAlgorithm {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyMMddHHmm");
    private final Clock clock;

    public LegacyPreAndroid15OtpAlgorithm() {
        this(Clock.systemUTC());
    }

    public LegacyPreAndroid15OtpAlgorithm(Clock clock) {
        this.clock = clock;
    }

    @Override
    public OtpResult generate(String seed) {
        if (seed == null || seed.isBlank()) {
            return OtpResult.error("Legacy keys must not be empty.");
        }

        List<Suggestion> suggestions = new ArrayList<>();
        String recommended = "";
        for (int offset = 5; offset >= -5; offset--) {
            String code = generateForOffset(seed, offset);
            boolean isRecommended = offset == 0;
            if (isRecommended) {
                recommended = code;
            }
            suggestions.add(new Suggestion(offset, code, isRecommended));
        }

        return OtpResult.success("Legacy pre-Android 15 OTP suggestions generated.", recommended, suggestions);
    }

    public String generateForOffset(String key, int offsetMinutes) {
        return Integer.toString(makeHashCode(key + getDateString(offsetMinutes)));
    }

    public String getDateString(int offsetMinutes) {
        ZonedDateTime utcTime = ZonedDateTime.now(clock)
            .withZoneSameInstant(ZoneOffset.UTC)
            .minusMinutes(offsetMinutes);
        return utcTime.format(DATE_FORMATTER);
    }

    public int makeHashCode(String value) {
        int hashValue = 0;
        for (int i = 0; i < value.length(); i++) {
            hashValue = (hashValue * 5) + value.charAt(i);
        }
        if (hashValue < 0) {
            hashValue = hashValue * -1;
        }
        return hashValue;
    }
}
