package com.pyre.community.dto.response;

import com.pyre.community.entity.Channel;
import com.pyre.community.enumeration.ChannelGenre;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record ChannelGetGenresResponseDto(
        @Schema(description = "채널 장르 조회 수", example = "5")
        int total,
        @Schema(description = "채널 장르 조회 아이템", example = "{}")
        List<ChannelGenre> hits
) {
    public static ChannelGetGenresResponseDto makeDto(List<ChannelGenre> genres) {
        ChannelGetGenresResponseDto dto =
                new ChannelGetGenresResponseDto(genres.size(), genres);
        return dto;
    }
}
