package com.brp.memoria.domain.shop.service;

import com.brp.memoria.domain.member.entity.Member;
import com.brp.memoria.domain.member.repository.MemberRepository;
import com.brp.memoria.domain.shop.dto.ShopItemResponse;
import com.brp.memoria.domain.shop.dto.UserItemResponse;
import com.brp.memoria.domain.shop.entity.ShopItem;
import com.brp.memoria.domain.shop.entity.UserItem;
import com.brp.memoria.domain.shop.exception.ShopErrorCode;
import com.brp.memoria.domain.shop.exception.ShopException;
import com.brp.memoria.domain.shop.repository.ShopItemRepository;
import com.brp.memoria.domain.shop.repository.UserItemRepository;
import com.brp.memoria.domain.ticket.service.TicketService;
import com.brp.memoria.global.exception.BusinessException;
import com.brp.memoria.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ShopService {

    private final ShopItemRepository shopItemRepository;
    private final UserItemRepository userItemRepository;
    private final TicketService ticketService;
    private final MemberRepository memberRepository;

    public List<ShopItemResponse> getItems(String category) {
        ShopItem.ItemCategory itemCategory = ShopItem.ItemCategory.valueOf(category);

        List<ShopItem> items = shopItemRepository.findByCategoryAndActiveYn(itemCategory, "Y");

        return items.stream()
                .map(ShopItemResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public UserItemResponse purchaseItem(Long memberId, Long itemId) {
        ShopItem shopItem = shopItemRepository.findById(itemId)
                .orElseThrow(() -> new ShopException(ShopErrorCode.ITEM_NOT_FOUND));

        if ("N".equals(shopItem.getActiveYn())) {
            throw new ShopException(ShopErrorCode.ITEM_NOT_ACTIVE);
        }

        // 이미 보유 체크
        if (userItemRepository.existsByMemberMemberIdAndShopItemItemId(memberId, itemId)) {
            throw new ShopException(ShopErrorCode.ALREADY_OWNED);
        }

        // 티켓 차감 (무료 아이템이 아닌 경우)
        if (shopItem.getPrice() > 0) {
            ticketService.use(memberId, shopItem.getPrice(),
                    shopItem.getCategory().name(), shopItem.getItemId(),
                    shopItem.getName() + " 구매");
        }

        Member member = findMemberById(memberId);

        UserItem userItem = UserItem.builder()
                .member(member)
                .shopItem(shopItem)
                .build();

        userItemRepository.save(userItem);

        return UserItemResponse.from(userItem);
    }

    public List<UserItemResponse> getMyItems(Long memberId) {
        List<UserItem> userItems = userItemRepository.findByMemberMemberId(memberId);

        return userItems.stream()
                .map(UserItemResponse::from)
                .collect(Collectors.toList());
    }

    private Member findMemberById(Long memberId) {
        return memberRepository.findById(memberId)
                .filter(m -> "N".equals(m.getDelYn()))
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "회원을 찾을 수 없습니다."));
    }
}
