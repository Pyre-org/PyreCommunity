package com.pyre.community.dto.request;

public record ChannelJoinRequest(
        long channelId,
        Boolean agreement
) {
}
