package com.pyre.community.dto.response;

import java.util.UUID;

public record ChannelInfoFromSpaceResponse(
        UUID channelId,
        UUID captureRoomSpaceId
) {
    public static ChannelInfoFromSpaceResponse makeDto(UUID channelId, UUID captureRoomSpaceId) {
        return new ChannelInfoFromSpaceResponse(channelId, captureRoomSpaceId);
    }
}
