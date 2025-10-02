package com.example.psp.domain.valueobjects;

import com.example.psp.shared.CardValidation;
import lombok.*;

import java.time.YearMonth;

/**
 * Value object representing stored card information with masked card number and expiry date.
 * This class is immutable and ensures that sensitive card details are not exposed.
 */
@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class StoredCardInfo {

    @NonNull String cardNumberMasked;
    @NonNull YearMonth expiryDate;

    /**
     * Factory method to create a StoredCardInfo instance from CardDetails.
     * The card number is masked to show only the last 4 digits.
     *
     * @param cardDetails the CardDetails instance
     * @return a StoredCardInfo instance with masked card number
     * @throws NullPointerException if cardDetails is null
     */
    public static StoredCardInfo of(@NonNull CardDetails cardDetails) {

        // All validation is done in CardDetails and should not be repeated here

        String maskedNumber = "****-****-****-" + cardDetails.getCardNumber().substring(cardDetails.getCardNumber().length() - 4);
        return new StoredCardInfo(maskedNumber, cardDetails.getExpiryDate());
    }

}
