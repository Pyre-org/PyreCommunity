package com.pyre.community.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ChannelLocateRequest(
        @NotNull
        @Schema(description = "채널 옮길 위치", example = "5")
        int to,
        @NotNull
        @Schema(description = "채널 UUID", example = "4123asd-vcxere-ADfv")
        UUID channelId
) {
}
