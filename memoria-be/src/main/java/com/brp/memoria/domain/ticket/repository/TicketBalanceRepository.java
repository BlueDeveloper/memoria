package com.brp.memoria.domain.ticket.repository;

import com.brp.memoria.domain.ticket.entity.TicketBalance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TicketBalanceRepository extends JpaRepository<TicketBalance, Long> {

    Optional<TicketBalance> findByMemberMemberId(Long memberId);
}
