package com.pyre.community.dto.request;

import java.util.UUID;

public record ChannelJoinRequest(
        UUID channelId,
        Boolean agreement
) {
}
