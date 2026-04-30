package com.brp.memoria.domain.shop.dto;

import com.brp.memoria.domain.shop.entity.ShopItem;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ShopItemResponse {

    private Long itemId;
    private String category;
    private String name;
    private String description;
    private int price;
    private String imageUrl;
    private String metadata;

    public static ShopItemResponse from(ShopItem item) {
        return new ShopItemResponse(
                item.getItemId(),
                item.getCategory().name(),
                item.getName(),
                item.getDescription(),
                item.getPrice(),
                item.getImageUrl(),
                item.getMetadata()
        );
    }
}
