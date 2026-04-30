package com.brp.memoria.domain.shop.repository;

import com.brp.memoria.domain.shop.entity.ShopItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ShopItemRepository extends JpaRepository<ShopItem, Long> {

    List<ShopItem> findByCategoryAndActiveYn(ShopItem.ItemCategory category, String activeYn);
}
