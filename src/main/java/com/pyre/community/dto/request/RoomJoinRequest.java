package com.pyre.community.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record RoomJoinRequest(
        @NotBlank
        @Schema(description = "룸 UUID", example = "ASDvcxv-q222aSDc-ASDfvc")
        String channelId
) {
}
