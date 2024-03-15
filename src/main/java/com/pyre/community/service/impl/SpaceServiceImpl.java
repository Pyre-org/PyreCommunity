package com.pyre.community.service.impl;

import com.pyre.community.dto.request.SpaceCreateRequest;
import com.pyre.community.dto.request.SpaceLocateRequest;
import com.pyre.community.dto.request.SpaceUpdateRequest;
import com.pyre.community.dto.response.SpaceCreateResponse;
import com.pyre.community.dto.response.SpaceGetListByRoomResponse;
import com.pyre.community.dto.response.SpaceGetResponse;
import com.pyre.community.entity.*;
import com.pyre.community.enumeration.RoomRole;
import com.pyre.community.enumeration.RoomType;
import com.pyre.community.enumeration.SpaceRole;
import com.pyre.community.enumeration.SpaceType;
import com.pyre.community.exception.customexception.CustomException;
import com.pyre.community.exception.customexception.DataNotFoundException;
import com.pyre.community.exception.customexception.PermissionDenyException;
import com.pyre.community.repository.*;
import com.pyre.community.service.SpaceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

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
        Optional<RoomEndUser> roomEndUser = this.roomEndUserRepository.findByRoomAndUserIdAndIsDeleted(room.get(), userId, false);
        if (!roomEndUser.isPresent()) {
            throw new PermissionDenyException("해당 룸에 가입하지 않은 상태입니다.");
        }
        if (!roomEndUser.get().getRole().equals(RoomRole.ROOM_MODE) && !roomEndUser.get().getRole().equals(RoomRole.ROOM_ADMIN)) {
            throw new PermissionDenyException("해당 룸의 모더나 어드민이 아닙니다.");
        }
        if (spaceCreateRequest.type().getKey().equals("SPACE_GENERAL") ||
                spaceCreateRequest.type().getKey().equals("SPACE_GENERAL_CHAT")) {
            throw new CustomException("해당 타입은 생성할 수 없습니다.");
        }
        Space lastSpace = getLastSpace(room.get());
        Space space = Space.builder()
                .room(room.get())
                .title(spaceCreateRequest.title())
                .description(spaceCreateRequest.description())
                .type(spaceCreateRequest.type())
                .role(SpaceRole.SPACEROLE_USER)
                .prevId(lastSpace.getId())
                .build();
        Space savedSpace = this.spaceRepository.save(space);
        lastSpace.updateNext(savedSpace.getId());
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
        Optional<RoomEndUser> roomEndUser = this.roomEndUserRepository.findByRoomAndUserIdAndIsDeleted(room.get(), userId, false);
        if (!roomEndUser.isPresent()) {
            throw new PermissionDenyException("해당 룸에 가입하지 않은 상태입니다.");
        }

        Space firstSpace = getFirstSpace(room.get());
        UUID spaceId = firstSpace.getId();
        List<Space> sortedSpaces = new ArrayList<>();
        while (spaceId != null) {
            Space space = spaceRepository.findById(spaceId).orElseThrow(() -> new DataNotFoundException("해당 스페이스는 존재하지 않습니다."));
            sortedSpaces.add(space);
            spaceId = space.getNextId();
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
        RoomEndUser roomEndUser = roomEndUserRepository.findByRoomAndUserIdAndIsDeleted(room, userId, false).orElseThrow(() -> new PermissionDenyException("해당 룸에 가입하지 않은 상태입니다."));
        if (space.getIsDeleted()) {
            throw new DataNotFoundException("해당 스페이스는 존재하지 않습니다.");
        }
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
        if (space.getIsDeleted()) {
            throw new DataNotFoundException("해당 스페이스는 존재하지 않습니다.");
        }
        Room room = space.getRoom();
        Optional<RoomEndUser> roomEndUser = roomEndUserRepository.findByRoomAndUserIdAndIsDeleted(room, userId, false);
        if (!roomEndUser.isPresent()) {
            throw new PermissionDenyException("해당 룸에 가입하지 않은 상태입니다.");
        }
        RoomEndUser gotRoomEndUser = roomEndUser.get();
        if (!gotRoomEndUser.getRole().equals(RoomRole.ROOM_MODE) && !roomEndUserRepository.findByRoomAndUserIdAndIsDeleted(room, userId, false).get().getRole().equals(RoomRole.ROOM_ADMIN)) {
            throw new PermissionDenyException("해당 룸의 모더나 어드민이 아닙니다.");
        }
        space.updateSpace(spaceUpdateRequest);
        return "스페이스 정보가 수정되었습니다.";
    }
    @Transactional
    @Override
    public String deleteSpace(UUID userId, String spaceId) {
        Space space = spaceRepository.findById(UUID.fromString(spaceId)).orElseThrow(() -> new DataNotFoundException("해당 스페이스는 존재하지 않습니다."));
        if (space.getIsDeleted()) {
            throw new DataNotFoundException("해당 스페이스는 존재하지 않습니다.");
        }
        Room room = space.getRoom();
        Optional<RoomEndUser> roomEndUser = roomEndUserRepository.findByRoomAndUserIdAndIsDeleted(room, userId, false);
        if (!roomEndUser.isPresent()) {
            throw new PermissionDenyException("해당 룸에 가입하지 않은 상태입니다.");
        }
        RoomEndUser gotRoomEndUser = roomEndUser.get();
        if (!gotRoomEndUser.getRole().equals(RoomRole.ROOM_MODE) && !roomEndUserRepository.findByRoomAndUserIdAndIsDeleted(room, userId, false).get().getRole().equals(RoomRole.ROOM_ADMIN)) {
            throw new PermissionDenyException("해당 룸의 모더나 어드민이 아닙니다.");
        }
        if (space.getType().getKey().equals("SPACE_GENERAL") ||
                space.getType().getKey().equals("SPACE_GENERAL_CHAT")) {
            throw new CustomException("해당 스페이스는 삭제할 수 없습니다.");
        }
        deleteSpace(space);

        return "스페이스가 삭제되었습니다.";
    }
    @Transactional
    @Override
    public String locateSpace(UUID userId, SpaceLocateRequest spaceLocateRequest) {
        if (spaceLocateRequest.from().equals(spaceLocateRequest.to())) {
            throw new CustomException("이동하려는 스페이스와 현재 스페이스가 같습니다.");
        }
        Optional<Space> space = this.spaceRepository.findById(spaceLocateRequest.from());
        Optional<Space> toSpace = this.spaceRepository.findById(spaceLocateRequest.to());
        if (!space.isPresent() || !toSpace.isPresent() || space.get().getIsDeleted() || toSpace.get().getIsDeleted()) {
            throw new DataNotFoundException("존재하지 않는 스페이스입니다.");
        }
        Optional<Room> room = this.roomRepository.findById(space.get().getRoom().getId());
        Optional<Room> toRoom = this.roomRepository.findById(toSpace.get().getRoom().getId());
        if (!room.isPresent() || !toRoom.isPresent()) {
            throw new DataNotFoundException("해당 룸은 존재하지 않습니다.");
        }
        Room gotRoom = room.get();
        Room gotToRoom = toRoom.get();
        if (!gotRoom.equals(gotToRoom)) {
            throw new CustomException("해당 스페이스와 이동하려는 스페이스의 룸이 다릅니다.");
        }
        Optional<RoomEndUser> roomEndUser = roomEndUserRepository.findByRoomAndUserIdAndIsDeleted(gotRoom, userId, false);

        if (!roomEndUser.isPresent()) {
            throw new PermissionDenyException("해당 룸에 가입하지 않았습니다.");
        }
        if (!roomEndUser.get().getRole().equals(RoomRole.ROOM_MODE) && !roomEndUser.get().getRole().equals(RoomRole.ROOM_ADMIN)) {
            throw new PermissionDenyException("해당 룸의 모더나 관리자가 아닙니다.");
        }
        if (space.get().getType().equals(SpaceType.SPACE_GENERAL) ||
                space.get().getType().equals(SpaceType.SPACE_GENERAL_CHAT)) {
            throw new CustomException("해당 스페이스는 이동할 수 없습니다.");
        }
        if (toSpace.get().getType().equals(SpaceType.SPACE_GENERAL)) {
            throw new CustomException("해당 스페이스로 이동할 수 없습니다.");
        }
        moveSpace(space.get(), toSpace.get());
        return "스페이스의 위치가 변경되었습니다.";
    }
    @Transactional(readOnly = true)
    @Override
    public Boolean canWriteSpace(UUID userId, String spaceId) {
        Space space = spaceRepository.findById(UUID.fromString(spaceId)).orElseThrow(() -> new DataNotFoundException("해당 스페이스는 존재하지 않습니다."));
        if (space.getIsDeleted()) {
            throw new DataNotFoundException("해당 스페이스는 존재하지 않습니다.");
        }
        if (space.getType().equals(SpaceType.SPACE_CHAT) ||
                space.getType().equals(SpaceType.SPACE_GENERAL_CHAT)) {
            return false;
        }
        Room room = space.getRoom();
        Optional<RoomEndUser> roomEndUser = roomEndUserRepository.findByRoomAndUserIdAndIsDeleted(room, userId, false);
        if (!roomEndUser.isPresent()) {
            throw new PermissionDenyException("해당 룸에 가입하지 않은 상태입니다.");
        }
        RoomEndUser gotRoomEndUser = roomEndUser.get();
        if (gotRoomEndUser.getRole().equals(RoomRole.ROOM_GUEST)) {
            if (!space.getRole().equals(SpaceRole.SPACEROLE_GUEST)) {
                return false;
            } else {
                return true;
            }
        } else if (gotRoomEndUser.getRole().equals(RoomRole.ROOM_USER)) {
            if (space.getRole().equals(SpaceRole.SPACEROLE_USER) || space.getRole().equals(SpaceRole.SPACEROLE_GUEST)) {
                return true;
            } else {
                return false;
            }
        } else {
            return true;
        }
    }
    @Transactional(readOnly = true)
    @Override
    public String getCaptureSpace(String userId, String channelId) {
        Optional<Channel> channel = channelRepository.findById(UUID.fromString(channelId));
        if (!channel.isPresent()) {
            throw new DataNotFoundException("해당 채널은 존재하지 않습니다.");
        }
        Optional<ChannelEndUser> channelEndUser = channelEndUserRepository.findByChannelAndUserId(channel.get(), UUID.fromString(userId));
        if (!channelEndUser.isPresent()) {
            throw new PermissionDenyException("해당 채널에 가입하지 않은 상태입니다.");
        }
        if (channelEndUser.get().getSubscribe().equals(false)) {
            throw new PermissionDenyException("해당 채널을 구독하지 않은 상태입니다.");
        }


        Space space = channel.get().getRooms().stream().filter(room -> room.getType().equals(RoomType.ROOM_CAPTURE)).findAny().orElse(null).getSpaces().get(0);
        if (space.getIsDeleted()) {
            throw new DataNotFoundException("해당 스페이스는 존재하지 않습니다.");
        }
        return space.getId().toString();
    }
    @Transactional(readOnly = true)
    @Override
    public List<UUID> canReadSpaces(UUID userId) {
        List<RoomEndUser> roomEndUsers = roomEndUserRepository.findAllByUserIdAndIsDeleted(userId, false);
        List<UUID> spaceIds = new ArrayList<>();
        for (RoomEndUser roomEndUser : roomEndUsers) {
            Room room = roomEndUser.getRoom();
            switch (roomEndUser.getRole()) {
                case ROOM_ADMIN:
                    List<Space> spaces = spaceRepository.findAllByRoomAndIsDeleted(room, false);
                    for (Space space : spaces) {
                        spaceIds.add(space.getId());
                    }
                    break;
                case ROOM_MODE:
                    List<Space> spaces1 = spaceRepository.findAllByRoomAndIsDeleted(room, false);
                    for (Space space : spaces1) {
                        spaceIds.add(space.getId());
                    }
                    break;
                case ROOM_USER:
                    List<Space> spaces2 = spaceRepository.findAllByRoomAndIsDeleted(room, false);
                    for (Space space : spaces2) {
                        if (space.getRole().equals(SpaceRole.SPACEROLE_USER) || space.getRole().equals(SpaceRole.SPACEROLE_GUEST)) {
                            spaceIds.add(space.getId());
                        }
                    }
                    break;
                default:
                    List<Space> spaces3 = spaceRepository.findAllByRoomAndIsDeleted(room, false);
                    for (Space space : spaces3) {
                        if (space.getRole().equals(SpaceRole.SPACEROLE_GUEST)) {
                            spaceIds.add(space.getId());
                        }
                }
            }
        }
        return spaceIds;
    }

    private Space getLastSpace(Room room) {
        List<Space> spaces = spaceRepository.findAllByRoomAndIsDeleted(room, false);
        Space lastSpace = spaces.stream().filter(space -> Objects.isNull(space.getNextId())).findAny().orElse(null);
        return lastSpace;
    }
    private Space getFirstSpace(Room room) {
        List<Space> spaces = spaceRepository.findAllByRoomAndIsDeleted(room, false);
        Space firstSpace = spaces.stream().filter(space -> Objects.isNull(space.getPrevId())).findAny().orElse(null);
        return firstSpace;
    }
    private void deleteSpace(Space space) {

        Optional<Space> tempPrev;
        Optional<Space> tempNext;
        if (space.getPrevId() != null) {
            tempPrev = spaceRepository.findById(space.getPrevId());
            if (space.getNextId() != null) {
                tempNext = spaceRepository.findById(space.getNextId());
                if ( tempPrev.isPresent() ) {
                    if (tempNext.isPresent()) {
                        tempPrev.get().updateNext(tempNext.get().getId());
                    }
                }
            } else {
                tempPrev.get().updateNext(null);
            }
        }
        if (space.getNextId() != null) {
            tempNext = spaceRepository.findById(space.getNextId());
            if (space.getPrevId() != null) {
                tempPrev = spaceRepository.findById(space.getPrevId());
                if ( tempNext.isPresent() ) {
                    if (tempPrev.isPresent()) {
                        tempNext.get().updatePrev(tempPrev.get().getId());
                    }
                }
            } else {
                tempNext.get().updatePrev(null);
            }
        }
        space.updateIsDeleted(true);
        space.updatePrev(null);
        space.updateNext(null);

    }
    public void moveSpace(Space space, Space toSpace) {
        UUID tempPrevId = space.getPrevId();
        UUID tempNextId = space.getNextId();

        UUID tempToNextId = toSpace.getNextId();
        Optional<Space> tempPrev;
        Optional<Space> tempNext;
        if (tempPrevId != null ? tempPrevId.equals(toSpace.getId()) : false) {
            throw new CustomException("이동하려는 자리를 이미 차지하고 있습니다.");
        }
        if (tempNextId != null ? tempNextId.equals(toSpace.getId()) : false) {
            space.updateNext(tempToNextId);
            space.updatePrev(toSpace.getId());
            toSpace.updateNext(space.getId());
            toSpace.updatePrev(tempPrevId);
            if (tempPrevId != null) {
                tempPrev = spaceRepository.findById(tempPrevId);
                if ( tempPrev.isPresent() ) {
                    tempPrev.get().updateNext(toSpace.getId());
                }
            }
            if (tempToNextId != null) {
                tempNext = spaceRepository.findById(tempToNextId);
                if ( tempNext.isPresent() ) {
                    tempNext.get().updatePrev(space.getId());
                }
            }
        } else {
            if (tempPrevId != null) {
                tempPrev = spaceRepository.findById(tempPrevId);
                if ( tempPrev.isPresent() ) {
                    tempPrev.get().updateNext(tempNextId);
                }
            }
            if (tempNextId != null) {
                tempNext = spaceRepository.findById(tempNextId);
                if ( tempNext.isPresent() ) {
                    tempNext.get().updatePrev(tempPrevId);
                }
            }
            toSpace.updateNext(space.getId());
            if (tempToNextId != null) {
                tempNext = spaceRepository.findById(tempToNextId);
                if ( tempNext.isPresent() ) {
                    tempNext.get().updatePrev(space.getId());
                }
            }
            space.updatePrev(toSpace.getId());
            space.updateNext(tempToNextId);
        }

    }
}
