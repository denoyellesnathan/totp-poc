package com.example;

import com.example.otp.OtpAuth;
import com.example.otp.secret.CodeVerifier;
import com.example.otp.secret.TotpGenerator;
import com.example.otp.secret.impl.DefaultTotpGenerator;
import com.example.otp.secret.impl.OtpSecretGenerator;
import com.example.otp.secret.impl.TotpCodeVerifier;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

@Slf4j
public class App {
    public App(String[] args) throws IOException {
        OtpAuth otpAuth = args.length == 0 ? OtpAuth.builder()
                .label("testemail@testingemail.xyz")
                .period(30)
                .digits(6)
                .issuer("TEST")
                .secretGenerator(new OtpSecretGenerator())
                .build() : loadFromFile(args[0]);
        if (args.length == 0) {
            persistTotp(otpAuth);
        }

        log.info("OtpAuth loaded with secret: {}", otpAuth.getSecret());
        CodeVerifier codeVerifier = new TotpCodeVerifier();
        TotpGenerator totpGenerator = new DefaultTotpGenerator();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            log.info("Type 'exit' to exit.");
            log.info("Enter TOTP code: ");
            String input = scanner.nextLine();

            if (input.equals("exit")) {
                break;
            }

            log.info("TOTP Validation Result: {}", codeVerifier.verify(otpAuth, totpGenerator, input));
        }
    }

    public static void main(String[] args) {
        try {
            new App(args);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private OtpAuth loadFromFile(String filename) throws IOException {
        log.info("Loading OTP config from file: {}", filename);
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(new File(filename), OtpAuth.class);
    }

    private void persistTotp(OtpAuth otpAuth) {
        // Generate QR code with secret for user to sync with Google Authenticator.
        try {
            File out = new File("otp/" + System.currentTimeMillis());
            if (out.exists() || out.mkdirs()) {
                File outputFile = new File(out.getPath() + "/qr.jpg");
                ImageIO.write(otpAuth.getQRCode(), "jpg", outputFile);

                ObjectMapper mapper = new ObjectMapper();
                mapper.writeValue(new File(out.getPath() + "/otp.json"), otpAuth);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
