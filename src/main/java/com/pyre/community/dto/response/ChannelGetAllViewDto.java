package com.pyre.community.dto.response;

import java.util.List;

public record ChannelGetAllViewDto(
        long total,
        List<ChannelGetViewDto> hits
) {
    public static ChannelGetAllViewDto makeDto(long size, List<ChannelGetViewDto> hits) {
        ChannelGetAllViewDto dto = new ChannelGetAllViewDto(size, hits);
        return dto;
    }
}
