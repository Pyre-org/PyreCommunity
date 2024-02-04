package com.pyre.community.dto.response;

import com.pyre.community.entity.ChannelEndUser;

import java.time.LocalDateTime;
import java.util.UUID;

public record ChannelJoinResponse(
        UUID channelId,

        LocalDateTime joinDate,
        Boolean agreement
) {
    public static ChannelJoinResponse makeDto(UUID channelId, Boolean agreement) {
        ChannelJoinResponse response = new ChannelJoinResponse(channelId, LocalDateTime.now(), agreement);
        return response;
    }
}
