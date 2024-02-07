package com.pyre.community.service.impl;

import com.pyre.community.dto.request.SpaceCreateRequest;
import com.pyre.community.dto.response.SpaceCreateResponse;
import com.pyre.community.entity.ChannelEndUser;
import com.pyre.community.entity.Room;
import com.pyre.community.entity.RoomEndUser;
import com.pyre.community.entity.Space;
import com.pyre.community.enumeration.RoomRole;
import com.pyre.community.exception.customexception.DataNotFoundException;
import com.pyre.community.exception.customexception.PermissionDenyException;
import com.pyre.community.repository.*;
import com.pyre.community.service.SpaceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class SpaceServiceImpl implements SpaceService {
    private final SpaceRepository spaceRepository;
    private final ChannelRepository channelRepository;
    private final ChannelEndUserRepository channelEndUserRepository;
    private final RoomRepository roomRepository;
    private final RoomEndUserRepository roomEndUserRepository;
    @Transactional
    @Override
    public SpaceCreateResponse createSpace(SpaceCreateRequest spaceCreateRequest, UUID userId) {
        Optional<Room> room = this.roomRepository.findById(spaceCreateRequest.roomId());
        if (!room.isPresent()) {
            throw new DataNotFoundException("해당 룸은 존재하지 않습니다.");
        }
        Optional<RoomEndUser> roomEndUser = this.roomEndUserRepository.findByRoomAndUserId(room.get(), userId);
        if (!roomEndUser.isPresent()) {
            throw new PermissionDenyException("해당 룸에 가입하지 않은 상태입니다.");
        }
        if (!roomEndUser.get().getRole().equals(RoomRole.ROOM_MODE) && !roomEndUser.get().getRole().equals(RoomRole.ROOM_ADMIN)) {
            throw new PermissionDenyException("해당 룸의 모더나 어드민이 아닙니다.");
        }

        return null;
    }
    private Space getLastSpace(Room room) {
        return null;
    }

}
