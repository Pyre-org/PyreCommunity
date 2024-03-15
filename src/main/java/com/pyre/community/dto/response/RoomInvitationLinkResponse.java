package com.pyre.community.dto.response;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public record RoomInvitationLinkResponse(
        String invitationLink,
        String expirationDate
) {
    public static RoomInvitationLinkResponse makeDto (String invitationLink, LocalDateTime expirationDate){
        return new RoomInvitationLinkResponse(invitationLink, expirationDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    }
}
