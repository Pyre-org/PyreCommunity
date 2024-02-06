package com.pyre.community.dto.response;

import com.pyre.community.entity.Channel;
import com.pyre.community.entity.Room;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

public record RoomCreateResponse(
        @Schema(description = "룸 UUID", example = "asdasf-qweqw-czxc")
        UUID id,
        @Schema(description = "룸 이름", example = "리그 1방")
        String title
) {
    public static RoomCreateResponse makeDto(Room room) {
        RoomCreateResponse roomCreateResponse =
                new RoomCreateResponse(room.getId(), room.getTitle());
        return roomCreateResponse;
    }
}
