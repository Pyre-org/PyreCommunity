package com.pyre.community.dto.response;

import com.pyre.community.entity.Space;
import com.pyre.community.enumeration.SpaceRole;
import com.pyre.community.enumeration.SpaceType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

public record SpaceCreateResponse(
        @Schema(description = "스페이스 UUID", example = "asdasf-qweqw-czxc")
        UUID id,
        @Schema(description = "룸 UUID", example = "asdasf-qweqw-czxc")
        UUID roomId,
        @Schema(description = "이전 스페이스 UUID", example = "asdasf-qweqw-czxc")
        UUID prevSpaceId,
        @Schema(description = "다음 스페이스 UUID", example = "asdasf-qweqw-czxc")
        UUID nextSpaceId,
        @Schema(description = "스페이스 타입", example = "SPACE_FEED")
        SpaceType type,
        @Schema(description = "스페이스 역할", example = "SPACEROLE_GUEST")
        SpaceRole role,
        @Schema(description = "스페이스 제목", example = "제목")
        String title,
        @Schema(description = "스페이스 설명", example = "설명")
        String description
) {
    public static SpaceCreateResponse makeDto(Space space) {
        SpaceCreateResponse spaceCreateResponse =
                new SpaceCreateResponse(
                        space.getId(),
                        space.getRoom().getId(),
                        space.getPrev() == null ? null : space.getPrev().getId(),
                        space.getNext() == null ? null : space.getNext().getId(),
                        space.getType(),
                        space.getRole(),
                        space.getTitle(),
                        space.getDescription()
                );
        return spaceCreateResponse;
    }
}
