package com.example.otp.secret.impl;

import com.example.exception.TotpException;
import com.example.otp.secret.TotpGenerator;
import org.apache.commons.codec.binary.Base32;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class DefaultTotpGenerator implements TotpGenerator {

    private static final String SHA1 = "HmacSHA1";
    private static final int TOTP_LENGTH = 6;

    private final Base32 base32 = new Base32();

    @Override
    public String generateTotp(String secret, long timeInterval) {
        try {
            byte[] decodedKey = base32.decode(secret);
            byte[] timeIntervalBytes = getTimeIntervalBytes(timeInterval);

            // SHA1 algorithm to hash the time interval with the secret (RFC 6238) and HOTP (RFC 4226) standards.
            byte[] hash = getShaHash(decodedKey, timeIntervalBytes);

            /*
             * The line offset = hash[hash.length - 1] & 0xF; is used to determine the offset into the HMAC hash
             * from which a 4-byte dynamic binary code will be extracted to generate the TOTP.
             * This method of determining the offset is specified in the TOTP (RFC 6238) and HOTP (RFC 4226) standards.
             */
            int offset = hash[hash.length - 1] & 0xF;

            /*
             * The expression hash[offset] & 0x7F uses the hexadecimal value 0x7F to mask
             * the most significant bit (MSB) of the byte at hash[offset],
             * ensuring it's set to 0. The reason for this is to make sure that the resulting 32-bit integer
             * (binaryCode) is treated as a positive number. Reference TOTP (RFC 6238)
             */
            long mostSignificantByte = (hash[offset] & 0x7F) << 24;
            long secondMostSignificantByte = (hash[offset + 1] & 0xFF) << 16;
            long thirdMostSignificantByte = (hash[offset + 2] & 0xFF) << 8;
            long leastSignificantByte = hash[offset + 3] & 0xFF;
            long binaryCode = mostSignificantByte | secondMostSignificantByte | thirdMostSignificantByte | leastSignificantByte;

            int totp = (int) (binaryCode % Math.pow(10, TOTP_LENGTH));
            return String.format("%0" + TOTP_LENGTH + "d", totp); // Making sure length is equal to TOTP_LENGTH
        } catch (Exception e) {
            throw new TotpException("Failed to generate TOTP", e);
        }
    }

    public byte[] getShaHash(byte[] decodedKey, byte[] timeIntervalBytes) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac hmac = Mac.getInstance(SHA1);
        hmac.init(new SecretKeySpec(decodedKey, SHA1));
        return hmac.doFinal(timeIntervalBytes);
    }

    public byte[] getTimeIntervalBytes(long timeInterval) {
        byte[] timeIntervalBytes = new byte[8];

        /*
         * RFC 4226, it's specified that the counter value
         * (in the case of TOTP, is derived from the current time)
         * should be represented in big-endian format when used as input for the HMAC computation.
         * Reference: https://datatracker.ietf.org/doc/html/rfc4226
         */

        // Convert the timeInterval into its byte representation
        for (int i = 7; i >= 0; i--) {
            timeIntervalBytes[i] = (byte) (timeInterval & 0xFF);
            timeInterval >>= 8;
        }
        return timeIntervalBytes;
    }
}
