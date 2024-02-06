package com.pyre.community.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record ChannelSearchRequest(

        @Schema(description = "채널 검색어", nullable = true, example = "오버워")
        String keyword
) {
}
