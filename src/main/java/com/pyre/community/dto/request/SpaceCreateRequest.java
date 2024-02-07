package com.pyre.community.dto.request;

import com.pyre.community.enumeration.SpaceType;

import java.util.UUID;

public record SpaceCreateRequest(
        UUID roomId,
        String title,
        String description,
        SpaceType type
) {
}
