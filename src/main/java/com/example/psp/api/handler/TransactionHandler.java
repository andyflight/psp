package com.example.psp.api.handler;

import com.example.psp.api.dto.transaction.TransactionRequestDto;
import com.example.psp.api.dto.transaction.TransactionResponseDto;
import com.example.psp.domain.valueobjects.CardDetails;
import com.example.psp.domain.valueobjects.Money;
import com.example.psp.service.transaction.ports.PaymentRequest;
import com.example.psp.service.transaction.ports.PaymentResponse;
import com.example.psp.service.transaction.ports.TransactionService;
import jakarta.validation.ValidationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Currency;

/**
 * Handler for processing transaction requests.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class TransactionHandler {

    private final TransactionService transactionService;
    private final Validator validator;
    private final Clock clock;

    /**
     * Processes a transaction request.
     * Handles POST: /api/v1/transactions
     *
     * @param request the server request containing the transaction details
     * @return a Mono emitting the server response with the transaction result
     */
    public Mono<ServerResponse> processTransaction(ServerRequest request) {
        return request.bodyToMono(TransactionRequestDto.class)
                .doOnNext(dto -> log.info("Received transaction request for merchant ID: {}, with amount {} {}", dto.getMerchantId(), dto.getAmount(), dto.getCurrencyCode()))
                .flatMap(this::validateRequest)
                .map(this::mapToPaymentRequest)
                .flatMap(transactionService::processPayment)
                .doOnNext(response -> log.info("Transaction request processed: {}", response))
                .map(this::mapToTransactionResponseDto)
                .flatMap(responseDto -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(responseDto))
                .doOnSuccess(responseDto -> log.info("Transaction successfully processed with status code: {}", responseDto.statusCode()))
                .doOnError(error -> log.error("Error processing transaction: {}", error.getMessage(), error));
    }

    private TransactionResponseDto mapToTransactionResponseDto(PaymentResponse response) {

        return TransactionResponseDto.builder()
                .transactionId(response.getTransactionId().toString())
                .transactionStatus(response.getTransactionStatus().name())
                .message(response.getMessage())
                .build();

    }

    private PaymentRequest mapToPaymentRequest(TransactionRequestDto dto) {
        return PaymentRequest.builder()
                .card(
                        CardDetails.builder()
                                .cardNumber(dto.getCardNumber())
                                .expiryDate(
                                        YearMonth.parse(
                                                dto.getExpiry(),
                                                DateTimeFormatter.ofPattern("MM/yy")
                                        )
                                )
                                .cvv(dto.getCvv())
                                .clock(clock)
                                .build()
                )
                .merchantId(dto.getMerchantId())
                .money(
                        Money.builder()
                                .amount(BigDecimal.valueOf(dto.getAmount()))
                                .currency(Currency.getInstance(dto.getCurrencyCode()))
                                .build()
                )
                .build();
    }

    private Mono<TransactionRequestDto> validateRequest(TransactionRequestDto dto) {
        var violations = validator.validate(dto);

        if (!violations.isEmpty()) {
            var errorMessage = violations.stream()
                    .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                    .reduce((msg1, msg2) -> msg1 + ", " + msg2)
                    .orElse("Validation error");
            return Mono.error(new ValidationException(errorMessage));
        }

        return Mono.just(dto);
    }
}
