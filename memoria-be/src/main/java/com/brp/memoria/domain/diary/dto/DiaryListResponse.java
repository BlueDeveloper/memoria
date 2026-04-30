package com.brp.memoria.domain.diary.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DiaryListResponse {

    private List<DiaryResponse> diaries;

    public static DiaryListResponse of(List<DiaryResponse> diaries) {
        return new DiaryListResponse(diaries);
    }
}
