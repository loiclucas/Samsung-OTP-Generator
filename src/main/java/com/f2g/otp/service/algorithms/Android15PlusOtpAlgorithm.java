package com.f2g.otp.service.algorithms;

import com.f2g.otp.model.Suggestion;
import com.f2g.otp.service.OtpResult;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.time.Clock;
import java.util.List;

public class Android15PlusOtpAlgorithm implements OtpAlgorithm {
    private static final long TIME_STEP_SECONDS = 300L;
    private static final int CODE_DIGITS = 6;
    private static final int DIGIT_DECIMAL = 1_000_000;

    private final byte[] secretKeyBytes;
    private final Clock clock;

    public Android15PlusOtpAlgorithm(String secretKeyHex) {
        this(secretKeyHex, Clock.systemUTC());
    }

    public Android15PlusOtpAlgorithm(String secretKeyHex, Clock clock) {
        if (secretKeyHex == null || secretKeyHex.isBlank()) {
            throw new IllegalArgumentException("Android 15+ secret key is required.");
        }
        if (secretKeyHex.length() % 2 != 0 || !secretKeyHex.matches("[0-9A-Fa-f]+")) {
            throw new IllegalArgumentException("Android 15+ secret key must be even-length hex.");
        }
        this.secretKeyBytes = hexToBytes(secretKeyHex);
        this.clock = clock;
    }

    @Override
    public OtpResult generate(String seed) {
        try {
            String code = generateAtOffset(seed, 0);
            return OtpResult.success(
                "Android 15+ OTP generated.",
                code,
                List.of(new Suggestion(0, code, true))
            );
        } catch (IllegalArgumentException ex) {
            return OtpResult.error(ex.getMessage());
        }
    }

    public String generateAtOffset(String providedKey, int offsetSteps) {
        if (providedKey == null || !providedKey.matches("\\d{8}")) {
            throw new IllegalArgumentException("Android 15+ keys must be exactly 8 numeric digits.");
        }

        String nonce = providedKey.length() >= 2 ? providedKey.substring(2) : "";
        long currentTimestamp = clock.instant().getEpochSecond();
        long timeStep = (currentTimestamp / TIME_STEP_SECONDS) + offsetSteps;
        String timeHex = Long.toHexString(timeStep).toUpperCase();
        timeHex = leftPad(timeHex, 16);

        return generateTotpNonce(timeHex, nonce);
    }

    private String generateTotpNonce(String timeHex, String nonce) {
        boolean numericSixDigits = nonce.length() == 6 && nonce.chars().allMatch(Character::isDigit);
        String messageHex = numericSixDigits ? nonce + timeHex : timeHex;
        int paddingLength = numericSixDigits ? 22 : 16;
        byte[] messageBytes = hexToBytes(leftPad(messageHex, paddingLength));
        byte[] hmacHash = hmacSha256(secretKeyBytes, messageBytes);

        int offset = hmacHash[hmacHash.length - 1] & 0x0F;
        int largeInt = 0;
        for (int i = offset; i < offset + 4; i++) {
            largeInt = (largeInt << 8) | (hmacHash[i] & 0xFF);
        }
        largeInt = largeInt & 0x7FFFFFFF;

        int otpValue = largeInt % DIGIT_DECIMAL;
        return String.format("%0" + CODE_DIGITS + "d", otpValue);
    }

    private static byte[] hmacSha256(byte[] key, byte[] msg) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(key, "HmacSHA256"));
            return mac.doFinal(msg);
        } catch (GeneralSecurityException ex) {
            throw new IllegalStateException("Unable to initialize HMAC-SHA256.", ex);
        }
    }

    private static byte[] hexToBytes(String hex) {
        byte[] bytes = new byte[hex.length() / 2];
        for (int i = 0; i < hex.length(); i += 2) {
            bytes[i / 2] = (byte) Integer.parseInt(hex.substring(i, i + 2), 16);
        }
        return bytes;
    }

    private static String leftPad(String value, int length) {
        if (value.length() >= length) {
            return value;
        }
        return "0".repeat(length - value.length()) + value;
    }
}
