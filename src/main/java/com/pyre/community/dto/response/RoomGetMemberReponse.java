package com.pyre.community.dto.response;

import com.pyre.community.entity.RoomEndUser;
import com.pyre.community.enumeration.RoomRole;

import java.util.UUID;

public record RoomGetMemberReponse(
        UUID userId,
        String nickname,
        String profileImageUrl,
        RoomRole role,
        Boolean isOwner
) {
    public static RoomGetMemberReponse makeDto(RoomEndUser roomEndUser, NicknameAndProfileImgResponse nicknameAndProfileImgResponse) {
        return new RoomGetMemberReponse(roomEndUser.getUserId(), nicknameAndProfileImgResponse.nickname(),
                nicknameAndProfileImgResponse.profilePictureUrl(), roomEndUser.getRole(), roomEndUser.getOwner().equals(roomEndUser.getUserId()));
    }
}
