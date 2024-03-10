package com.pyre.community.dto.response;

import com.pyre.community.entity.Space;
import com.pyre.community.enumeration.SpaceRole;
import com.pyre.community.enumeration.SpaceType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

public record SpaceGetResponse (
        @Schema(description = "스페이스 UUID", example = "asdasf-qweqw-czxc")
        UUID id,
        @Schema(description = "룸 UUID", example = "asdasf-qweqw-czxc")
        UUID roomId,
        @Schema(description = "스페이스 이름", example = "오버워치")
        String title,
        @Schema(description = "스페이스 설명", example = "오버워치 게임 스페이스")
        String description,
        @Schema(description = "스페이스 타입", example = "SPACE_FEED")
        SpaceType type,
        @Schema(description = "스페이스 역할", example = "SPACEROLE_GUEST")
        SpaceRole role,
        @Schema(description = "이전 스페이스 UUID", example = "asdasf-qweqw-czxc")
        UUID prevSpaceId,
        @Schema(description = "다음 스페이스 UUID", example = "asdasf-qweqw-czxc")
        UUID nextSpaceId
) {
    public static SpaceGetResponse makeDto(Space space) {
        return new SpaceGetResponse(
                space.getId(),
                space.getRoom().getId(),
                space.getTitle(),
                space.getDescription(),
                space.getType(),
                space.getRole(),
                space.getPrev() == null ? null : space.getPrev().getId(),
                space.getNext() == null ? null : space.getNext().getId()
        );
    }
}