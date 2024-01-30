package com.pyre.community.enumeration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RoomType {
    ROOM_PUBLIC("ROOM_PUBLIC"),
    ROOM_PRIVATE("ROOM_PRIVATE"),
    ROOM_OPEN("ROOM_OPEN"),
    ROOM_GLOBAL("ROOM_GLOBAL"),
    ROOM_CAPTURE("ROOM_CAPTURE");
    private final String key;
}
