package com.example.psp.service.transaction.impl;

import com.example.psp.domain.entities.Transaction;
import com.example.psp.repository.transaction.TransactionRepository;
import com.example.psp.service.acquirer.ports.AcquirerRouter;
import com.example.psp.service.transaction.ports.PaymentRequest;
import com.example.psp.service.transaction.ports.PaymentResponse;
import com.example.psp.service.transaction.ports.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuples;

import java.time.Clock;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final Clock clock;
    private final TransactionRepository repository;
    private final AcquirerRouter router;

    @Override
    public Mono<PaymentResponse> processPayment(PaymentRequest paymentDetails) {

        return Mono.defer(() -> {
            Transaction transaction = Transaction.initialize(
                    paymentDetails.getCard(),
                    paymentDetails.getMoney(),
                    paymentDetails.getMerchantId(),
                    clock
            );

            return repository.save(transaction)
                    .flatMap(savedTnx -> router.getAcquirer(paymentDetails.getCard())
                            .map(acquirer -> Tuples.of(savedTnx, acquirer)))
                    .flatMap(tuple -> {
                        Transaction tnx = tuple.getT1();
                        var acquirer = tuple.getT2();
                        return acquirer.authorizeTransaction(paymentDetails.getCard(), paymentDetails.getMoney())
                                .map(decision -> Tuples.of(tnx, acquirer, decision));
                    })
                    .flatMap(tuple -> {
                        Transaction tnx = tuple.getT1();
                        var acquirer = tuple.getT2();
                        var decision = tuple.getT3();
                        tnx.updateStatus(decision, acquirer.getType(), clock);
                        return repository.update(tnx);
                    })
                    .map(tnx -> PaymentResponse.builder()
                            .transactionId(tnx.getId())
                            .transactionStatus(tnx.getStatus())
                            .message("Transaction for merchant " + tnx.getMerchantId() + " is " + tnx.getStatus())
                            .build()
                    );
        });
    }
}
