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
@Table(name = "TICKET_BALANCE")
@SequenceGenerator(
        name = "SEQ_TICKET_BALANCE_GENERATOR",
        sequenceName = "SEQ_TICKET_BALANCE",
        allocationSize = 1
)
public class TicketBalance extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_TICKET_BALANCE_GENERATOR")
    @Column(name = "BALANCE_ID")
    private Long balanceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID", nullable = false,
            foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Member member;

    @Column(name = "BALANCE", nullable = false)
    private int balance;

    @Builder
    public TicketBalance(Member member, int balance) {
        this.member = member;
        this.balance = balance;
    }

    public void addBalance(int amount) {
        this.balance += amount;
    }

    public void subtractBalance(int amount) {
        this.balance -= amount;
    }
}
