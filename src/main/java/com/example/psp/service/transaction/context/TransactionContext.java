package com.example.psp.service.transaction.context;

import com.example.psp.domain.entities.Transaction;
import com.example.psp.domain.enums.AcquirerDecision;
import com.example.psp.service.acquirer.ports.Acquirer;

public record TransactionContext(Transaction transaction, Acquirer acquirer, AcquirerDecision decision) {}
