package com.pyre.community.dto.response.search;

import com.pyre.community.entity.Room;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;

public record RoomSearchListResponse(
        Long total,
        List<RoomSearchResponse> hits
) {
    public static RoomSearchListResponse makeDto(Page<Room> rooms) {
        List<RoomSearchResponse> roomSearchResponses = new ArrayList<>();
        for (Room room : rooms.getContent()) {
            roomSearchResponses.add(RoomSearchResponse.makeDto(room));
        }
        return new RoomSearchListResponse(rooms.getTotalElements(), roomSearchResponses);
    }
}
