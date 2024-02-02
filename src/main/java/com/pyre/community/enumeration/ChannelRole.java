package com.pyre.community.enumeration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ChannelRole {
    CHANNEL_ADMIN("CHANNEL_ADMIN"),
    CHANNEL_USER("CHANNEL_USER");

    private final String key;
}
