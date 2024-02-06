package com.pyre.community.dto.response;

import com.pyre.community.entity.Room;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.ArrayList;
import java.util.List;

public record RoomListByChannelResponse(
        @Schema(description = "룸 조회 수", example = "40")
        int total,
        @Schema(description = "룸 조회 아이템", example = "{}")
        List<RoomGetResponse> hits
) {
    public static RoomListByChannelResponse makeDto(List<Room> rooms) {
        List<RoomGetResponse> roomGetResponses = new ArrayList<>();
        for (Room r : rooms) {
            roomGetResponses.add(RoomGetResponse.makeDto(r));
        }
        RoomListByChannelResponse response =
                new RoomListByChannelResponse(roomGetResponses.size(), roomGetResponses);
        return response;
    }
}
