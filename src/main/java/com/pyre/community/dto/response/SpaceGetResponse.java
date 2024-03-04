package com.pyre.community.dto.response;

import com.pyre.community.entity.Space;
import com.pyre.community.enumeration.SpaceRole;
import com.pyre.community.enumeration.SpaceType;

import java.util.UUID;

public record SpaceGetResponse (
        UUID id,
        UUID roomId,
        SpaceType type,
        SpaceRole role,
        UUID prevSpaceId,
        UUID nextSpaceId
) {
    public static SpaceGetResponse makeDto(Space space) {
        return new SpaceGetResponse(
                space.getId(),
                space.getRoom().getId(),
                space.getType(),
                space.getRole(),
                space.getPrev() == null ? null : space.getPrev().getId(),
                space.getNext() == null ? null : space.getNext().getId()
        );
    }
}