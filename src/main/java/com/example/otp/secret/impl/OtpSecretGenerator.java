package com.example.otp.secret.impl;

import com.example.otp.secret.SecretGenerator;
import org.apache.commons.codec.binary.Base32;

import java.security.SecureRandom;

public class OtpSecretGenerator implements SecretGenerator {

    private final SecureRandom secureRandom = new SecureRandom();
    private final Base32 base32 = new Base32();

    @Override
    public String generate() {
        byte[] bytes = new byte[20];
        secureRandom.nextBytes(bytes);
        return base32.encodeAsString(bytes);
    }
}
