package com.example.psp.domain.valueobjects;

import com.example.psp.shared.CardValidation;
import lombok.*;

import java.time.YearMonth;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class StoredCardInfo {

    @NonNull String cardNumberMasked;
    @NonNull YearMonth expiryDate;

    public static StoredCardInfo of(@NonNull CardDetails cardDetails) {

        // All validation is done in CardDetails and should not be repeated here

        String maskedNumber = "****-****-****-" + cardDetails.getCardNumber().substring(cardDetails.getCardNumber().length() - 4);
        return new StoredCardInfo(maskedNumber, cardDetails.getExpiryDate());
    }

}
