package com.f2g.otp.model;

import java.util.ArrayList;
import java.util.List;

public class GenerateResponse {
    public boolean success;
    public String keyFormat;
    public String message;
    public String recommended;
    public List<Suggestion> suggestions = new ArrayList<>();
    public String timestampUtc;

    public GenerateResponse() {}

    public GenerateResponse(
        boolean success,
        String keyFormat,
        String message,
        String recommended,
        List<Suggestion> suggestions,
        String timestampUtc
    ) {
        this.success = success;
        this.keyFormat = keyFormat;
        this.message = message;
        this.recommended = recommended;
        this.suggestions = suggestions;
        this.timestampUtc = timestampUtc;
    }
}
