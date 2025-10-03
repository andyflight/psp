package com.example.psp.api.dto.transaction;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import org.hibernate.validator.constraints.CreditCardNumber;
import org.hibernate.validator.constraints.Currency;

@Jacksonized
@Builder()
@Value
public class TransactionRequestDto {

    @NotBlank(message = "Card number must not be blank")
    @Pattern(regexp = "\\d{16}", message = "Card number must be 16 digits")
    @CreditCardNumber
    String cardNumber;

    @NotBlank(message = "Card holder name must not be blank")
    @Pattern(regexp = "(0[1-9]|1[0-2])/\\d{2}", message = "Expiry must be in MM/yy form")
    String expiry;

    @NotBlank
    @Pattern(regexp = "\\d{3}", message = "CVV must be 3 digits")
    String cvv;

    @NotNull(message = "Amount must not be null")
    @Positive(message = "Amount must be positive")
    @DecimalMin(value = "0.01", message = "Amount must be at least 0.01")
    @DecimalMax(value = "999999.99", message = "Amount must not exceed 999999.99")
    @Digits(integer = 6, fraction = 2, message = "Amount must have at most 6 integer digits and 2 decimal places")
    Double amount;

    @NotBlank(message = "Currency code must not be blank")
    @Pattern(regexp = "[A-Z]{3}", message = "Currency code must be 3 uppercase letters (ISO 4217)")
    String currencyCode;

    @NotBlank(message = "Merchant ID must not be blank")
    @Size(min = 3, max = 100, message = "Merchant ID must be between 3 and 100 characters")
    String merchantId;

}
