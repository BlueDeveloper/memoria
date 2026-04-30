package com.brp.memoria.domain.shop.entity;

import com.brp.memoria.domain.member.entity.Member;
import com.brp.memoria.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "USER_ITEM")
@SequenceGenerator(
        name = "SEQ_USER_ITEM_GENERATOR",
        sequenceName = "SEQ_USER_ITEM",
        allocationSize = 1
)
public class UserItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_USER_ITEM_GENERATOR")
    @Column(name = "USER_ITEM_ID")
    private Long userItemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID", nullable = false,
            foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ITEM_ID", nullable = false,
            foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private ShopItem shopItem;

    @Column(name = "PURCHASED_AT", nullable = false)
    private LocalDateTime purchasedAt;

    @Builder
    public UserItem(Member member, ShopItem shopItem) {
        this.member = member;
        this.shopItem = shopItem;
        this.purchasedAt = LocalDateTime.now();
    }
}
