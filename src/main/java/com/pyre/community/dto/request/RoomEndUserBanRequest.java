package com.pyre.community.dto.request;

import java.util.UUID;

public record RoomEndUserBanRequest(
        UUID RoomEndUserId,
        String reason,
        UUID roomId
) {
}
