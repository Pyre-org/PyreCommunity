package com.pyre.community.dto.response;

import com.pyre.community.entity.Space;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.ArrayList;
import java.util.List;

public record SpaceGetListByRoomResponse(
        @Schema(description = "스페이스 조회 수", example = "40")
        int total,
        @Schema(description = "스페이스 조회 아이템", example = "{}")
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