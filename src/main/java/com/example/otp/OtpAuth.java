package com.example.otp;

import com.example.otp.secret.SecretGenerator;
import com.example.otp.secret.impl.OtpSecretGenerator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Getter;
import net.glxn.qrgen.core.image.ImageType;
import net.glxn.qrgen.javase.QRCode;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;

@Getter
public class OtpAuth implements Serializable {

    @JsonIgnore
    private SecretGenerator secretGenerator = new OtpSecretGenerator();
    private String label;
    private String issuer;
    private int period;
    private int digits;
    private String secret;

    public OtpAuth() {
        // Used for serialization and deserialization.
    }

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

    @JsonIgnore
    public String getURI() {
        return "otpauth://totp/" + getLabel() + "?secret=" + getSecret() + "&issuer=" + getIssuer() + "&period=" + getPeriod() + "&digits=" + getDigits();
    }

    @JsonIgnore
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
