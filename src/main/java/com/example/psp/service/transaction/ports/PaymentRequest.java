package com.example.psp.service.transaction.ports;

import com.example.psp.domain.valueobjects.CardDetails;
import com.example.psp.domain.valueobjects.Money;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

/**
 * Represents a payment request containing card details, amount of money, and merchant ID.
 */
@Value
@Builder
public class PaymentRequest {
    @NonNull CardDetails card;
    @NonNull Money money;
    @NonNull String merchantId;

    /**
     * Private constructor to enforce validation rules.
     *
     * @param card       the card details
     * @param money      the amount of money
     * @param merchantId the merchant identifier
     * @throws IllegalArgumentException if merchantId is blank
     */
    private PaymentRequest(CardDetails card, Money money, String merchantId) {
        if (merchantId.isBlank()) {
            throw new IllegalArgumentException("Merchant ID must not be blank");
        }
        this.card = card;
        this.money = money;
        this.merchantId = merchantId;
    }
}
