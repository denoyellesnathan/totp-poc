package com.example.otp;

import com.example.otp.secret.SecretGenerator;
import lombok.Builder;
import lombok.Getter;
import net.glxn.qrgen.core.image.ImageType;
import net.glxn.qrgen.javase.QRCode;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Getter
public class OtpAuth {

    private final SecretGenerator secretGenerator;
    private final String label;
    private final String issuer;
    private final int period;
    private final int digits;
    private String secret;

    @Builder
    public OtpAuth(SecretGenerator secretGenerator, String label, String issuer, int period, int digits) {
        this.secretGenerator = secretGenerator;
        this.label = label;
        this.issuer = issuer;
        this.period = period;
        this.digits = digits;
    }

    public String getSecret() {
        if (secret == null) {
            secret = secretGenerator.generate();
        }
        return secret;
    }

    public String getURI() {
        return "otpauth://totp/" + getLabel() + "?secret=" + getSecret() + "&issuer=" + getIssuer() + "&period=" + getPeriod() + "&digits=" + getDigits();
    }

    public BufferedImage getQRCode() throws IOException {
        QRCode.from("Hello World").to(ImageType.JPG).stream();
        ByteArrayOutputStream stream = QRCode
                .from(getURI())
                .withSize(250, 250)
                .stream();
        ByteArrayInputStream bis = new ByteArrayInputStream(stream.toByteArray());
        return ImageIO.read(bis);
    }
}
