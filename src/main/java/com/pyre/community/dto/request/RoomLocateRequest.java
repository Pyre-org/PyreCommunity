package com.pyre.community.dto.request;

import java.util.UUID;

public record RoomLocateRequest(
        UUID from,
        UUID to
) {
}
