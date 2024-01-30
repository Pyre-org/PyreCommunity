package com.pyre.community.dto.response;

import com.pyre.community.entity.Channel;
import com.pyre.community.enumeration.ChannelGenre;

import java.util.List;

public record ChannelGetGenresResponseDto(
        int total,
        List<ChannelGenre> hits
) {
    public static ChannelGetGenresResponseDto makeDto(List<ChannelGenre> genres) {
        ChannelGetGenresResponseDto dto =
                new ChannelGetGenresResponseDto(genres.size(), genres);
        return dto;
    }
}
