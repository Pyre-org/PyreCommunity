package com.pyre.community.dto.request;


import com.pyre.community.enumeration.ChannelGenre;

public record ChannelEditDto(
        String title,
        String description,
        ChannelGenre genre,
        String imageUrl
) {
}
