package com.brp.memoria.domain.shop.dto;

import com.brp.memoria.domain.shop.entity.UserItem;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserItemResponse {

    private Long userItemId;
    private Long itemId;
    private String category;
    private String name;
    private String description;
    private String imageUrl;
    private LocalDateTime purchasedAt;

    public static UserItemResponse from(UserItem userItem) {
        return new UserItemResponse(
                userItem.getUserItemId(),
                userItem.getShopItem().getItemId(),
                userItem.getShopItem().getCategory().name(),
                userItem.getShopItem().getName(),
                userItem.getShopItem().getDescription(),
                userItem.getShopItem().getImageUrl(),
                userItem.getPurchasedAt()
        );
    }
}
