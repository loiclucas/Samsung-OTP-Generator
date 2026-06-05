package com.f2g.otp.service;

public enum FormatHint {
    AUTO,
    OLD_ANDROID_COMPAT,
    OLD_ANDROID_SCREENSHOT,
    LEGACY_PRE_ANDROID_15,
    ANDROID_15_PLUS;

    public static FormatHint from(String value) {
        if (value == null || value.isBlank()) {
            return AUTO;
        }

        try {
            String normalized = value.trim().toUpperCase();
            if ("OLD_ANDROID".equals(normalized) || "LEGACY".equals(normalized)) {
                return OLD_ANDROID_COMPAT;
            }
            return FormatHint.valueOf(normalized);
        } catch (IllegalArgumentException ex) {
            return AUTO;
        }
    }
}
