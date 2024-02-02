package com.pyre.community.dto.response;

import com.pyre.community.entity.ChannelEndUser;

import java.time.LocalDateTime;

public record ChannelJoinResponse(
        long channelId,

        LocalDateTime joinDate,
        Boolean agreement
) {
    public static ChannelJoinResponse makeDto(long channelId, Boolean agreement) {
        ChannelJoinResponse response = new ChannelJoinResponse(channelId, LocalDateTime.now(), agreement);
        return response;
    }
}
