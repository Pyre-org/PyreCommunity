package com.pyre.community.dto.request;


import com.pyre.community.enumeration.ApprovalStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record ChannelUpdateApprovalStatusDto(
        @Schema(description = "채널 승인 상태", nullable = true, example = "ALLOW")
        ApprovalStatus status

        
) {
}
