package com.pyre.community.dto.response;

import com.pyre.community.entity.RoomEndUser;

import java.util.UUID;

public record RoomJoinResponse(
        UUID id
) {
    public static RoomJoinResponse makeDto(RoomEndUser roomEndUser) {
        RoomJoinResponse response = new RoomJoinResponse(roomEndUser.getRoom().getId());
        return response;
    }

}
