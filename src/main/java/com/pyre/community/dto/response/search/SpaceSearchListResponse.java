package com.pyre.community.dto.response.search;

import com.pyre.community.entity.Space;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;

public record SpaceSearchListResponse(
        Long total,
        List<SpaceSearchResponse> hits
) {
    public static SpaceSearchListResponse makeDto(Page<Space> spaces) {
        List<SpaceSearchResponse> spaceSearchResponses = new ArrayList<>();
        for (Space space : spaces.getContent()) {
            spaceSearchResponses.add(SpaceSearchResponse.makeDto(space));
        }
        return new SpaceSearchListResponse(spaces.getTotalElements(), spaceSearchResponses);
    }
}
