package com.pyre.community.service.impl;

import com.pyre.community.dto.response.search.RoomSearchListResponse;
import com.pyre.community.dto.response.search.SpaceSearchListResponse;
import com.pyre.community.entity.Room;
import com.pyre.community.entity.Space;
import com.pyre.community.enumeration.RoomType;
import com.pyre.community.repository.RoomEndUserRepository;
import com.pyre.community.repository.RoomRepository;
import com.pyre.community.repository.SpaceRepository;
import com.pyre.community.service.SearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {
    private final RoomRepository roomRepository;
    private final SpaceRepository spaceRepository;
    private final RoomEndUserRepository roomEndUserRepository;
    @Transactional(readOnly = true)
    @Override
    public RoomSearchListResponse searchRooms(UUID userId, String keyword, int page, int size) {
        Sort sort = Sort.by(Sort.Direction.ASC, "title");
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Room> rooms = roomRepository.findAllByTitleSearch(keyword, RoomType.ROOM_PUBLIC, userId, pageable);
        RoomSearchListResponse response = RoomSearchListResponse.makeDto(rooms);
        return response;
    }
    @Transactional(readOnly = true)
    @Override
    public SpaceSearchListResponse searchSpaces(UUID userId, String keyword, int page, int size) {
        Sort sort = Sort.by(Sort.Direction.ASC, "title");
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Space> spaces = spaceRepository.findAllByTitleSearch(keyword, RoomType.ROOM_PUBLIC, pageable);
        SpaceSearchListResponse response = SpaceSearchListResponse.makeDto(spaces);
        return response;
    }

}
