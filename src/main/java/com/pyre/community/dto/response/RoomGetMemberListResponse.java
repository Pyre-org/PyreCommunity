package com.pyre.community.dto.response;

import com.pyre.community.entity.RoomEndUser;

import java.util.ArrayList;
import java.util.List;

public record RoomGetMemberListResponse(
        int total,
        List<RoomGetMemberReponse> hits
) {
    public static RoomGetMemberListResponse makeDto(List<RoomEndUser> roomEndUsers, List<NicknameAndProfileImgResponse> nicknames) {
        List<RoomGetMemberReponse> hits = new ArrayList<>();
        for (RoomEndUser re : roomEndUsers) {
            RoomGetMemberReponse reponse = RoomGetMemberReponse.makeDto(re, nicknames.get(roomEndUsers.indexOf(re)));
            hits.add(reponse);
        }
        return new RoomGetMemberListResponse(hits.size(), hits);
    }
}
