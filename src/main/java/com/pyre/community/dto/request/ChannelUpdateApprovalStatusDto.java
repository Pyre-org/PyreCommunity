package com.pyre.community.dto.request;


import com.pyre.community.enumeration.ApprovalStatus;

public record ChannelUpdateApprovalStatusDto(
        ApprovalStatus status
) {
}
