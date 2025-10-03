package com.example.psp.api.dto.transaction;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

/**
 * DTO representing the response after processing a transaction.
 */
@Jacksonized
@Builder
@Value
@Schema(description = "Transaction processing response")
public class TransactionResponseDto {

    @Schema(
            description = "Unique transaction identifier",
            example = "550e8400-e29b-41d4-a716-446655440000"
    )
    String transactionId;

    @Schema(
            description = "Current status of the transaction",
            example = "APPROVED",
            allowableValues = {"APPROVED", "DENIED", "PENDING"}
    )
    String transactionStatus;

    @Schema(
            description = "Additional information about the transaction result",
            example = "Transaction for merchant MERCHANT_001 is APPROVED"
    )
    String message;
}
