package com.pyre.community.enumeration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ChannelGenre {
    GENERAL("GENERAL"),
    FPS("FPS");
    private final String key;
}
