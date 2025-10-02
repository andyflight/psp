package com.example.psp.repository.transaction;

import com.example.psp.domain.entities.Transaction;
import reactor.core.publisher.Mono;

public interface TransactionRepository {

    Mono<Transaction> save(Transaction transaction);
    Mono<Transaction> update(Transaction transaction);
}
