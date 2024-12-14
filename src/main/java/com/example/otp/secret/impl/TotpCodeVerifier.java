package com.example.otp.secret.impl;

import com.example.otp.OtpAuth;
import com.example.otp.secret.CodeVerifier;
import com.example.otp.secret.TotpGenerator;

import java.time.Instant;

public class TotpCodeVerifier implements CodeVerifier {

    @Override
    public boolean verify(OtpAuth otpAuth, TotpGenerator totpGenerator, String code) {
        String c2 = totpGenerator.generateTotp(otpAuth.getSecret(), Math.floorDiv(Instant.now().getEpochSecond(), otpAuth.getPeriod()));
        return c2.equals(code);
    }
}
