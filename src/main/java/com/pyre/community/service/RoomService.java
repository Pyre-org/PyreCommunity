package com.pyre.community.service;

import com.pyre.community.dto.request.RoomCreateRequest;
import com.pyre.community.dto.response.*;
import org.springframework.transaction.annotation.Transactional;


import java.util.UUID;

public interface RoomService {
    @Transactional
    RoomCreateResponse createRoom(UUID userId, RoomCreateRequest roomCreateRequest);
    @Transactional
    RoomGetDetailResponse getRoom(UUID id, UUID userId);
    @Transactional
    RoomListByChannelResponse listByChannelAndKeywordAndType(UUID channelId, String keyword, String type);
    @Transactional
    RoomListByChannelResponse listByChannelAndKeywordAndUserId(UUID channelId, String keyword, UUID userId);
    @Transactional
    RoomListByChannelResponse listByChannelAndUserIdByIndexing(UUID channelId, UUID userId);
    @Transactional
    RoomJoinResponse joinRoom(UUID roomId, UUID userId, UUID channelId);
}
