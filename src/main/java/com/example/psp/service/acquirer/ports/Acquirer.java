package com.example.psp.service.acquirer.ports;

import com.example.psp.domain.enums.AcquirerDecision;
import com.example.psp.domain.enums.AcquirerType;
import com.example.psp.domain.valueobjects.CardDetails;
import com.example.psp.domain.valueobjects.Money;
import reactor.core.publisher.Mono;

/**
 * Interface representing an acquirer that can authorize transactions.
 */
public interface Acquirer {

    /**
     * Authorizes a transaction based on card details and the amount of money.
     *
     * @param cardDetails The details of the card used for the transaction.
     * @param money       The amount of money involved in the transaction.
     * @return A Mono emitting the decision made by the acquirer (APPROVED or DENIED).
     */
    Mono<AcquirerDecision> authorizeTransaction(CardDetails cardDetails, Money money);

    /**
     * Gets the type of the acquirer.
     *
     * @return The type of the acquirer.
     */
    AcquirerType getType();

}
