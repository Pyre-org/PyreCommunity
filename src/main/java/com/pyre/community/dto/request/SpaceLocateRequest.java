package com.pyre.community.dto.request;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record SpaceLocateRequest(
        @NotNull
        UUID from,
        @NotNull
        UUID to
) {
}
