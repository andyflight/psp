package com.example.psp.repository.transaction.impl;

import com.example.psp.domain.entities.Transaction;
import com.example.psp.repository.transaction.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Service
@Primary
@RequiredArgsConstructor
public class R2dbcTransactionRepositoryImpl implements TransactionRepository {

    private final TransactionR2dbcRepository r2dbcRepository;

    @Override
    @Transactional
    public Mono<Transaction> save(Transaction transaction) {
        return r2dbcRepository.save(TransactionDBModel.fromDomain(transaction, true))
                .map(TransactionDBModel::toDomain);
    }

    @Override
    @Transactional
    public Mono<Transaction> update(Transaction transaction) {
        return r2dbcRepository.save(TransactionDBModel.fromDomain(transaction, false))
                .map(TransactionDBModel::toDomain);
    }
}
