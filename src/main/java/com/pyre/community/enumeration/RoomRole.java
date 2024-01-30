package com.pyre.community.enumeration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RoomRole {
    ROOM_ADMIN("ROOM_ADMIN"),
    ROOM_MODE("ROOM_MODE"),
    ROOM_USER("ROOM_USER"),
    ROOM_GUEST("ROOM_GUEST");

    private final String key;
}
