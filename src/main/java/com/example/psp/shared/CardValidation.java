package com.example.psp.shared;

import java.util.stream.IntStream;

public final class CardValidation {

    // Limit to typical length for most card types for simplicity
    public final static int CARD_LENGTH = 16;

    private CardValidation() {
        // Private constructor to prevent instantiation
    }

    public static boolean isValidPan(String pan) {
        if (pan == null) return false;
        String normalizedPan = pan.replaceAll("[^0-9]", "");
        return normalizedPan.length() == CARD_LENGTH;
    }

    public static boolean isValidLuhn(String pan) {
        if (!isValidPan(pan)) {
            return false;
        }

        int[] digits = pan.chars().map(c -> c - '0').toArray();

        return IntStream.range(0,digits.length)
                        .map (i -> (((i%2) ^ (digits.length%2)) == 0) ? ((2*digits[i])/10+(2*digits[i])%10) : digits[i])
                        .sum() % 10 == 0;

    }
}
