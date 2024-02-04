package com.pyre.community.dto.response;

import com.pyre.community.entity.Channel;
import com.pyre.community.entity.Room;

import java.util.UUID;

public record RoomCreateResponse(
        UUID id,
        String title
) {
    public static RoomCreateResponse makeDto(Room room) {
        RoomCreateResponse roomCreateResponse =
                new RoomCreateResponse(room.getId(), room.getTitle());
        return roomCreateResponse;
    }
}
