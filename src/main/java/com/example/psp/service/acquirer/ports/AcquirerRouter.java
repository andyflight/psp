package com.example.psp.service.acquirer.ports;

import com.example.psp.domain.valueobjects.CardDetails;
import reactor.core.publisher.Mono;

/**
 * Interface representing a router that selects an appropriate acquirer based on card details.
 */
public interface AcquirerRouter {

    /**
     * Gets an acquirer based on the provided card details.
     *
     * @param cardDetails The details of the card used for the transaction.
     * @return A Mono emitting the selected acquirer.
     */
    Mono<Acquirer> getAcquirer(CardDetails cardDetails);
}
