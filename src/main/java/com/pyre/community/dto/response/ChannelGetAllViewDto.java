package com.pyre.community.dto.response;

import java.util.List;

public record ChannelGetAllViewDto(
        long total,
        List<ChannelGetViewDto> hits
) {
}