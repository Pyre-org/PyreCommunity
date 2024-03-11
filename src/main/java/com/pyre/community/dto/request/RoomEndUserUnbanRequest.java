package com.pyre.community.dto.request;

import java.util.UUID;

public record RoomEndUserUnbanRequest(
        UUID RoomEndUserId,
        UUID roomId,
        String reason
) {
}
