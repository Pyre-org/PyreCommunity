package com.pyre.community.dto.request;

import java.util.UUID;

public record RoomInvitationCreateRequest(
        UUID roomId,
        int maxDays
) {
}
