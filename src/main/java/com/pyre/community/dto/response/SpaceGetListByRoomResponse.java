package com.pyre.community.dto.response;

import com.pyre.community.entity.Space;

import java.util.ArrayList;
import java.util.List;

public record SpaceGetListByRoomResponse(
        int total,
        List<SpaceGetResponse> hits
) {
    public static SpaceGetListByRoomResponse makeDto(List<Space> spaces) {
        List<SpaceGetResponse> responses = new ArrayList<>();
        for (Space s : spaces) {
            SpaceGetResponse spaceGetResponse = SpaceGetResponse.makeDto(s);
            responses.add(spaceGetResponse);
        }
        return new SpaceGetListByRoomResponse(responses.size(), responses);
    }
}