package com.example.psp.service.transaction.ports;

import com.example.psp.domain.enums.TransactionStatus;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.util.UUID;

/**
 * Represents the response after processing a payment transaction.
 */
@Value
@Builder
public class PaymentResponse {

    @NonNull UUID transactionId;

    @NonNull TransactionStatus transactionStatus;

    @NonNull String message;

}
