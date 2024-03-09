package com.pyre.community.dto.request;

import com.pyre.community.enumeration.SpaceType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record SpaceCreateRequest(
        @Schema(description = "룸 UUID", example = "asdasf-qweqw-czxc")
        @NotNull
        UUID roomId,
        @NotBlank
        @Schema(description = "스페이스 이름", example = "리그 1방")
        String title,
        @Schema(description = "스페이스 설명", example = "리그 오브 레전드의 방")
        String description,
        @Schema(description = "스페이스 타입", example = "SPACE_PUBLIC")
        @NotNull
        SpaceType type
) {
}
