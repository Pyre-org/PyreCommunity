package com.pyre.community.dto.request;

import com.pyre.community.enumeration.SpaceRole;
import com.pyre.community.enumeration.SpaceType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

public record SpaceUpdateRequest(
        @Schema(description = "스페이스 역할", example = "SPACEROLE_GUEST")
        SpaceRole role,
        @Schema(description = "스페이스 제목", example = "제목")
        String title,
        @Schema(description = "스페이스 설명", example = "설명")
        String description
) {
}
