package com.crestostudio.restaurant_pos.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class OtpGenerator {

    private final int otpLength;
    private final SecureRandom secureRandom = new SecureRandom();

    public OtpGenerator(@Value("${app.otp.length}") int otpLength) {
        this.otpLength = otpLength;
    }

    public String generate() {
        int bound = (int) Math.pow(10, otpLength);
        int otp = secureRandom.nextInt(bound);
        return String.format("%0" + otpLength + "d", otp);
    }
}
