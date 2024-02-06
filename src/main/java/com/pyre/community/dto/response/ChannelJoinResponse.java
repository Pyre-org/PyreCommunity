package com.pyre.community.dto.response;

import com.pyre.community.entity.ChannelEndUser;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

public record ChannelJoinResponse(
        @Schema(description = "채널 UUID", example = "asdf-QWecz-ASdf")
        UUID channelId,
        @Schema(description = "채널 참여 일", example = "YYYY-MM-dd HH:mm")
        LocalDateTime joinDate,
        @Schema(description = "채널 참여 동의", example = "true")
        Boolean agreement
) {
    public static ChannelJoinResponse makeDto(UUID channelId, Boolean agreement) {
        ChannelJoinResponse response = new ChannelJoinResponse(channelId, LocalDateTime.now(), agreement);
        return response;
    }
}
