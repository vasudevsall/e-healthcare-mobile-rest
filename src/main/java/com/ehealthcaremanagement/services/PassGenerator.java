package com.ehealthcaremanagement.services;

import java.util.Random;

public class PassGenerator {

    private static final String CAPITAL = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String SMALL = "abcdefghijklmnopqrstuvwxyz";
    private static final String NUMBERS = "123456789";
    private static final String SYMBOLS = "!@#$%^&*";

    public static String generatePassword() {
        String values = CAPITAL + SMALL + NUMBERS + SYMBOLS;
        String password = "";
        Random random = new Random();

        for(int i=0; i<8; i++) {
            password = password + values.charAt(random.nextInt(values.length()));
        }
        password += NUMBERS.charAt(random.nextInt(NUMBERS.length()));
        password += SYMBOLS.charAt(random.nextInt(SYMBOLS.length()));

        return password;
    }

    public static int generateOtp() {
        Random random = new Random();
        String otp = "";

        for(int i=0; i<6; i++) {
            otp += NUMBERS.charAt(random.nextInt(NUMBERS.length()));
        }

        return Integer.parseInt(otp);
    }
}
