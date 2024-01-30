package com.pyre.community.dto.response;

public record UserInfoFeignResponse(
        long id,
        String email,
        String nickname,
        String role
) {
}
