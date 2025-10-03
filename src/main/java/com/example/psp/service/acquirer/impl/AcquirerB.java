package com.example.psp.service.acquirer.impl;

import com.example.psp.domain.enums.AcquirerDecision;
import com.example.psp.domain.enums.AcquirerType;
import com.example.psp.domain.valueobjects.CardDetails;
import com.example.psp.domain.valueobjects.Money;
import com.example.psp.service.acquirer.ports.Acquirer;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component("acquirerB")
@Slf4j
public class AcquirerB implements Acquirer {

    private final AcquirerType type;

    public AcquirerB() {
        this.type = AcquirerType.ACQUIRER_B;
    }

    /**
     * Authorizes a transaction based on the last digit of the card number.
     * If the last digit is even, the transaction is approved; if odd, it is denied.
     *
     * @param cardDetails The details of the card being used for the transaction.
     * @param money       The amount of money involved in the transaction. (Not used in the decision logic, but included for completeness)
     * @return A Mono emitting the decision of the acquirer (APPROVED or DENIED).
     */
    @Override
    public Mono<AcquirerDecision> authorizeTransaction(@NonNull CardDetails cardDetails, @NonNull Money money) {
        return Mono.fromSupplier(() -> {
                    String lastDigit = cardDetails.getLastDigit();
                    return Integer.parseInt(lastDigit) % 2 == 0 ? AcquirerDecision.APPROVED : AcquirerDecision.DENIED;
                })
                .doOnSuccess(decision -> log.debug("Authorization decision: acquirer={}, decision={}",
                        type, decision));
    }

    @Override
    public AcquirerType getType() {
        return type;
    }
}
