package com.example.psp.domain.valueobjects;


import com.example.psp.shared.CardValidation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.time.Clock;
import java.time.YearMonth;

@Value
@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class CardDetails {

    String cardNumber;
    String cvv;
    YearMonth expiryDate;

    public String getBIN() {
        return cardNumber.substring(0, 6);
    }

    public String getLastDigit() {
        return cardNumber.substring(cardNumber.length() - 1);
    }

    @Builder(builderMethodName = "builder")
    public static CardDetails create(@NonNull String cardNumber, @NonNull String cvv, @NonNull YearMonth expiryDate, Clock clock) {

        if (!CardValidation.isValidLuhn(cardNumber)) throw new IllegalArgumentException("Invalid card number");

        if (cvv.length() != 3) throw new IllegalArgumentException("CVV must be 3 digits long");

        if (expiryDate.isBefore(YearMonth.now(clock))) throw new IllegalArgumentException("Card is expired");

        return new CardDetails(cardNumber, cvv, expiryDate);
    }


}
