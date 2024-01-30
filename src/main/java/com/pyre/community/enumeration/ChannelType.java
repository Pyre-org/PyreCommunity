package com.pyre.community.enumeration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ChannelType {
    CHANNEL_PUBLIC("CHANNEL_PUBLIC"),
    CHANNEL_PRIVATE("CHANNEL_PRIVATE"),
    CHANNEL_OPEN("CHANNEL_OPEN");

    private final String key;
}
