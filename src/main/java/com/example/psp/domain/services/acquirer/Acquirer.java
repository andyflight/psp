package com.example.psp.domain.services.acquirer;

import com.example.psp.domain.enums.AcquirerDecision;
import com.example.psp.domain.enums.AcquirerType;
import com.example.psp.domain.valueobjects.CardDetails;
import com.example.psp.domain.valueobjects.Money;

public interface Acquirer {

    AcquirerDecision authorizeTransaction(CardDetails cardDetails, Money money);

    AcquirerType getType();

}
