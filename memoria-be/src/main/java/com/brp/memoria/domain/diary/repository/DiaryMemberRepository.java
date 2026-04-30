package com.brp.memoria.domain.diary.repository;

import com.brp.memoria.domain.diary.entity.Diary;
import com.brp.memoria.domain.diary.entity.DiaryMember;
import com.brp.memoria.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DiaryMemberRepository extends JpaRepository<DiaryMember, Long> {

    Optional<DiaryMember> findByDiaryAndMember(Diary diary, Member member);

    List<DiaryMember> findByMemberAndDelYn(Member member, String delYn);

    List<DiaryMember> findByDiaryAndDelYn(Diary diary, String delYn);

    long countByMemberAndDelYn(Member member, String delYn);
}
