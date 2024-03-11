package com.pyre.community.enumeration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum RoomEndUserStatus {
    ACTIVE("ACTIVE"),
    BANNED("BANNED"),
    DELETED("DELETED");
    private final String key;
}
