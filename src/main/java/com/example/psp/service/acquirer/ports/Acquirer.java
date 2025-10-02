package com.example.psp.service.acquirer.ports;

import com.example.psp.domain.enums.AcquirerDecision;
import com.example.psp.domain.enums.AcquirerType;
import com.example.psp.domain.valueobjects.CardDetails;
import com.example.psp.domain.valueobjects.Money;
import reactor.core.publisher.Mono;

public interface Acquirer {

    Mono<AcquirerDecision> authorizeTransaction(CardDetails cardDetails, Money money);

    AcquirerType getType();

}
