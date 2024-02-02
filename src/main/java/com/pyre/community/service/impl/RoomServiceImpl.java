package com.pyre.community.service.impl;

import com.pyre.community.repository.RoomEndUserRepository;
import com.pyre.community.repository.RoomRepository;
import com.pyre.community.service.RoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoomServiceImpl implements RoomService {
    private final RoomRepository roomRepository;
    private final RoomEndUserRepository roomEndUserRepository;

}
