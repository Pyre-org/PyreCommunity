package com.pyre.community.dto.request;

import com.pyre.community.enumeration.RoomType;

import java.util.UUID;

public record RoomCreateRequest(
        String title,
        String description,
        UUID channelId,
        String imageUrl,
        RoomType type
) {
}
