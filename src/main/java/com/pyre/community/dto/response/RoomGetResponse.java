package com.pyre.community.dto.response;

import com.pyre.community.entity.Room;
import com.pyre.community.enumeration.RoomType;

import java.time.format.DateTimeFormatter;
import java.util.UUID;

public record RoomGetResponse(
        UUID id,
        String title,
        String description,
        String imageUrl,
        RoomType type,
        int memberCounts,
        int spaceCounts,
        String cAt

) {
    public static RoomGetResponse makeDto(
            Room room
    ) {
        RoomGetResponse dto = new RoomGetResponse(
                room.getId(),
                room.getTitle(),
                room.getDescription(),
                room.getImageUrl(),
                room.getType(),
                room.getUsers().size(),
                room.getSpaces().size(),
                room.getCAt().format(DateTimeFormatter.ofPattern("YYYY-MM-dd HH:mm"))
        );
        return dto;
    }
}
