package com.pyre.community.dto.response;

import com.pyre.community.entity.Channel;
import com.pyre.community.enumeration.ChannelGenre;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public record ChannelGetViewDto(
    @Schema(description = "채널 UUID", example = "asdf-qwex-vzxc")
    UUID id,
    @Schema(description = "채널 이름", example = "리그 오브 레전드")
    String title,
    @Schema(description = "채널 설명", example = "리그 오브 레전드의 채널이다.")
    String description,
    @Schema(description = "채널 장르", example = "FPS")
    ChannelGenre genre,
    @Schema(description = "채널 대표 사진", example = "https://someimage.link")
    String imageUrl,
    @Schema(description = "채널 평점", example = "4.3")
    float rating,
    @Schema(description = "채널 멤버 수", example = "50")
    long memberCounts,
    @Schema(description = "채널 룸 수", example = "20")
    long roomCounts,
    @Schema(description = "채널 생성 일", example = "YYYY-MM-dd HH:mm")
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
