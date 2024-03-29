package com.pyre.community.service;

import com.pyre.community.dto.request.SpaceCreateRequest;
import com.pyre.community.dto.request.SpaceLocateRequest;
import com.pyre.community.dto.request.SpaceUpdateRequest;
import com.pyre.community.dto.response.ChannelInfoFromSpaceResponse;
import com.pyre.community.dto.response.SpaceCreateResponse;
import com.pyre.community.dto.response.SpaceGetListByRoomResponse;
import com.pyre.community.dto.response.SpaceGetResponse;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

public interface SpaceService {
    @Transactional
    SpaceCreateResponse createSpace(SpaceCreateRequest spaceCreateRequest, UUID userId);
    @Transactional(readOnly = true)
    SpaceGetListByRoomResponse getSpaceListByRoom(UUID userId, String roomId);
    @Transactional(readOnly = true)
    SpaceGetResponse getSpace(UUID userId, String spaceId);
    @Transactional
    String updateSpace(UUID userId, String spaceId, SpaceUpdateRequest spaceUpdateRequest);
    @Transactional
    String deleteSpace(UUID userId, String spaceId);
    @Transactional
    String locateSpace(UUID userId, SpaceLocateRequest spaceLocateRequest);
    @Transactional(readOnly = true)
    Boolean canWriteSpace(UUID userId, String spaceId);
    @Transactional(readOnly = true)
    String getCaptureSpace(String userId, String channelId);
    @Transactional(readOnly = true)
    List<UUID> canReadSpaces(UUID userId);
    @Transactional(readOnly = true)
    ChannelInfoFromSpaceResponse getChannelCaptureSpace(String userId, String spaceId);
}
