package com.brp.memoria.domain.shop.entity;

import com.brp.memoria.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "SHOP_ITEM")
@SequenceGenerator(
        name = "SEQ_SHOP_ITEM_GENERATOR",
        sequenceName = "SEQ_SHOP_ITEM",
        allocationSize = 1
)
public class ShopItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_SHOP_ITEM_GENERATOR")
    @Column(name = "ITEM_ID")
    private Long itemId;

    @Enumerated(EnumType.STRING)
    @Column(name = "CATEGORY", nullable = false, length = 30)
    private ItemCategory category;

    @Column(name = "NAME", nullable = false, length = 200)
    private String name;

    @Column(name = "DESCRIPTION", length = 1000)
    private String description;

    @Column(name = "PRICE", nullable = false)
    private int price;

    @Column(name = "IMAGE_URL", length = 500)
    private String imageUrl;

    @Column(name = "METADATA", length = 2000)
    private String metadata;

    @Column(name = "ACTIVE_YN", nullable = false, columnDefinition = "CHAR(1)")
    private String activeYn;

    @Builder
    public ShopItem(ItemCategory category, String name, String description, int price,
                    String imageUrl, String metadata) {
        this.category = category;
        this.name = name;
        this.description = description;
        this.price = price;
        this.imageUrl = imageUrl;
        this.metadata = metadata;
        this.activeYn = "Y";
    }

    public void deactivate() {
        this.activeYn = "N";
    }

    public void activate() {
        this.activeYn = "Y";
    }

    public enum ItemCategory {
        LOGO, THEME, DIARY_SLOT, WEDDING_FEATURE
    }
}
