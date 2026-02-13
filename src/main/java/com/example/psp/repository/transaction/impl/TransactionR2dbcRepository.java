package com.example.psp.repository.transaction.impl;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface TransactionR2dbcRepository extends ReactiveCrudRepository<TransactionDBModel, UUID> {
}
