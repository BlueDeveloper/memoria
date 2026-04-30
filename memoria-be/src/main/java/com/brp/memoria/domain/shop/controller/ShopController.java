package com.brp.memoria.domain.shop.controller;

import com.brp.memoria.domain.shop.dto.PurchaseRequest;
import com.brp.memoria.domain.shop.dto.ShopItemResponse;
import com.brp.memoria.domain.shop.dto.UserItemResponse;
import com.brp.memoria.domain.shop.service.ShopService;
import com.brp.memoria.global.common.ApiResponse;
import com.brp.memoria.global.security.SecurityUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shop")
@RequiredArgsConstructor
public class ShopController {

    private final ShopService shopService;

    @GetMapping("/items")
    public ResponseEntity<ApiResponse<List<ShopItemResponse>>> getItems(
            @RequestParam String category) {
        List<ShopItemResponse> response = shopService.getItems(category);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PostMapping("/purchase")
    public ResponseEntity<ApiResponse<UserItemResponse>> purchaseItem(
            @Valid @RequestBody PurchaseRequest request) {
        Long memberId = SecurityUtil.getCurrentMemberId();
        UserItemResponse response = shopService.purchaseItem(memberId, request.getItemId());
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/my-items")
    public ResponseEntity<ApiResponse<List<UserItemResponse>>> getMyItems() {
        Long memberId = SecurityUtil.getCurrentMemberId();
        List<UserItemResponse> response = shopService.getMyItems(memberId);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }
}
