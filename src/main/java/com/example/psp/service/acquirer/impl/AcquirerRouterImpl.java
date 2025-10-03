package com.example.psp.service.acquirer.impl;

import com.example.psp.domain.enums.AcquirerType;
import com.example.psp.domain.valueobjects.CardDetails;
import com.example.psp.service.acquirer.ports.Acquirer;
import com.example.psp.service.acquirer.ports.AcquirerRouter;
import lombok.NonNull;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class AcquirerRouterImpl implements AcquirerRouter {

    private final Map<AcquirerType, Acquirer> acquirersCache = new ConcurrentHashMap<>();

    public AcquirerRouterImpl(List<Acquirer> acquirers) {
        acquirers.forEach(acquirer -> acquirersCache.put(acquirer.getType(), acquirer));
    }

    @Override
    public Mono<Acquirer> getAcquirer(@NonNull CardDetails cardDetails) {
        return Mono.fromSupplier(() -> {
            String bin = cardDetails.getBIN();
            AcquirerType type = calculateBinSum(bin) % 2 == 0 ? AcquirerType.ACQUIRER_A : AcquirerType.ACQUIRER_B;
            Acquirer acquirer =  acquirersCache.get(type);
            if (acquirer == null) {
                throw new IllegalStateException("No acquirer found");
            }
            return acquirer;
        });
    }

    private int calculateBinSum(String bin) {
        return bin.chars().map(Character::getNumericValue).sum();
    }

}
