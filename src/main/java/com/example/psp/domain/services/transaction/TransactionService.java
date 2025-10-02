package com.example.psp.domain.services.transaction;

import com.example.psp.domain.entities.Transaction;

public interface TransactionService {

    PaymentResponse processPayment(PaymentRequest paymentDetails);
}
