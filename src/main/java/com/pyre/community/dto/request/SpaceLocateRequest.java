package com.pyre.community.dto.request;

import java.util.UUID;

public record SpaceLocateRequest(
        UUID from,
        UUID to
) {
}
