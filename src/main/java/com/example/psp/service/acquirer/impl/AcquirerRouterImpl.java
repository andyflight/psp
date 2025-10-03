package com.example.psp.service.acquirer.impl;

import com.example.psp.domain.enums.AcquirerType;
import com.example.psp.domain.valueobjects.CardDetails;
import com.example.psp.service.acquirer.ports.Acquirer;
import com.example.psp.service.acquirer.ports.AcquirerRouter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class AcquirerRouterImpl implements AcquirerRouter {

    private final Map<AcquirerType, Acquirer> acquirersCache = new ConcurrentHashMap<>();

    public AcquirerRouterImpl(List<Acquirer> acquirers) {
        acquirers.forEach(acquirer -> acquirersCache.put(acquirer.getType(), acquirer));
    }

    /**
     * Selects an acquirer based on the sum of the digits in the card's BIN.
     * If the sum is even, Acquirer A is selected; if odd, Acquirer B is selected.
     *
     * @param cardDetails The details of the card being used for the transaction.
     * @return A Mono emitting the selected Acquirer.
     */
    @Override
    public Mono<Acquirer> getAcquirer(@NonNull CardDetails cardDetails) {
        return Mono.fromSupplier(() -> {
            String bin = cardDetails.getBIN();
            AcquirerType type = calculateBinSum(bin) % 2 == 0 ? AcquirerType.ACQUIRER_A : AcquirerType.ACQUIRER_B;
            Acquirer acquirer =  acquirersCache.get(type);
            if (acquirer == null) {
                throw new IllegalStateException("No acquirer found");
            }
            if (log.isDebugEnabled()) {
                log.debug("Router selected acquirer: {}", type);
            }
            return acquirer;
        });
    }

    private int calculateBinSum(String bin) {
        return bin.chars().map(Character::getNumericValue).sum();
    }

}
