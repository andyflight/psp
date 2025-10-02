package com.example.psp.domain.services.acquirer;

import com.example.psp.domain.valueobjects.CardDetails;

public interface AcquirerRouter {

    Acquirer getAcquirer(CardDetails cardDetails);
}
