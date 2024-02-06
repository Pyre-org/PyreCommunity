package com.pyre.community.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record ChannelGetAllViewDto(
        @Schema(description = "채널 조회 수", example = "5")
        long total,
        @Schema(description = "채널 조회 아이템", example = "{}")
        List<ChannelGetViewDto> hits
) {
    public static ChannelGetAllViewDto makeDto(long size, List<ChannelGetViewDto> hits) {
        ChannelGetAllViewDto dto = new ChannelGetAllViewDto(size, hits);
        return dto;
    }
}
