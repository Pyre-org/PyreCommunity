package com.pyre.community.service;

import com.pyre.community.dto.request.RoomCreateRequest;
import com.pyre.community.dto.request.RoomLocateRequest;
import com.pyre.community.dto.request.RoomUpdateRequest;
import com.pyre.community.dto.response.*;
import org.springframework.transaction.annotation.Transactional;


import java.util.UUID;

public interface RoomService {
    @Transactional
    RoomCreateResponse createRoom(UUID userId, RoomCreateRequest roomCreateRequest);
    @Transactional(readOnly = true)
    RoomGetDetailResponse getRoom(UUID id, UUID userId);
    @Transactional(readOnly = true)
    RoomListByChannelResponse listByChannelAndKeywordAndType(UUID channelId, String keyword, String type);
    @Transactional(readOnly = true)
    RoomListByChannelResponse listByChannelAndKeywordAndUserId(UUID channelId, String keyword, UUID userId);
    @Transactional(readOnly = true)
    RoomListByChannelResponse listByChannelAndUserIdByIndexing(UUID channelId, UUID userId);
    @Transactional
    RoomJoinResponse joinRoom(UUID roomId, UUID userId, UUID channelId);
    @Transactional
    UUID leaveRoom(UUID roomId, UUID userId);
    @Transactional
    String updateRoom(UUID roomId, UUID userId, RoomUpdateRequest roomUpdateRequest);
    @Transactional
    UUID deleteRoom(UUID roomId, UUID userId);
    @Transactional
    String locateRoom(UUID userId, RoomLocateRequest roomLocateRequest);
}
