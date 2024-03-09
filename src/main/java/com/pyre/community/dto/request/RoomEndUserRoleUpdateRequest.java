package com.pyre.community.dto.request;

import com.pyre.community.enumeration.RoomRole;

import java.util.UUID;

public record RoomEndUserRoleUpdateRequest(
        UUID userId,
        UUID roomId,
        RoomRole role


) {
}
