package com.pyre.community.service.impl;

import com.pyre.community.dto.request.SpaceCreateRequest;
import com.pyre.community.dto.request.SpaceUpdateRequest;
import com.pyre.community.dto.response.SpaceCreateResponse;
import com.pyre.community.dto.response.SpaceGetListByRoomResponse;
import com.pyre.community.dto.response.SpaceGetResponse;
import com.pyre.community.entity.ChannelEndUser;
import com.pyre.community.entity.Room;
import com.pyre.community.entity.RoomEndUser;
import com.pyre.community.entity.Space;
import com.pyre.community.enumeration.RoomRole;
import com.pyre.community.enumeration.SpaceRole;
import com.pyre.community.exception.customexception.DataNotFoundException;
import com.pyre.community.exception.customexception.PermissionDenyException;
import com.pyre.community.repository.*;
import com.pyre.community.service.SpaceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
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
        Space lastSpace = getLastSpace(room.get());
        Space space = Space.builder()
                .room(room.get())
                .title(spaceCreateRequest.title())
                .description(spaceCreateRequest.description())
                .type(spaceCreateRequest.type())
                .role(SpaceRole.SPACEROLE_USER)
                .prev(lastSpace)
                .build();
        lastSpace.updateNext(space);
        Space savedSpace = this.spaceRepository.save(space);
        SpaceCreateResponse spaceCreateResponse = SpaceCreateResponse.makeDto(savedSpace);
        return spaceCreateResponse;
    }
    @Transactional(readOnly = true)
    @Override
    public SpaceGetListByRoomResponse getSpaceListByRoom(UUID userId, String roomId) {
        Optional<Room> room = this.roomRepository.findById(UUID.fromString(roomId));
        if (!room.isPresent()) {
            throw new DataNotFoundException("해당 룸은 존재하지 않습니다.");
        }
        Optional<RoomEndUser> roomEndUser = this.roomEndUserRepository.findByRoomAndUserId(room.get(), userId);
        if (!roomEndUser.isPresent()) {
            throw new PermissionDenyException("해당 룸에 가입하지 않은 상태입니다.");
        }

        Space firstSpace = getFirstSpace(room.get());
        List<Space> sortedSpaces = new ArrayList<>();
        while (firstSpace != null) {
            sortedSpaces.add(firstSpace);
            firstSpace = firstSpace.getNext();
        }
        List<Space> filteredSpaces;
        SpaceGetListByRoomResponse spaceGetListByRoomResponse;
        RoomRole role = roomEndUser.get().getRole();
        switch (role) {
            case ROOM_ADMIN:
                filteredSpaces = sortedSpaces.stream().filter(
                        space -> space.getRole().equals(SpaceRole.SPACEROLE_USER) ||
                                space.getRole().equals(SpaceRole.SPACEROLE_GUEST) ||
                                space.getRole().equals(SpaceRole.SPACEROLE_MODE)
                ).toList();
                spaceGetListByRoomResponse = SpaceGetListByRoomResponse.makeDto(filteredSpaces);
                break;
            case ROOM_MODE:
                filteredSpaces = sortedSpaces.stream().filter(
                        space -> space.getRole().equals(SpaceRole.SPACEROLE_USER) ||
                                space.getRole().equals(SpaceRole.SPACEROLE_GUEST) ||
                                space.getRole().equals(SpaceRole.SPACEROLE_MODE)
                        ).toList();
                 spaceGetListByRoomResponse = SpaceGetListByRoomResponse.makeDto(filteredSpaces);
                break;
            case ROOM_USER:
                filteredSpaces = sortedSpaces.stream().filter(space -> space.getRole().equals(SpaceRole.SPACEROLE_USER) || space.getRole().equals(SpaceRole.SPACEROLE_GUEST) ).toList();
                spaceGetListByRoomResponse = SpaceGetListByRoomResponse.makeDto(filteredSpaces);
                break;
            default:
                filteredSpaces = sortedSpaces.stream().filter(space -> space.getRole().equals(SpaceRole.SPACEROLE_GUEST)).toList();
                spaceGetListByRoomResponse = SpaceGetListByRoomResponse.makeDto(filteredSpaces);

        }
        return spaceGetListByRoomResponse;
    }

    @Override
    public SpaceGetResponse getSpace(UUID userId, String spaceId) {
        Space space = spaceRepository.findById(UUID.fromString(spaceId)).orElseThrow(() -> new DataNotFoundException("해당 스페이스는 존재하지 않습니다."));
        Room room = space.getRoom();
        RoomEndUser roomEndUser = roomEndUserRepository.findByRoomAndUserId(room, userId).orElseThrow(() -> new PermissionDenyException("해당 룸에 가입하지 않은 상태입니다."));

        if (space.getRole().equals(SpaceRole.SPACEROLE_GUEST)) {
            return SpaceGetResponse.makeDto(space);
        } else if (space.getRole().equals(SpaceRole.SPACEROLE_USER)) {
            if (roomEndUser.getRole().equals(RoomRole.ROOM_GUEST)) {
                throw new PermissionDenyException("해당 스페이스에 접근할 권한이 없습니다.");
            }
            return SpaceGetResponse.makeDto(space);
        } else {
            if (roomEndUser.getRole().equals(RoomRole.ROOM_GUEST) || roomEndUser.getRole().equals(RoomRole.ROOM_USER)) {
                throw new PermissionDenyException("해당 스페이스에 접근할 권한이 없습니다.");
            }
            return SpaceGetResponse.makeDto(space);
        }
    }
    @Transactional
    @Override
    public String updateSpace(UUID userId, String spaceId, SpaceUpdateRequest spaceUpdateRequest) {
        Space space = spaceRepository.findById(UUID.fromString(spaceId)).orElseThrow(() -> new DataNotFoundException("해당 스페이스는 존재하지 않습니다."));
        Room room = space.getRoom();
        Optional<RoomEndUser> roomEndUser = roomEndUserRepository.findByRoomAndUserId(room, userId);
        if (!roomEndUser.isPresent()) {
            throw new PermissionDenyException("해당 룸에 가입하지 않은 상태입니다.");
        }
        RoomEndUser gotRoomEndUser = roomEndUser.get();
        if (!gotRoomEndUser.getRole().equals(RoomRole.ROOM_MODE) && !roomEndUserRepository.findByRoomAndUserId(room, userId).get().getRole().equals(RoomRole.ROOM_ADMIN)) {
            throw new PermissionDenyException("해당 룸의 모더나 어드민이 아닙니다.");
        }
        space.updateSpace(spaceUpdateRequest);
        return "스페이스 정보가 수정되었습니다.";
    }
    @Transactional
    @Override
    public String deleteSpace(UUID userId, String spaceId) {
        Space space = spaceRepository.findById(UUID.fromString(spaceId)).orElseThrow(() -> new DataNotFoundException("해당 스페이스는 존재하지 않습니다."));
        Room room = space.getRoom();
        Optional<RoomEndUser> roomEndUser = roomEndUserRepository.findByRoomAndUserId(room, userId);
        if (!roomEndUser.isPresent()) {
            throw new PermissionDenyException("해당 룸에 가입하지 않은 상태입니다.");
        }
        RoomEndUser gotRoomEndUser = roomEndUser.get();
        if (!gotRoomEndUser.getRole().equals(RoomRole.ROOM_MODE) && !roomEndUserRepository.findByRoomAndUserId(room, userId).get().getRole().equals(RoomRole.ROOM_ADMIN)) {
            throw new PermissionDenyException("해당 룸의 모더나 어드민이 아닙니다.");
        }
        Space prevSpace = space.getPrev();
        Space nextSpace = space.getNext();
        if (prevSpace != null) {
            prevSpace.updateNext(nextSpace);
        }
        if (nextSpace != null) {
            nextSpace.updatePrev(prevSpace);
        }

        spaceRepository.delete(space);
        return "스페이스가 삭제되었습니다.";
    }

    private Space getLastSpace(Room room) {
        List<Space> spaces = spaceRepository.findAllByRoom(room);
        Space lastSpace = spaces.stream().map(Space::getNext).filter(next -> next == null).findFirst().orElse(null);
        return lastSpace;
    }
    private Space getFirstSpace(Room room) {
        List<Space> spaces = spaceRepository.findAllByRoom(room);
        Space firstSpace = spaces.stream().map(Space::getPrev).filter(prev -> prev == null).findFirst().orElse(null);
        return firstSpace;
    }

}
