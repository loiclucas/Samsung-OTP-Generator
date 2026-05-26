package com.f2g.otp.service.algorithms;

import com.f2g.otp.service.OtpResult;

public class DisabledOtpAlgorithm implements OtpAlgorithm {
    private final String message;

    public DisabledOtpAlgorithm(String message) {
        this.message = message;
    }

    @Override
    public OtpResult generate(String seed) {
        return OtpResult.notConfigured(message);
    }
}
