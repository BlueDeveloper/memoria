package com.brp.memoria.domain.ticket.service;

import com.brp.memoria.domain.member.entity.Member;
import com.brp.memoria.domain.member.repository.MemberRepository;
import com.brp.memoria.domain.ticket.dto.TicketBalanceResponse;
import com.brp.memoria.domain.ticket.dto.TicketTransactionResponse;
import com.brp.memoria.domain.ticket.entity.TicketBalance;
import com.brp.memoria.domain.ticket.entity.TicketTransaction;
import com.brp.memoria.domain.ticket.exception.TicketErrorCode;
import com.brp.memoria.domain.ticket.exception.TicketException;
import com.brp.memoria.domain.ticket.repository.TicketBalanceRepository;
import com.brp.memoria.domain.ticket.repository.TicketTransactionRepository;
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
public class TicketService {

    private final TicketBalanceRepository ticketBalanceRepository;
    private final TicketTransactionRepository ticketTransactionRepository;
    private final MemberRepository memberRepository;

    public TicketBalanceResponse getBalance(Long memberId) {
        TicketBalance balance = getOrCreateBalance(memberId);
        return TicketBalanceResponse.of(balance.getBalance());
    }

    @Transactional
    public TicketBalanceResponse charge(Long memberId, int amount, String description) {
        if (amount <= 0) {
            throw new TicketException(TicketErrorCode.INVALID_AMOUNT);
        }

        TicketBalance balance = getOrCreateBalance(memberId);
        balance.addBalance(amount);

        Member member = findMemberById(memberId);

        TicketTransaction transaction = TicketTransaction.builder()
                .member(member)
                .type(TicketTransaction.TransactionType.CHARGE)
                .amount(amount)
                .balanceAfter(balance.getBalance())
                .description(description != null ? description : "티켓 충전")
                .build();

        ticketTransactionRepository.save(transaction);

        return TicketBalanceResponse.of(balance.getBalance());
    }

    @Transactional
    public void use(Long memberId, int amount, String itemType, Long itemId, String description) {
        if (amount <= 0) {
            throw new TicketException(TicketErrorCode.INVALID_AMOUNT);
        }

        TicketBalance balance = getOrCreateBalance(memberId);

        if (balance.getBalance() < amount) {
            throw new TicketException(TicketErrorCode.INSUFFICIENT_TICKETS);
        }

        balance.subtractBalance(amount);

        Member member = findMemberById(memberId);

        TicketTransaction transaction = TicketTransaction.builder()
                .member(member)
                .type(TicketTransaction.TransactionType.USE)
                .amount(amount)
                .balanceAfter(balance.getBalance())
                .itemType(itemType)
                .itemId(itemId)
                .description(description != null ? description : "티켓 사용")
                .build();

        ticketTransactionRepository.save(transaction);
    }

    public List<TicketTransactionResponse> getHistory(Long memberId) {
        List<TicketTransaction> transactions =
                ticketTransactionRepository.findByMemberMemberIdOrderByCreDtDesc(memberId);

        return transactions.stream()
                .map(TicketTransactionResponse::from)
                .collect(Collectors.toList());
    }

    // === 내부 헬퍼 메서드 ===

    @Transactional
    public TicketBalance getOrCreateBalance(Long memberId) {
        return ticketBalanceRepository.findByMemberMemberId(memberId)
                .orElseGet(() -> {
                    Member member = findMemberById(memberId);
                    TicketBalance newBalance = TicketBalance.builder()
                            .member(member)
                            .balance(0)
                            .build();
                    return ticketBalanceRepository.save(newBalance);
                });
    }

    private Member findMemberById(Long memberId) {
        return memberRepository.findById(memberId)
                .filter(m -> "N".equals(m.getDelYn()))
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "회원을 찾을 수 없습니다."));
    }
}
