package com.example.psp.service.acquirer.ports;

import com.example.psp.domain.valueobjects.CardDetails;
import reactor.core.publisher.Mono;

public interface AcquirerRouter {

    Mono<Acquirer> getAcquirer(CardDetails cardDetails);
}
