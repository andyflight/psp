package com.example.psp.repository.transaction;

import com.example.psp.domain.entities.Transaction;
import reactor.core.publisher.Mono;

/**
 * Repository interface for managing transactions.
 */
public interface TransactionRepository {

    /**
     * Saves a new transaction to the repository.
     *
     * @param transaction The transaction to be saved.
     * @return A Mono emitting the saved transaction.
     */
    Mono<Transaction> save(Transaction transaction);

    /**
     * Updates an existing transaction in the repository.
     *
     * @param transaction The transaction to be updated.
     * @return A Mono emitting the updated transaction.
     */
    Mono<Transaction> update(Transaction transaction);
}
