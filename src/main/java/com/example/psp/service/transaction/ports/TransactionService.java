package com.example.psp.service.transaction.ports;

import reactor.core.publisher.Mono;

/**
 * Interface representing a service for processing transactions.
 */
public interface TransactionService {

    /**
     * Processes a payment based on the provided payment details.
     *
     * @param paymentDetails The details of the payment to be processed.
     * @return A Mono emitting the response of the payment processing.
     */
    Mono<PaymentResponse> processPayment(PaymentRequest paymentDetails);
}
