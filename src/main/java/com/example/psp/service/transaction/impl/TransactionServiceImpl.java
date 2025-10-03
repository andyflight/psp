package com.example.psp.service.transaction.impl;

import com.example.psp.domain.entities.Transaction;
import com.example.psp.repository.transaction.TransactionRepository;
import com.example.psp.service.acquirer.ports.AcquirerRouter;
import com.example.psp.service.transaction.ports.PaymentRequest;
import com.example.psp.service.transaction.ports.PaymentResponse;
import com.example.psp.service.transaction.ports.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuples;

import java.time.Clock;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionServiceImpl implements TransactionService {

    private final Clock clock;
    private final TransactionRepository repository;
    private final AcquirerRouter router;

    /**
     * Processes a payment request by initializing a transaction, selecting an acquirer,
     * authorizing the transaction, updating its status, and returning a payment response.
     *
     * @param paymentDetails the details of the payment request
     * @return a Mono emitting the payment response
     */
    @Override
    public Mono<PaymentResponse> processPayment(PaymentRequest paymentDetails) {

        return Mono.defer(() -> {
            // Start log without sensitive data
            if (log.isInfoEnabled()) {
                var money = paymentDetails.getMoney();
                log.info("Processing payment: merchantId={}, amount={}, currency={}",
                        paymentDetails.getMerchantId(), money.getAmount(), money.getCurrency());
            }

            return Mono.fromCallable(() -> Transaction.initialize(
                        paymentDetails.getCard(),
                        paymentDetails.getMoney(),
                        paymentDetails.getMerchantId(),
                        clock
                )
            ).doOnNext(tnx ->
                    log.debug("Initialized transaction: id={}, status={}", tnx.getId(), tnx.getStatus())
            )
            .flatMap(transaction -> repository.save(transaction)
                    .doOnNext(saved -> log.info("Transaction persisted: id={}", saved.getId()))
                    .flatMap(savedTnx -> router.getAcquirer(paymentDetails.getCard())
                            .map(acquirer -> Tuples.of(savedTnx, acquirer)))
                    .doOnNext(tuple -> log.info("Acquirer selected for transaction {}: {}", tuple.getT1().getId(), tuple.getT2().getType()))
                    .flatMap(tuple -> {
                        Transaction tnx = tuple.getT1();
                        var acquirer = tuple.getT2();
                        return acquirer.authorizeTransaction(paymentDetails.getCard(), paymentDetails.getMoney())
                                .map(decision -> Tuples.of(tnx, acquirer, decision));
                    })
                    .doOnNext(tuple -> log.info("Acquirer decision for transaction {}: {}", tuple.getT1().getId(), tuple.getT3()))
                    .flatMap(tuple -> {
                        Transaction tnx = tuple.getT1();
                        var acquirer = tuple.getT2();
                        var decision = tuple.getT3();
                        tnx.updateStatus(decision, acquirer.getType(), clock);
                        log.info("Transaction status updated: id={}, status={}, acquirer={}", tnx.getId(), tnx.getStatus(), acquirer.getType());
                        return repository.update(tnx);
                    })
                    .map(tnx -> PaymentResponse.builder()
                            .transactionId(tnx.getId())
                            .transactionStatus(tnx.getStatus())
                            .message("Transaction for merchant " + tnx.getMerchantId() + " is " + tnx.getStatus())
                            .build()
                    ))
                    .doOnSuccess(resp -> log.info("Payment processed: transactionId={}, status={}", resp.getTransactionId(), resp.getTransactionStatus()))
                    .doOnError(err -> log.error("Error processing payment for merchantId={}: {}", paymentDetails.getMerchantId(), err.getMessage(), err));
        });
    }
}
