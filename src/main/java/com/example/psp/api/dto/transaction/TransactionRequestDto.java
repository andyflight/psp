package com.example.psp.api.dto.transaction;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import org.hibernate.validator.constraints.CreditCardNumber;
import org.hibernate.validator.constraints.Currency;

/**
 * DTO representing a transaction request.
 */
@Jacksonized
@Builder
@Value
@Schema(description = "Transaction request containing payment details")
public class TransactionRequestDto {

    @Schema(
            description = "Card number (16 digits)",
            example = "4532015112830366"
    )
    @NotBlank(message = "Card number must not be blank")
    @Pattern(regexp = "\\d{16}", message = "Card number must be 16 digits")
    @CreditCardNumber
    String cardNumber;

    @Schema(
            description = "Card expiry date in MM/yy format",
            example = "12/25"
    )
    @NotBlank(message = "Card holder name must not be blank")
    @Pattern(regexp = "(0[1-9]|1[0-2])/\\d{2}", message = "Expiry must be in MM/yy form")
    String expiry;

    @Schema(
            description = "Card verification value (3 digits)",
            example = "123"
    )
    @NotBlank
    @Pattern(regexp = "\\d{3}", message = "CVV must be 3 digits")
    String cvv;

    @Schema(
            description = "Transaction amount",
            example = "99.99",
            minimum = "0.01",
            maximum = "999999.99"
    )
    @NotNull(message = "Amount must not be null")
    @Positive(message = "Amount must be positive")
    @DecimalMin(value = "0.01", message = "Amount must be at least 0.01")
    @DecimalMax(value = "999999.99", message = "Amount must not exceed 999999.99")
    @Digits(integer = 6, fraction = 2, message = "Amount must have at most 6 integer digits and 2 decimal places")
    Double amount;

    @Schema(
            description = "ISO 4217 currency code (3 uppercase letters)",
            example = "USD"
    )
    @NotBlank(message = "Currency code must not be blank")
    @Pattern(regexp = "[A-Z]{3}", message = "Currency code must be 3 uppercase letters (ISO 4217)")
    String currencyCode;

    @Schema(
            description = "Unique merchant identifier",
            example = "MERCHANT_001",
            minLength = 3,
            maxLength = 100
    )
    @NotBlank(message = "Merchant ID must not be blank")
    @Size(min = 3, max = 100, message = "Merchant ID must be between 3 and 100 characters")
    String merchantId;

}
