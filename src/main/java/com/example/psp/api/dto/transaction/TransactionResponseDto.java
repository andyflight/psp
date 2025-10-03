package com.example.psp.api.dto.transaction;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

/**
 * DTO representing the response after processing a transaction.
 */
@Jacksonized
@Builder
@Value
public class TransactionResponseDto {

    String transactionId;

    String transactionStatus;

    String message;
}
