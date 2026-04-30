package com.brp.memoria.domain.ticket.repository;

import com.brp.memoria.domain.ticket.entity.TicketTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TicketTransactionRepository extends JpaRepository<TicketTransaction, Long> {

    List<TicketTransaction> findByMemberMemberIdOrderByCreDtDesc(Long memberId);
}
