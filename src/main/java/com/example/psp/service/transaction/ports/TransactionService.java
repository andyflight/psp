package com.example.psp.service.transaction.ports;

import reactor.core.publisher.Mono;

public interface TransactionService {

    Mono<PaymentResponse> processPayment(PaymentRequest paymentDetails);
}
