package com.example.psp.domain.valueobjects;


import com.example.psp.shared.CardValidation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.time.Clock;
import java.time.YearMonth;

/**
 * Value object representing card details.
 * Immutable and validated upon creation.
 */
@Value
@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class CardDetails {

    String cardNumber;
    String cvv;
    YearMonth expiryDate;

    /**
     * Returns the Bank Identification Number (BIN) which is the first 6 digits of the card number.
     *
     * @return the BIN of the card
     */
    public String getBIN() {
        return cardNumber.substring(0, 6);
    }

    /**
     * Returns the last digit of the card number.
     *
     * @return the last digit of the card
     */
    public String getLastDigit() {
        return cardNumber.substring(cardNumber.length() - 1);
    }

    /**
     * Builder method to create a CardDetails instance with validation.
     *
     * @param cardNumber the card number
     * @param cvv        the card CVV
     * @param expiryDate the card expiry date
     * @param clock      the clock to use for current time (for testing purposes)
     * @return a validated CardDetails instance
     * @throws IllegalArgumentException if any validation fails
     * @throws NullPointerException     if any parameter is null
     */
    @Builder(builderMethodName = "builder")
    public static CardDetails create(@NonNull String cardNumber, @NonNull String cvv, @NonNull YearMonth expiryDate, Clock clock) {

        if (!CardValidation.isValidLuhn(cardNumber)) throw new IllegalArgumentException("Invalid card number");

        if (cvv.length() != 3) throw new IllegalArgumentException("CVV must be 3 digits long");

        if (expiryDate.isBefore(YearMonth.now(clock))) throw new IllegalArgumentException("Card is expired");

        return new CardDetails(cardNumber, cvv, expiryDate);
    }


}
