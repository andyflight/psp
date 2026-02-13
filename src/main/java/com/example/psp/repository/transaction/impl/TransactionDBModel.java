package com.example.psp.repository.transaction.impl;

import com.example.psp.domain.entities.Transaction;
import com.example.psp.domain.enums.AcquirerType;
import com.example.psp.domain.enums.TransactionStatus;
import com.example.psp.domain.valueobjects.Money;
import com.example.psp.domain.valueobjects.StoredCardInfo;
import com.example.psp.repository.common.DBModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.YearMonth;
import java.util.Currency;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("transaction")
public class TransactionDBModel implements DBModel, Persistable<UUID> {

    @Id
    private UUID id;

    private String cardNumberMasked;

    private String cardExpiryDate;

    private BigDecimal amount;

    private String currency;

    private String status;

    private String acquirerType;

    private String merchantId;

    private Instant createdAt;

    private Instant updatedAt;

    @Transient
    private boolean isNew;

    @Override
    public boolean isNew() {
        return isNew;
    }

    public static TransactionDBModel fromDomain(Transaction transaction, boolean isNew) {
        return TransactionDBModel.builder()
                .id(transaction.getId())
                .cardNumberMasked(transaction.getCard().getCardNumberMasked())
                .cardExpiryDate(transaction.getCard().getExpiryDate().toString())
                .amount(transaction.getMoney().getAmount())
                .currency(transaction.getMoney().getCurrency().getCurrencyCode())
                .status(transaction.getStatus().name())
                .acquirerType(transaction.getAcquirerType() != null ? transaction.getAcquirerType().name() : null)
                .merchantId(transaction.getMerchantId())
                .createdAt(transaction.getCreatedAt())
                .updatedAt(transaction.getUpdatedAt())
                .isNew(isNew)
                .build();
    }

    public Transaction toDomain() {
        return Transaction.reconstruct(
                this.id,
                new StoredCardInfo(this.cardNumberMasked, YearMonth.parse(this.cardExpiryDate)),
                Money.builder()
                        .amount(this.amount)
                        .currency(Currency.getInstance(this.currency))
                        .build(),
                this.merchantId,
                TransactionStatus.valueOf(this.status),
                this.acquirerType != null ? AcquirerType.valueOf(this.acquirerType) : null,
                this.createdAt,
                this.updatedAt
        );
    }
}
