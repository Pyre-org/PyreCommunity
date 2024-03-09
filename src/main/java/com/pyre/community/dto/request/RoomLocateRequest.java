package com.pyre.community.dto.request;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record RoomLocateRequest(
        @NotNull
        UUID from,
        @NotNull
        UUID to
) {
}
