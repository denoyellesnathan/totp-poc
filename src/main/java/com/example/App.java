package com.example;

import com.example.otp.OtpAuth;
import com.example.otp.secret.impl.DefaultTotpGenerator;
import com.example.otp.secret.impl.OtpSecretGenerator;
import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import java.io.File;
import java.time.Instant;
import java.util.Scanner;

@Slf4j
public class App {
    public static void main(String[] args) {
        OtpAuth otpAuth = OtpAuth.builder()
                .label("testemail@testingemail.xyz")
                .period(30)
                .digits(6)
                .issuer("TEST")
                .secretGenerator(new OtpSecretGenerator())
                .build();

        // Generate QR code with secret for user to sync with Google Authenticator.
        try {
            File outputfile = new File("image.jpg");
            ImageIO.write(otpAuth.getQRCode(), "jpg", outputfile);
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        DefaultTotpGenerator totpGenerator = new DefaultTotpGenerator();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            log.info("Press ENTER to refresh TOTP code.");
            log.info("Type 'exit' to exit.");

            if (scanner.nextLine().equals("exit")) {
                break;
            }

            log.info("TOTP: {}", totpGenerator.generateTotp(otpAuth.getSecret(), Math.floorDiv(Instant.now().getEpochSecond(), otpAuth.getPeriod())));
        }
    }
}
