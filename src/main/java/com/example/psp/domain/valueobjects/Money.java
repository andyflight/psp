package com.example.psp.domain.valueobjects;

import lombok.*;

import java.math.BigDecimal;
import java.util.Currency;

/**
 * Value object representing money with currency and amount.
 */
@Value
@Builder
public class Money {

    @NonNull Currency currency;
    @NonNull BigDecimal amount;

    /**
     * Private constructor to enforce validation rules.
     * @param currency the currency of the money
     * @param amount the amount of money, must be greater than zero
     * @throws IllegalArgumentException if amount is less than or equal to zero
     */
    private Money(Currency currency, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }
        this.currency = currency;
        this.amount = amount;
    }

}
