package com.pyre.community.enumeration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SpaceType {
    SPACE_FEED("SPACE_FEED"),
    SPACE_CHAT("SPACE_CHAT");

    private final String key;
}
