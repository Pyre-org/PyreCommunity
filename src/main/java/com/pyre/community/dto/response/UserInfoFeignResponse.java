package com.pyre.community.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

public record UserInfoFeignResponse(
        @Schema(description = "유저 UUID", example = "asdasf-qweqw-czxc")
        UUID id,
        @Schema(description = "유저 이메일", example = "sasa@sasa.com")
        String email,
        @Schema(description = "유저 닉네임", example = "nickname2")
        String nickname,
        @Schema(description = "유저 역할", example = "ROLE_USER")
        String role
) {
}
