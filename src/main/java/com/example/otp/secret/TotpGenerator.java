package com.example.otp.secret;

public interface TotpGenerator {
    String generateTotp(String secret, long timeInterval);
}
