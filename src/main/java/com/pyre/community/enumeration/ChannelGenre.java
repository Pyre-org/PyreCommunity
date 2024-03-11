package com.pyre.community.enumeration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ChannelGenre {
    GENERAL("GENERAL"),
    FPS("FPS"),
    RPG("RPG"),
    SPORTS("SPORTS"),
    MOBILE("MOBILE"),
    CASUAL("CASUAL"),
    SIMULATION("SIMULATION"),
    ETC("ETC");
    private final String key;
}
