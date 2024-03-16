package com.pyre.community.dto.response.search;

import com.pyre.community.dto.response.SpaceGetResponse;
import com.pyre.community.entity.Space;
import com.pyre.community.enumeration.SpaceRole;
import com.pyre.community.enumeration.SpaceType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

public record SpaceSearchResponse(
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
        SpaceRole role
) {
    public static SpaceSearchResponse makeDto(Space space) {
        return new SpaceSearchResponse(
                space.getId(),
                space.getRoom().getId(),
                space.getTitle(),
                space.getDescription(),
                space.getType(),
                space.getRole()
        );
    }
}
