package com.pyre.community.dto.request;

import com.pyre.community.enumeration.RoomType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record RoomUpdateRequest(
        @NotBlank
        @Schema(description = "룸 이름", example = "리그 1방")
        String title,
        @Schema(description = "룸 설명", example = "리그 오브 레전드의 일반 유저방입니다.")
        String description,
        @Schema(description = "룸 대표 사진", nullable = true, example = "https://someimage.link")
        String imageUrl,
        @Schema(description = "룸 타입", nullable = true, example = "ROOM_PUBLIC")
        @NotNull
        RoomType type
) {
}
