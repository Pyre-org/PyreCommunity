package com.pyre.community.dto.response;

import com.pyre.community.entity.Room;
import com.pyre.community.enumeration.RoomType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.format.DateTimeFormatter;
import java.util.UUID;

public record RoomGetResponse(
        @Schema(description = "룸 UUID", example = "asdasf-qweqw-czxc")
        UUID id,
        @Schema(description = "룸 이름", example = "리그 1방")
        String title,
        @Schema(description = "룸 설명", example = "리그 오브 레전드의 방")
        String description,
        @Schema(description = "룸 대표 사진", example = "https://someimage.link")
        String imageUrl,
        @Schema(description = "룸 타입", example = "ROOM_PUBLIC")
        RoomType type,
        @Schema(description = "룸 유저 수", example = "50")
        int memberCounts,
        @Schema(description = "룸 스페이스 수", example = "5")
        int spaceCounts,
        @Schema(description = "룸 생성 일", example = "YYYY-MM-dd HH:mm")
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
