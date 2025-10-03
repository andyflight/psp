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


/**
 * Represents a payment transaction in the system.
 * <p>
 * This entity encapsulates all relevant information about a transaction,
 * including card details, amount, status, acquirer information, and timestamps.
 * <p>
 * The transaction lifecycle is managed through status updates based on acquirer decisions.
 */
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

    /**
     * Initializes a new Transaction with the provided details.
     *
     * @param card       The card details used for the transaction.
     * @param money      The monetary amount of the transaction.
     * @param merchantId The identifier of the merchant initiating the transaction.
     * @param now        The current timestamp for creation and update fields.
     * @return A new Transaction instance with status set to PENDING.
     * @throws NullPointerException if any of the parameters are null.
     */
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

    /**
     * Initializes a new Transaction with the provided details, using the provided Clock to obtain the current time.
     *
     * @param card       The card details used for the transaction.
     * @param money      The monetary amount of the transaction.
     * @param merchantId The identifier of the merchant initiating the transaction.
     * @param clock      The clock to obtain the current time.
     * @return A new Transaction instance with status set to PENDING.
     * @throws NullPointerException if any of the parameters are null.
     * @see #initialize(CardDetails, Money, String, Instant)
     */
    public static Transaction initialize(@NonNull CardDetails card,
                                         @NonNull Money money,
                                         @NonNull String merchantId,
                                         @NonNull Clock clock
    ) {
        Instant now = Instant.now(clock);
        return initialize(card, money, merchantId, now);
    }


    /**
     * Updates the transaction status based on the acquirer's decision.
     *
     * @param decision The decision made by the acquirer (APPROVED or DENIED).
     * @param acquirer The type of acquirer that processed the transaction.
     * @param now      The current timestamp for the update field.
     * @throws IllegalStateException if the current status is not PENDING.
     * @throws NullPointerException  if any of the parameters are null.
     */
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

    /**
     * Updates the transaction status based on the acquirer's decision, using the provided Clock to obtain the current time.
     *
     * @param decision The decision made by the acquirer (APPROVED or DENIED).
     * @param acquirer The type of acquirer that processed the transaction.
     * @param clock    The clock to obtain the current time.
     * @throws IllegalStateException if the current status is not PENDING.
     * @throws NullPointerException  if any of the parameters are null.
     * @see #updateStatus(AcquirerDecision, AcquirerType, Instant)
     */
    public void updateStatus(@NonNull AcquirerDecision decision,
                             @NonNull AcquirerType acquirer,
                             @NonNull Clock clock
    ) {
        Instant now = Instant.now(clock);

        this.updateStatus(decision, acquirer, now);
    }
}
