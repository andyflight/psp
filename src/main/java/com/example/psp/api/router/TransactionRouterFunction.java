package com.example.psp.api.router;

import com.example.psp.api.dto.transaction.TransactionRequestDto;
import com.example.psp.api.dto.transaction.TransactionResponseDto;
import com.example.psp.api.handler.TransactionHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

/**
 * Router function for handling transaction-related API endpoints.
 */
@Configuration
public class TransactionRouterFunction {

    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = "/api/v1/transactions",
                    method = RequestMethod.POST,
                    beanClass = TransactionHandler.class,
                    beanMethod = "processTransaction",
                    operation = @Operation(
                            operationId = "processTransaction",
                            summary = "Process a payment transaction",
                            description = "Processes a payment transaction with the provided card details and amount",
                            tags = {"Transactions"},
                            requestBody = @RequestBody(
                                    description = "Transaction request details",
                                    required = true,
                                    content = @Content(
                                            mediaType = "application/json",
                                            schema = @Schema(implementation = TransactionRequestDto.class),
                                            examples = {
                                                    @ExampleObject(
                                                            name = "Valid Transaction",
                                                            summary = "A valid transaction example",
                                                            description = "Example of a valid payment transaction",
                                                            value = """
                                                                   {
                                                                      "cardNumber": "4532015112830366",
                                                                      "expiry": "12/25",
                                                                      "cvv": "123",
                                                                      "amount": 99.99,
                                                                      "currencyCode": "USD",
                                                                      "merchantId": "MERCHANT_001"
                                                                    }
                                                                   """

                                                    )
                                            }
                                    )
                            ),
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200",
                                            description = "Transaction processed successfully",
                                            content = @Content(
                                                    mediaType = "application/json",
                                                    schema = @Schema(implementation = TransactionResponseDto.class)
                                            )
                                    ),
                                    @ApiResponse(
                                            responseCode = "400",
                                            description = "Invalid request - validation failed",
                                            content = @Content(
                                                    mediaType = "application/json",
                                                    examples = {
                                                            @ExampleObject(
                                                                    name = "Bad Request Response",
                                                                    summary = "Response for request with invalid card number",
                                                                    description = "The card number provided does not pass validation",
                                                                    value = """
                                                                            {
                                                                                "type": "/api/v1/transactions",
                                                                                "status": 400,
                                                                                "title": "ValidationException",
                                                                                "description": "cardNumber: invalid credit card number, cardNumber: Card number must be 16 digits"
                                                                            }
                                                                            """
                                                            )
                                                        }
                                            )
                                    )
                            }
                    )
            )
    })
    public RouterFunction<ServerResponse> transactionRouter(TransactionHandler handler) {
        return RouterFunctions.route()
                .path("/api/v1", builder -> builder
                        .POST("/transactions", RequestPredicates.accept(MediaType.APPLICATION_JSON)
                                .and(RequestPredicates.contentType(MediaType.APPLICATION_JSON)),
                                handler::processTransaction)
                )
                .build();
    }
}
