package com.pyre.community.dto.request;


import com.pyre.community.enumeration.ChannelGenre;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record ChannelCreateDto(
        @NotBlank
        @Schema(description = "채널 이름", example = "리그 오브 레전드")
        String title,
        @NotBlank
        @Schema(description = "채널 설명", example = "리그 오브 레전드는 게임이다.")
        String description,
        @Schema(description = "채널 장르", nullable = true, example = "FPS")
        ChannelGenre genre,
        @Schema(description = "채널 대표 사진", nullable = true, example = "https://someimage.link")
        String imageUrl
) {

}
