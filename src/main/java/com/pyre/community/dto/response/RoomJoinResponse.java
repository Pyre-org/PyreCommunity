package com.pyre.community.dto.response;

import com.pyre.community.entity.RoomEndUser;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

public record RoomJoinResponse(
        @Schema(description = "룸 UUID", example = "asdasf-qweqw-czxc")
        UUID id
) {
    public static RoomJoinResponse makeDto(RoomEndUser roomEndUser) {
        RoomJoinResponse response = new RoomJoinResponse(roomEndUser.getRoom().getId());
        return response;
    }

}
