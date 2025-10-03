package com.example.psp.repository.transaction.impl;

import com.example.psp.domain.entities.Transaction;
import com.example.psp.repository.transaction.TransactionRepository;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory implementation of the TransactionRepository.
 * This implementation uses a ConcurrentHashMap to store transactions.
 */
@Repository
@Slf4j
public class InMemoryTransactionRepositoryImpl implements TransactionRepository {

    private final Map<UUID, Transaction> storage = new ConcurrentHashMap<>();

    @Override
    public Mono<Transaction> save(@NonNull Transaction transaction) {
        return Mono.fromRunnable(() -> storage.put(transaction.getId(), transaction))
                .thenReturn(transaction)
                .doOnSuccess(t -> log.debug("Saved transaction: id={}, status={}", t.getId(), t.getStatus()));
    }

    @Override
    public Mono<Transaction> update(Transaction transaction) {
        return Mono.fromSupplier(() -> {
            if (!storage.containsKey(transaction.getId())) {
                throw new IllegalStateException("Transaction not found");
            }
            storage.put(transaction.getId(), transaction);
            return transaction;
        }).doOnSuccess(t -> log.debug("Updated transaction: id={}, status={}", t.getId(), t.getStatus()));
    }
}
