package com.pyre.community.dto.response;

import com.pyre.community.entity.Channel;

public record ChannelCreateViewDto(
        long id,
        String title
) {
    public static ChannelCreateViewDto makeDto(Channel channel) {
        ChannelCreateViewDto channelCreateViewDto =
                new ChannelCreateViewDto(channel.getId(), channel.getTitle());
        return channelCreateViewDto;
    }
}
