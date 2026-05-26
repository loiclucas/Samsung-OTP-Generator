package com.f2g.otp.service;

import com.f2g.otp.model.Suggestion;

import java.util.ArrayList;
import java.util.List;

public class OtpResult {
    public boolean success;
    public String message;
    public String recommended;
    public List<Suggestion> suggestions = new ArrayList<>();

    public static OtpResult notConfigured(String message) {
        OtpResult result = new OtpResult();
        result.success = false;
        result.message = message;
        result.recommended = "";
        return result;
    }

    public static OtpResult success(String message, String recommended, List<Suggestion> suggestions) {
        OtpResult result = new OtpResult();
        result.success = true;
        result.message = message;
        result.recommended = recommended;
        result.suggestions = suggestions;
        return result;
    }

    public static OtpResult error(String message) {
        OtpResult result = new OtpResult();
        result.success = false;
        result.message = message;
        result.recommended = "";
        return result;
    }
}
