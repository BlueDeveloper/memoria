package com.brp.memoria.domain.ticket.entity;

import com.brp.memoria.domain.member.entity.Member;
import com.brp.memoria.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "TICKET_TRANSACTION")
@SequenceGenerator(
        name = "SEQ_TICKET_TRANSACTION_GENERATOR",
        sequenceName = "SEQ_TICKET_TRANSACTION",
        allocationSize = 1
)
public class TicketTransaction extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_TICKET_TRANSACTION_GENERATOR")
    @Column(name = "TRANSACTION_ID")
    private Long transactionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID", nullable = false,
            foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Member member;

    @Enumerated(EnumType.STRING)
    @Column(name = "TYPE", nullable = false, length = 20)
    private TransactionType type;

    @Column(name = "AMOUNT", nullable = false)
    private int amount;

    @Column(name = "BALANCE_AFTER", nullable = false)
    private int balanceAfter;

    @Column(name = "ITEM_TYPE", length = 50)
    private String itemType;

    @Column(name = "ITEM_ID")
    private Long itemId;

    @Column(name = "DESCRIPTION", length = 500)
    private String description;

    @Builder
    public TicketTransaction(Member member, TransactionType type, int amount,
                             int balanceAfter, String itemType, Long itemId, String description) {
        this.member = member;
        this.type = type;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
        this.itemType = itemType;
        this.itemId = itemId;
        this.description = description;
    }

    public enum TransactionType {
        CHARGE, USE, REFUND
    }
}
