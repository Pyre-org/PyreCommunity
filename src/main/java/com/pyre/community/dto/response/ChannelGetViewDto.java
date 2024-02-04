package com.pyre.community.dto.response;

import com.pyre.community.entity.Channel;
import com.pyre.community.enumeration.ChannelGenre;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public record ChannelGetViewDto(
    UUID id,
    String title,
    String description,
    ChannelGenre genre,
    String imageUrl,
    float rating,
    long memberCounts,
    long roomCounts,
    String cAt
) {
    public static ChannelGetViewDto fromEntity(Channel channel) {
        ChannelGetViewDto dto = new ChannelGetViewDto(
                channel.getId(),
                channel.getTitle(),
                channel.getDescription(),
                channel.getGenre(),
                channel.getImageUrl(),
                channel.getRating(),
                channel.getEndUsers().size(),
                channel.getRooms().size(),
                localDateToString(channel.getCAt())
        );
                return dto;
    }
    public static ChannelGetViewDto createChannelGetViewDto(Channel channel) {
        ChannelGetViewDto channelGetViewDto = new ChannelGetViewDto(
                channel.getId(),
                channel.getTitle(),
                channel.getDescription(),
                channel.getGenre(),
                channel.getImageUrl(),
                channel.getRating(),
                channel.getEndUsers().size(),
                channel.getRooms().size(),
                localDateToString(channel.getCAt())
        );
        return channelGetViewDto;
    }
    public static String localDateToString(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        String dateString = localDateTime.format(DateTimeFormatter.ofPattern("YYYY-MM-dd HH:mm"));

        return dateString;
    }
}
