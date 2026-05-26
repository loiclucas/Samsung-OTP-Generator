package com.f2g.otp.service;

import java.util.Map;

public class AppConfig {
    public final boolean otpEnabled;
    public final String appAuthToken;
    public final String android15SecretKeyHex;

    private AppConfig(boolean otpEnabled, String appAuthToken, String android15SecretKeyHex) {
        this.otpEnabled = otpEnabled;
        this.appAuthToken = appAuthToken;
        this.android15SecretKeyHex = android15SecretKeyHex;
    }

    public static AppConfig fromEnv() {
        return from(System.getenv());
    }

    public static AppConfig from(Map<String, String> env) {
        boolean otpEnabled = Boolean.parseBoolean(env.getOrDefault("OTP_ENABLED", "false"));
        String appAuthToken = trimToEmpty(env.get("APP_AUTH_TOKEN"));
        String android15SecretKeyHex = trimToEmpty(env.get("ANDROID15_SECRET_KEY_HEX"));

        if (otpEnabled && appAuthToken.isBlank()) {
            throw new IllegalStateException("APP_AUTH_TOKEN is required when OTP_ENABLED=true.");
        }

        if (otpEnabled && android15SecretKeyHex.isBlank()) {
            throw new IllegalStateException("ANDROID15_SECRET_KEY_HEX is required when OTP_ENABLED=true.");
        }

        if (!android15SecretKeyHex.isBlank()) {
            validateHex(android15SecretKeyHex);
        }

        return new AppConfig(otpEnabled, appAuthToken, android15SecretKeyHex);
    }

    private static String trimToEmpty(String value) {
        return value == null ? "" : value.trim();
    }

    private static void validateHex(String value) {
        if (value.length() % 2 != 0 || !value.matches("[0-9A-Fa-f]+")) {
            throw new IllegalStateException("ANDROID15_SECRET_KEY_HEX must contain an even number of hex characters.");
        }
    }
}
