package com.f2g.otp.service.algorithms;

import com.f2g.otp.service.OtpResult;

public interface OtpAlgorithm {
    OtpResult generate(String seed);
}
