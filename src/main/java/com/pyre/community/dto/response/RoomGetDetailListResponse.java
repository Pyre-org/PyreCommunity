package com.pyre.community.dto.response;

import com.pyre.community.entity.Room;

import java.util.ArrayList;
import java.util.List;

public record RoomGetDetailListResponse(
        int total,
        List<RoomGetDetailResponse> hits
) {
    public static RoomGetDetailListResponse makeDto(List<RoomGetDetailResponse> rooms) {
        return new RoomGetDetailListResponse(rooms.size(), rooms);
    }
}
