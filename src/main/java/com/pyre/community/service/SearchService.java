package com.pyre.community.service;

import com.pyre.community.dto.response.search.RoomSearchListResponse;
import com.pyre.community.dto.response.search.SpaceSearchListResponse;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

public interface SearchService {
    @Transactional(readOnly = true)
    RoomSearchListResponse searchRooms(UUID userId, String keyword, int page, int size);
    @Transactional(readOnly = true)
    SpaceSearchListResponse searchSpaces(UUID userId, String keyword, int page, int size);
}
