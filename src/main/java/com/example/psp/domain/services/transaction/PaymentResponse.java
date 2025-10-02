package com.example.psp.domain.services.transaction;

import com.example.psp.domain.enums.TransactionStatus;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.util.UUID;

@Value
@Builder
public class PaymentResponse {

    @NonNull UUID transactionId;

    @NonNull TransactionStatus transactionStatus;

    @NonNull String message;

}
