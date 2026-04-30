package com.brp.memoria.domain.calendar.repository;

import com.brp.memoria.domain.calendar.entity.Invitation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InvitationRepository extends JpaRepository<Invitation, Long> {

    Optional<Invitation> findByToken(String token);

    List<Invitation> findByInviteeEmailAndStatus(String inviteeEmail, Invitation.InvitationStatus status);
}
