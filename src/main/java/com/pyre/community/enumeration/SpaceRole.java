package com.pyre.community.enumeration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SpaceRole {
    SPACEROLE_GUEST("SPACEROLE_GUEST"),
    SPACEROLE_USER("SPACEROLE_USER"),
    SPACEROLE_MODE("SPACEROLE_MODE");

    private final String key;
}
