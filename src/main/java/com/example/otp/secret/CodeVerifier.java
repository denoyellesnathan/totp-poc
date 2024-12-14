package com.example.otp.secret;

import com.example.otp.OtpAuth;

public interface CodeVerifier {
    boolean verify(OtpAuth otpAuth, TotpGenerator totpGenerator, String code);
}
