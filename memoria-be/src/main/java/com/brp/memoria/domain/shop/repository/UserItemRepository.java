package com.brp.memoria.domain.shop.repository;

import com.brp.memoria.domain.shop.entity.UserItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserItemRepository extends JpaRepository<UserItem, Long> {

    List<UserItem> findByMemberMemberId(Long memberId);

    boolean existsByMemberMemberIdAndShopItemItemId(Long memberId, Long itemId);
}
