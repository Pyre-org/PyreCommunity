package com.pyre.community.dto.request;


import com.pyre.community.enumeration.ChannelGenre;
import jakarta.validation.constraints.NotBlank;

public record ChannelCreateDto(
        @NotBlank
        String title,
        @NotBlank
        String description,

        ChannelGenre genre,
        String imageUrl
) {

}
