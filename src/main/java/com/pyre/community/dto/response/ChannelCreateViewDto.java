package com.pyre.community.dto.response;

import com.pyre.community.entity.Channel;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record ChannelCreateViewDto(
        @Schema(description = "채널 UUID", example = "asdfASFcvx-QWEf-ASDC")
        UUID id,
        @Schema(description = "채널 이름", example = "리그 오브 레전드")
        String title
) {
    public static ChannelCreateViewDto makeDto(Channel channel) {
        ChannelCreateViewDto channelCreateViewDto =
                new ChannelCreateViewDto(channel.getId(), channel.getTitle());
        return channelCreateViewDto;
    }
}
