package com.example.psp.api.router;

import com.example.psp.api.handler.TransactionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class TransactionRouterFunction {

    @Bean
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
