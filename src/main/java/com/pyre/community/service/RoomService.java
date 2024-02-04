package com.pyre.community.service;

import com.pyre.community.dto.request.RoomCreateRequest;
import com.pyre.community.dto.response.RoomCreateResponse;
import jakarta.transaction.Transactional;

import java.util.UUID;

public interface RoomService {
    @Transactional
    RoomCreateResponse createRoom(UUID userId, RoomCreateRequest roomCreateRequest);
}
