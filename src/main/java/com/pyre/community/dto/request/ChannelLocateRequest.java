package com.pyre.community.dto.request;

import java.util.UUID;

public record ChannelLocateRequest(
        int to,
        UUID channelId
) {
}
