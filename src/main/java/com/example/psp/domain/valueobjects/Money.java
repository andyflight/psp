package com.example.psp.domain.valueobjects;

import lombok.*;

import java.math.BigDecimal;
import java.util.Currency;

@Value
@Builder
public class Money {

    @NonNull Currency currency;
    @NonNull BigDecimal amount;

    private Money(Currency currency, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }
        this.currency = currency;
        this.amount = amount;
    }

}
