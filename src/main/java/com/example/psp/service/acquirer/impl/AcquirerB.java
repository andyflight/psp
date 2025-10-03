package com.example.psp.service.acquirer.impl;

import com.example.psp.domain.enums.AcquirerDecision;
import com.example.psp.domain.enums.AcquirerType;
import com.example.psp.domain.valueobjects.CardDetails;
import com.example.psp.domain.valueobjects.Money;
import com.example.psp.service.acquirer.ports.Acquirer;
import lombok.NonNull;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component("acquirerB")
public class AcquirerB implements Acquirer {

    private final AcquirerType type;

    public AcquirerB() {
        this.type = AcquirerType.ACQUIRER_B;
    }

    @Override
    public Mono<AcquirerDecision> authorizeTransaction(@NonNull CardDetails cardDetails, @NonNull Money money) {
        return Mono.just(cardDetails)
                .map(details -> {
                    String lastDigit = details.getLastDigit();
                    return Integer.parseInt(lastDigit) % 2 == 0 ? AcquirerDecision.APPROVED : AcquirerDecision.DENIED;
                });
    }

    @Override
    public AcquirerType getType() {
        return type;
    }
}
