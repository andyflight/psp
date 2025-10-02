package com.example.psp.domain.repositories.transaction;

import com.example.psp.domain.entities.Transaction;

public interface TransactionRepository {

    void save(Transaction transaction);
    void update(Transaction transaction);
}
