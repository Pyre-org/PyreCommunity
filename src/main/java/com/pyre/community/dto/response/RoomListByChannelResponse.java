package com.pyre.community.dto.response;

import com.pyre.community.entity.Room;

import java.util.ArrayList;
import java.util.List;

public record RoomListByChannelResponse(
        int total,
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
