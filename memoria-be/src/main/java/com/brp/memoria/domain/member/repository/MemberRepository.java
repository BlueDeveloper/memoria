package com.brp.memoria.domain.member.repository;

import com.brp.memoria.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByEmail(String email);

    Optional<Member> findByProviderAndProviderId(Member.Provider provider, String providerId);

    boolean existsByEmail(String email);
}
