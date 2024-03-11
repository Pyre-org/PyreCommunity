package com.pyre.community.enumeration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SpaceType {
    SPACE_GENERAL("SPACE_GENERAL"),
    SPACE_GENERAL_CHAT("SPACE_GENERAL_CHAT"),
    SPACE_FEED("SPACE_FEED"),
    SPACE_CHAT("SPACE_CHAT");

    private final String key;
}
