package com.brp.memoria.domain.ticket.controller;

import com.brp.memoria.domain.ticket.dto.TicketBalanceResponse;
import com.brp.memoria.domain.ticket.dto.TicketChargeRequest;
import com.brp.memoria.domain.ticket.dto.TicketTransactionResponse;
import com.brp.memoria.domain.ticket.service.TicketService;
import com.brp.memoria.global.common.ApiResponse;
import com.brp.memoria.global.security.SecurityUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    @GetMapping("/balance")
    public ResponseEntity<ApiResponse<TicketBalanceResponse>> getBalance() {
        Long memberId = SecurityUtil.getCurrentMemberId();
        TicketBalanceResponse response = ticketService.getBalance(memberId);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/history")
    public ResponseEntity<ApiResponse<List<TicketTransactionResponse>>> getHistory() {
        Long memberId = SecurityUtil.getCurrentMemberId();
        List<TicketTransactionResponse> response = ticketService.getHistory(memberId);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PostMapping("/charge")
    public ResponseEntity<ApiResponse<TicketBalanceResponse>> charge(
            @Valid @RequestBody TicketChargeRequest request) {
        // TODO: 결제 연동 후 실제 결제 검증 로직 추가
        Long memberId = SecurityUtil.getCurrentMemberId();
        TicketBalanceResponse response = ticketService.charge(memberId, request.getAmount(), request.getDescription());
        return ResponseEntity.ok(ApiResponse.ok(response));
    }
}
