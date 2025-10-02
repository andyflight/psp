package com.example.psp.service.transaction.ports;

import com.example.psp.domain.valueobjects.CardDetails;
import com.example.psp.domain.valueobjects.Money;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder
public class PaymentRequest {
    @NonNull CardDetails card;
    @NonNull Money money;
    @NonNull String merchantId;

    private PaymentRequest(CardDetails card, Money money, String merchantId) {
        if (merchantId.isBlank()) {
            throw new IllegalArgumentException("Merchant ID must not be blank");
        }
        this.card = card;
        this.money = money;
        this.merchantId = merchantId;
    }
}
