package com.pyre.community.dto.response;

import java.util.UUID;

public record UserInfoFeignResponse(
        UUID id,
        String email,
        String nickname,
        String role
) {
}
