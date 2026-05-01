package com.brp.memoria.domain.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OAuthCallbackRequest {

    @NotBlank(message = "인가 코드는 필수입니다.")
    private String code;

    @NotBlank(message = "리다이렉트 URI는 필수입니다.")
    private String redirectUri;
}
