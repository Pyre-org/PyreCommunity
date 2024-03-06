package com.pyre.community.service;

import com.pyre.community.dto.request.SpaceCreateRequest;
import com.pyre.community.dto.response.SpaceCreateResponse;
import com.pyre.community.dto.response.SpaceGetListByRoomResponse;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

public interface SpaceService {
    @Transactional
    SpaceCreateResponse createSpace(SpaceCreateRequest spaceCreateRequest, UUID userId);
    @Transactional(readOnly = true)
    SpaceGetListByRoomResponse getSpaceListByRoom(UUID userId, String roomId);
}
