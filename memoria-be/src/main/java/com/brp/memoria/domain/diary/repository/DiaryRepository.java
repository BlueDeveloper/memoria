package com.brp.memoria.domain.diary.repository;

import com.brp.memoria.domain.diary.entity.Diary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DiaryRepository extends JpaRepository<Diary, Long> {

    Optional<Diary> findByInviteCode(String inviteCode);
}
