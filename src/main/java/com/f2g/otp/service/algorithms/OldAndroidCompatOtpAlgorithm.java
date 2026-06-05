package com.f2g.otp.service.algorithms;

import com.f2g.otp.model.Suggestion;
import com.f2g.otp.service.OtpResult;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

public class OldAndroidCompatOtpAlgorithm implements OtpAlgorithm {
    private static final int MAX_OFFSET = 5;
    private static final ZoneOffset UTC = ZoneOffset.UTC;
    private final Clock clock;

    public OldAndroidCompatOtpAlgorithm() {
        this(Clock.systemUTC());
    }

    public OldAndroidCompatOtpAlgorithm(Clock clock) {
        this.clock = clock;
    }

    @Override
    public OtpResult generate(String seed) {
        if (seed == null || seed.isBlank()) {
            return OtpResult.error("Old Android keys must not be empty.");
        }

        List<Suggestion> suggestions = new ArrayList<>();
        String recommended = "";
        for (int offset = MAX_OFFSET; offset >= 0; offset--) {
            String code = generateForOffset(seed, offset);
            boolean isRecommended = offset == 0;
            if (isRecommended) {
                recommended = code;
            }
            suggestions.add(new Suggestion(offset, code, isRecommended));
        }

        return OtpResult.success("Old Android OTP suggestions generated.", recommended, suggestions);
    }

    public String generateForOffset(String seed, int offsetMinutes) {
        return Integer.toString(makeHashCode(seed + getDateString(offsetMinutes)));
    }

    public String getDateString(int offsetMinutes) {
        LocalDateTime dt = LocalDateTime.now(clock).minusMinutes(offsetMinutes);
        String yy = String.format("%02d", dt.getYear() - 2000);
        String month = String.format("%02d", dt.getMonthValue());
        String minute = String.format("%02d", dt.getMinute());
        String day = String.format("%02d", dt.getDayOfMonth());
        String hour = String.format("%02d", dt.getHour());
        return yy + month + minute + day + hour;
    }

    public int makeHashCode(String input) {
        int hashValue = 0;
        for (int i = 0; i < input.length(); i++) {
            hashValue = (hashValue * 33) + input.charAt(i);
        }
        return Math.abs(hashValue);
    }
}
