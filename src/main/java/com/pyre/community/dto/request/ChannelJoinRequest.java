package com.pyre.community.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ChannelJoinRequest(
        @NotNull
        @Schema(description = "채널 UUID", example = "5asd-4123-fvcx")
        UUID channelId,
        @NotBlank
        @Schema(description = "채널 참가 동의", example = "true")
        Boolean agreement
) {
}
