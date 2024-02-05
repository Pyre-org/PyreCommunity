package com.pyre.community.service;

import com.pyre.community.dto.request.RoomCreateRequest;
import com.pyre.community.dto.response.RoomCreateResponse;
import com.pyre.community.dto.response.RoomGetResponse;
import com.pyre.community.dto.response.RoomJoinResponse;
import com.pyre.community.dto.response.RoomListByChannelResponse;
import jakarta.transaction.Transactional;

import java.util.UUID;

public interface RoomService {
    @Transactional
    RoomCreateResponse createRoom(UUID userId, RoomCreateRequest roomCreateRequest);
    @Transactional
    RoomGetResponse getRoom(UUID id, UUID userId);
    @Transactional
    RoomListByChannelResponse listByChannelAndKeywordAndType(UUID channelId, String keyword, String type);
    @Transactional
    RoomListByChannelResponse listByChannelAndKeywordAndUserId(UUID channelId, String keyword, UUID userId);
    @Transactional
    RoomListByChannelResponse listByChannelAndUserIdByIndexing(UUID channelId, UUID userId);
    @Transactional
    RoomJoinResponse joinRoom(UUID roomId, UUID userId, UUID channelId);
}
