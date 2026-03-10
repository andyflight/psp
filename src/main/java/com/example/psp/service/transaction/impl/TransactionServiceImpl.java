package com.example.psp.service.transaction.impl;

import com.example.psp.domain.entities.Transaction;
import com.example.psp.domain.enums.AcquirerDecision;
import com.example.psp.repository.transaction.TransactionRepository;
import com.example.psp.service.acquirer.ports.Acquirer;
import com.example.psp.service.acquirer.ports.AcquirerRouter;
import com.example.psp.service.transaction.context.TransactionContext;
import com.example.psp.service.transaction.ports.PaymentRequest;
import com.example.psp.service.transaction.ports.PaymentResponse;
import com.example.psp.service.transaction.ports.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuple3;
import reactor.util.function.Tuples;

import java.time.Clock;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionServiceImpl implements TransactionService {

    private final Clock clock;
    private final TransactionRepository repository;
    private final AcquirerRouter router;

    @Override
    public Mono<PaymentResponse> processPayment(PaymentRequest paymentDetails) {
        return Mono.defer(() -> {
            logPaymentStart(paymentDetails);
            return initializeTransaction(paymentDetails)
                    .flatMap(transaction -> saveAndRouteTransaction(transaction, paymentDetails))
                    .flatMap(context -> authorizeTransaction(context, paymentDetails))
                    .flatMap(this::updateTransactionStatus)
                    .map(this::buildPaymentResponse)
                    .doOnSuccess(resp -> log.info("Payment processed: transactionId={}, status={}", resp.getTransactionId(), resp.getTransactionStatus()))
                    .doOnError(err -> log.error("Error processing payment for merchantId={}: {}", paymentDetails.getMerchantId(), err.getMessage(), err));
        });
    }

    private void logPaymentStart(PaymentRequest paymentDetails) {
        if (log.isInfoEnabled()) {
            var money = paymentDetails.getMoney();
            log.info("Processing payment: merchantId={}, amount={}, currency={}",
                    paymentDetails.getMerchantId(), money.getAmount(), money.getCurrency());
        }
    }

    private Mono<Transaction> initializeTransaction(PaymentRequest paymentDetails) {
        return Mono.fromCallable(() -> Transaction.initialize(
                        paymentDetails.getCard(),
                        paymentDetails.getMoney(),
                        paymentDetails.getMerchantId(),
                        clock
                ))
                .doOnNext(tnx -> log.debug("Initialized transaction: id={}, status={}", tnx.getId(), tnx.getStatus()));
    }

    private Mono<TransactionContext> saveAndRouteTransaction(Transaction transaction, PaymentRequest paymentDetails) {
        return repository.save(transaction)
                .doOnNext(saved -> log.info("Transaction persisted: id={}", saved.getId()))
                .flatMap(savedTnx -> router.getAcquirer(paymentDetails.getCard())
                        .map(acquirer -> new TransactionContext(savedTnx, acquirer, null)))
                .doOnNext(ctx -> log.info("Acquirer selected for transaction {}: {}", ctx.transaction().getId(), ctx.acquirer().getType()));
    }

    private Mono<TransactionContext> authorizeTransaction(TransactionContext context, PaymentRequest paymentDetails) {
        return context.acquirer().authorizeTransaction(paymentDetails.getCard(), paymentDetails.getMoney())
                .map(decision -> new TransactionContext(context.transaction(), context.acquirer(), decision))
                .doOnNext(ctx -> log.info("Acquirer decision for transaction {}: {}", ctx.transaction().getId(), ctx.decision()));
    }

    private Mono<Transaction> updateTransactionStatus(TransactionContext context) {
        var tnx = context.transaction();
        tnx.updateStatus(context.decision(), context.acquirer().getType(), clock);
        log.info("Transaction status updated: id={}, status={}, acquirer={}", tnx.getId(), tnx.getStatus(), context.acquirer().getType());
        return repository.update(tnx);
    }

    private PaymentResponse buildPaymentResponse(Transaction tnx) {
        return PaymentResponse.builder()
                .transactionId(tnx.getId())
                .transactionStatus(tnx.getStatus())
                .message("Transaction for merchant " + tnx.getMerchantId() + " is " + tnx.getStatus())
                .build();
    }
}