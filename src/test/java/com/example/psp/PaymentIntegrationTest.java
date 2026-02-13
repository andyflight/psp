package com.example.psp;

import com.example.psp.domain.enums.TransactionStatus;
import com.example.psp.domain.valueobjects.CardDetails;
import com.example.psp.domain.valueobjects.Money;
import com.example.psp.repository.transaction.impl.TransactionR2dbcRepository;
import com.example.psp.service.transaction.ports.PaymentRequest;
import com.example.psp.service.transaction.ports.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.YearMonth;
import java.util.Currency;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class PaymentIntegrationTest extends AbstractIT {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private TransactionR2dbcRepository r2dbcRepository;

    @Autowired
    private Clock clock;

    @BeforeEach
    void setUp() {
        r2dbcRepository.deleteAll().block();
    }

    @Test
    void shouldProcessSuccessfulPayment() {
        // BIN: 424242 -> Sum: 18 (Even) -> Acquirer A
        // Last digit: 2 (Even) -> APPROVED
        CardDetails card = CardDetails.builder()
                .cardNumber("4242424242424242")
                .cvv("123")
                .expiryDate(YearMonth.now(clock).plusMonths(1))
                .clock(clock)
                .build();

        PaymentRequest request = PaymentRequest.builder()
                .card(card)
                .money(Money.builder().amount(new BigDecimal("100.00")).currency(Currency.getInstance("USD")).build())
                .merchantId("merchant-1")
                .build();

        transactionService.processPayment(request)
                .as(StepVerifier::create)
                .assertNext(response -> {
                    assertNotNull(response.getTransactionId());
                    assertEquals(TransactionStatus.APPROVED, response.getTransactionStatus());
                })
                .verifyComplete();

        r2dbcRepository.findAll()
                .as(StepVerifier::create)
                .assertNext(dbModel -> {
                    assertEquals(TransactionStatus.APPROVED.name(), dbModel.getStatus());
                    assertEquals("ACQUIRER_A", dbModel.getAcquirerType());
                })
                .verifyComplete();
    }

    @Test
    void shouldProcessDeniedPayment() {
        // BIN: 424242 -> Sum: 18 (Even) -> Acquirer A
        // Last digit: 9 (Odd) -> DENIED
        // Valid Luhn check digit for 424242424242425 is 9
        CardDetails card = CardDetails.builder()
                .cardNumber("4242424242424259")
                .cvv("123")
                .expiryDate(YearMonth.now(clock).plusMonths(1))
                .clock(clock)
                .build();

        PaymentRequest request = PaymentRequest.builder()
                .card(card)
                .money(Money.builder().amount(new BigDecimal("50.00")).currency(Currency.getInstance("USD")).build())
                .merchantId("merchant-2")
                .build();

        transactionService.processPayment(request)
                .as(StepVerifier::create)
                .assertNext(response -> {
                    assertEquals(TransactionStatus.DENIED, response.getTransactionStatus());
                })
                .verifyComplete();

        r2dbcRepository.findAll()
                .as(StepVerifier::create)
                .assertNext(dbModel -> {
                    assertEquals(TransactionStatus.DENIED.name(), dbModel.getStatus());
                })
                .verifyComplete();
    }

    @Test
    void shouldRouteToAcquirerBAndApprove() {
        // BIN: 444441 -> Sum: 21 (Odd) -> Acquirer B
        // Last digit: 8 (Even) -> APPROVED
        // Valid Luhn check digit for 444441444444445 is 8
        CardDetails card = CardDetails.builder()
                .cardNumber("4444414444444458")
                .cvv("123")
                .expiryDate(YearMonth.now(clock).plusMonths(1))
                .clock(clock)
                .build();

        PaymentRequest request = PaymentRequest.builder()
                .card(card)
                .money(Money.builder().amount(new BigDecimal("75.00")).currency(Currency.getInstance("EUR")).build())
                .merchantId("merchant-3")
                .build();

        transactionService.processPayment(request)
                .as(StepVerifier::create)
                .assertNext(response -> {
                    assertEquals(TransactionStatus.APPROVED, response.getTransactionStatus());
                })
                .verifyComplete();

        r2dbcRepository.findAll()
                .as(StepVerifier::create)
                .assertNext(dbModel -> {
                    assertEquals(TransactionStatus.APPROVED.name(), dbModel.getStatus());
                    assertEquals("ACQUIRER_B", dbModel.getAcquirerType());
                })
                .verifyComplete();
    }
}
