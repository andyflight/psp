package com.example.psp.domain.entities;

import com.example.psp.domain.enums.AcquirerDecision;
import com.example.psp.domain.enums.AcquirerType;
import com.example.psp.domain.enums.TransactionStatus;
import com.example.psp.domain.valueobjects.CardDetails;
import com.example.psp.domain.valueobjects.StoredCardInfo;
import com.example.psp.domain.valueobjects.Money;
import lombok.*;

import java.time.Clock;
import java.time.Instant;
import java.util.UUID;

@Getter
@EqualsAndHashCode(of = "id")
public class Transaction {

    private final UUID id;

    private final StoredCardInfo card;

    private final Money money;

    private TransactionStatus status;

    // Simple representation of the acquirer used for this transaction
    private AcquirerType acquirerType;

    private final String merchantId;

    private final Instant createdAt;

    private Instant updatedAt;

    private Transaction(UUID id,
                        StoredCardInfo card,
                        Money money,
                        String merchantId,
                        TransactionStatus status,
                        Instant createdAt,
                        Instant updatedAt
                        ) {
        this.id = id;
        this.card = card;
        this.money = money;
        this.merchantId = merchantId;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }


    public static Transaction initialize(@NonNull CardDetails card,
                                         @NonNull Money money,
                                         @NonNull String merchantId,
                                         @NonNull Instant now
    ) {

        return new Transaction(
                UUID.randomUUID(),
                StoredCardInfo.of(card),
                money,
                merchantId,
                TransactionStatus.PENDING,
                now,
                now
        );
    }


    public static Transaction initialize(@NonNull CardDetails card,
                                         @NonNull Money money,
                                         @NonNull String merchantId,
                                         @NonNull Clock clock
    ) {
        Instant now = Instant.now(clock);
        return initialize(card, money, merchantId, now);
    }


    public void updateStatus(@NonNull AcquirerDecision decision,
                             @NonNull AcquirerType acquirer,
                             @NonNull Instant now) {

        if (this.status != TransactionStatus.PENDING) {
            throw new IllegalStateException("Cannot change status from " + this.status + " using an acquirer decision");
        }

        this.status = switch (decision) {
            case APPROVED -> TransactionStatus.APPROVED;
            case DENIED   -> TransactionStatus.DENIED;
        };

        this.acquirerType = acquirer;
        this.updatedAt = now;
    }

    public void updateStatus(@NonNull AcquirerDecision decision,
                             @NonNull AcquirerType acquirer,
                             @NonNull Clock clock
    ) {
        Instant now = Instant.now(clock);

        this.updateStatus(decision, acquirer, now);
    }
}
