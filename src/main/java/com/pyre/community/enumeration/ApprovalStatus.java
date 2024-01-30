package com.pyre.community.enumeration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ApprovalStatus {
    ALLOW("ALLOW"),
    DENY("DENY"),
    CHECKING("CHECKING");
    private final String key;
}
