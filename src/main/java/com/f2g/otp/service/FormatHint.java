package com.f2g.otp.service;

public enum FormatHint {
    AUTO,
    LEGACY_PRE_ANDROID_15,
    ANDROID_15_PLUS;

    public static FormatHint from(String value) {
        if (value == null || value.isBlank()) {
            return AUTO;
        }

        try {
            return FormatHint.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            return AUTO;
        }
    }
}
