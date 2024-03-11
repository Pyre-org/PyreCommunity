package com.pyre.community.service.impl;

import com.pyre.community.dto.request.RoomCreateRequest;
import com.pyre.community.dto.request.RoomEndUserRoleUpdateRequest;
import com.pyre.community.dto.request.RoomLocateRequest;
import com.pyre.community.dto.request.RoomUpdateRequest;
import com.pyre.community.dto.response.*;
import com.pyre.community.entity.*;
import com.pyre.community.enumeration.RoomRole;
import com.pyre.community.enumeration.RoomType;
import com.pyre.community.enumeration.SpaceRole;
import com.pyre.community.enumeration.SpaceType;
import com.pyre.community.exception.customexception.CustomException;
import com.pyre.community.exception.customexception.DataNotFoundException;
import com.pyre.community.exception.customexception.DuplicateException;
import com.pyre.community.exception.customexception.PermissionDenyException;
import com.pyre.community.repository.*;
import com.pyre.community.service.RoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.enums.Enum;
import org.apache.tomcat.websocket.AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoomServiceImpl implements RoomService {
    private final RoomRepository roomRepository;
    private final RoomEndUserRepository roomEndUserRepository;
    private final ChannelEndUserRepository channelEndUserRepository;
    private final ChannelRepository channelRepository;
    private final SpaceRepository spaceRepository;
    @Transactional
    @Override
    public RoomCreateResponse createRoom(UUID userId, RoomCreateRequest roomCreateRequest) {
        Optional<Channel> channel = this.channelRepository.findById(roomCreateRequest.channelId());
        if (!channel.isPresent()) {
            throw new DataNotFoundException("존재하지 않는 채널에서 방을 만들 수 없습니다.");
        }
        Channel gotChannel = channel.get();
        Optional<ChannelEndUser> channelEndUser = this.channelEndUserRepository.findByChannelAndUserId(gotChannel, userId);
        if (!channelEndUser.isPresent()) {
            throw new DataNotFoundException("해당 채널이 없거나 해당 채널에 가입하지 않았습니다.");
        }
        if (channelEndUser.get().getBan().equals(true)) {
            throw new CustomException("차단 당한 채널에서 룸을 생성할 수 없습니다.");
        }
        if (this.roomRepository.existsByChannelAndTitle(gotChannel, roomCreateRequest.title())) {
            throw new DuplicateException("해당 채널에 이미 " +  roomCreateRequest.title() + " 이름을 가진 룸이 있습니다.");
        }
        Room savedRoom = createRoomAndSpace(roomCreateRequest, gotChannel, userId);
        RoomCreateResponse roomCreateResponse = RoomCreateResponse.makeDto(savedRoom);
        return roomCreateResponse;
    }
    @Transactional(readOnly = true)
    @Override
    public RoomGetResponse getRoom(UUID id, UUID userId) {
        Optional<Room> room = this.roomRepository.findById(id);
        if (!room.isPresent()) {
            throw new DataNotFoundException("존재하지 않는 룸 입니다.");
        }
        Room gotRoom = room.get();
        if (
                gotRoom.getType().equals(RoomType.ROOM_PUBLIC) ||
                        gotRoom.getType().equals(RoomType.ROOM_OPEN) ||
                        gotRoom.getType().equals(RoomType.ROOM_CAPTURE) ||
                        gotRoom.getType().equals(RoomType.ROOM_GLOBAL)
        ) {
            RoomGetResponse roomGetResponse = RoomGetResponse.makeDto(gotRoom);
            return roomGetResponse;
        } else {
            if (!this.roomEndUserRepository.existsByRoomAndUserIdAndIsDeleted(room.get(), userId, false)) {
                throw new PermissionDenyException("해당 룸에 가입하지 않은 상태입니다.");
            }
            RoomGetResponse roomGetResponse = RoomGetResponse.makeDto(gotRoom);
            return roomGetResponse;
        }
    }
    @Transactional(readOnly = true)
    @Override
    public RoomListByChannelResponse listByChannelAndKeywordAndType(UUID channelId, String keyword, String type) {
        Optional<Channel> channel = this.channelRepository.findById(channelId);
        if (!channel.isPresent()) {
            throw new DataNotFoundException("존재하지 않는 채널입니다.");
        }
        if (!type.equals("ROOM_PUBLIC") && !type.equals("ROOM_OPEN")) {
            keyword = "ROOM_PUBLIC";
        }
        List<Room> rooms = this.roomRepository.findAllByChannelAndTypeAndTitleContainingOrderByTitle(channel.get(), RoomType.valueOf(type), keyword);
        return RoomListByChannelResponse.makeDto(rooms);
    }
    @Transactional(readOnly = true)
    @Override
    public RoomListByChannelResponse listByChannelAndKeywordAndUserId(UUID channelId, String keyword, UUID userId) {
        Optional<Channel> channel = this.channelRepository.findById(channelId);
        if (!channel.isPresent()) {
            throw new DataNotFoundException("존재하지 않는 채널입니다.");
        }
        if (!this.channelEndUserRepository.existsByChannelAndUserId(channel.get(), userId)) {
            throw new DataNotFoundException("해당 채널에 가입하지 않았습니다.");
        }
        List<RoomEndUser> roomEndUsers = this.roomEndUserRepository.findAllByChannelAndUserIdAndIsDeleted(channel.get(), userId, false);
        List<Room> rooms = new ArrayList<>();
        for (RoomEndUser r : roomEndUsers) {
            if (!r.getRoom().getType().equals(RoomType.ROOM_GLOBAL) && !r.getRoom().getType().equals(RoomType.ROOM_CAPTURE)) {
                rooms.add(r.getRoom());
            }
        }
        List<Room> sortedRoom = rooms.stream().sorted(Comparator.comparing(Room::getTitle)).toList();
        return RoomListByChannelResponse.makeDto(sortedRoom);
    }
    @Transactional(readOnly = true)
    @Override
    public RoomGetDetailListResponse listByChannelAndUserIdByIndexing(UUID channelId, UUID userId) {
        Optional<Channel> channel = this.channelRepository.findById(channelId);
        if (!channel.isPresent()) {
            throw new DataNotFoundException("존재하지 않는 채널입니다.");
        }
        if (!this.channelEndUserRepository.existsByChannelAndUserId(channel.get(), userId)) {
            throw new DataNotFoundException("해당 채널에 가입하지 않았습니다.");
        }
        List<RoomEndUser> roomEndUsers = this.roomEndUserRepository.findAllByChannelAndUserIdAndIsDeleted(channel.get(), userId, false);
        RoomEndUser roomEndUser = getFirstRoomEndUser(roomEndUsers);
        List<RoomEndUser> sortedRoomEndUsers = new ArrayList<>();
        if (roomEndUser == null) {
            RoomGetDetailListResponse.makeDto(new ArrayList<>());
        }
        UUID nextId = roomEndUser.getId();
        while (nextId != null) {
            RoomEndUser gotRoomEndUser = roomEndUserRepository.findById(nextId).get();
            sortedRoomEndUsers.add(gotRoomEndUser);
            nextId = gotRoomEndUser.getNextId();
        }
        List<Room> rooms = new ArrayList<>();
        for (RoomEndUser r : sortedRoomEndUsers) {
            rooms.add(r.getRoom());
        }
        List<RoomGetDetailResponse> roomGetDetailResponses = new ArrayList<>();
        for (Room r : rooms) {
            List<Space> sortedSpaces = new ArrayList<>();
            Optional<RoomEndUser> roomEndUser2 = roomEndUserRepository.findByRoomAndUserIdAndIsDeleted(r, userId, false);
            RoomEndUser gotRoomEndUser = roomEndUser2.get();
            List<Space> spaces = r.getSpaces().stream().filter(space -> {
                if (gotRoomEndUser.getRole().equals(RoomRole.ROOM_USER)) {
                    return space.getRole().equals(SpaceRole.SPACEROLE_USER) || space.getRole().equals(SpaceRole.SPACEROLE_GUEST);
                }
                if (gotRoomEndUser.getRole().equals(RoomRole.ROOM_GUEST)) {
                    return space.getRole().equals(SpaceRole.SPACEROLE_GUEST);
                }
                return true;
            }).collect(Collectors.toList());
            Space firstSpace = getFirstSpace(spaces);
            UUID spaceId = firstSpace.getId();
            while (spaceId != null) {
                Space space = spaceRepository.findById(spaceId).get();
                sortedSpaces.add(space);
                spaceId = space.getNextId();
            }
            roomGetDetailResponses.add(RoomGetDetailResponse.makeDto(r, sortedSpaces));
        }
        return RoomGetDetailListResponse.makeDto(roomGetDetailResponses);
    }
    @Transactional
    @Override
    public RoomJoinResponse joinRoom(UUID roomId, UUID userId, UUID channelId) {
        Optional<Channel> channel = this.channelRepository.findById(channelId);

        if (!channel.isPresent()) {
            throw new DataNotFoundException("존재하지 않는 채널입니다.");
        }
        Optional<ChannelEndUser> channelEndUser = channelEndUserRepository.findByChannelAndUserId(channel.get(), userId);
        if (!channelEndUser.isPresent()) {
            throw new DataNotFoundException("해당 채널이 없거나 해당 채널에 가입하지 않았습니다.");
        }
        if (channelEndUser.get().getBan().equals(true)) {
            throw new CustomException("차단 당한 채널에서 룸을 생성할 수 없습니다.");
        }
        Optional<Room> room = this.roomRepository.findById(roomId);
        if (!room.isPresent()) {
            throw new DataNotFoundException("존재하지 않는 룸입니다.");
        }
        if (this.roomEndUserRepository.existsByRoomAndUserIdAndIsDeleted(room.get(), userId, false)) {
            throw new DuplicateException("이미 가입한 룸입니다.");
        }
        Room gotRoom = room.get();
        List<RoomEndUser> roomEndUsers = this.roomEndUserRepository.findAllByChannelAndUserIdAndIsDeleted(channel.get(), userId, false);
        RoomEndUser lastRoomEndUser = getLastRoomEndUser(roomEndUsers);
        RoomEndUser savedRoomEndUser;
        if (gotRoom.getType().equals(RoomType.ROOM_PUBLIC)) {
            RoomEndUser roomEndUser = RoomEndUser.builder()
                    .userId(userId)
                    .room(gotRoom)
                    .owner(false)
                    .prevId(lastRoomEndUser.getId())
                    .channelEndUser(channelEndUser.get())
                    .role(RoomRole.ROOM_GUEST)
                    .channel(channel.get())
                    .build();
            savedRoomEndUser = this.roomEndUserRepository.save(roomEndUser);
            lastRoomEndUser.updateNext(savedRoomEndUser.getId());

        } else if (!gotRoom.getType().equals(RoomType.ROOM_OPEN)) {
            throw new PermissionDenyException("해당 룸은 초대장을 통해서 가입할 수 있습니다.");
        } else {
            RoomEndUser roomEndUser = RoomEndUser.builder()
                    .userId(userId)
                    .room(gotRoom)
                    .owner(false)
                    .prevId(lastRoomEndUser.getId())
                    .channelEndUser(channelEndUser.get())
                    .role(RoomRole.ROOM_USER)
                    .channel(channel.get())
                    .build();
            savedRoomEndUser = this.roomEndUserRepository.save(roomEndUser);
            lastRoomEndUser.updateNext(savedRoomEndUser.getId());
        }
        RoomJoinResponse roomJoinResponse = RoomJoinResponse.makeDto(savedRoomEndUser);

        return roomJoinResponse;
    }
    @Transactional
    @Override
    public UUID leaveRoom(UUID roomId, UUID userId) {
        Optional<Room> room = this.roomRepository.findById(roomId);
        if (!room.isPresent()) {
            throw new DataNotFoundException("존재하지 않는 룸입니다.");
        }
        Optional<RoomEndUser> roomEndUser = roomEndUserRepository.findByRoomAndUserIdAndIsDeleted(room.get(), userId, false);
        if (!roomEndUser.isPresent()) {
            throw new PermissionDenyException("해당 룸에 가입하지 않았습니다.");
        }
        RoomEndUser gotRoomEndUser = roomEndUser.get();
        if (gotRoomEndUser.getRole().equals(RoomRole.ROOM_ADMIN)) {
            throw new PermissionDenyException("룸의 관리자는 룸을 탈퇴할 수 없습니다.");
        }
        if (gotRoomEndUser.getRoom().getType().equals(RoomType.ROOM_CAPTURE) ||
                gotRoomEndUser.getRoom().getType().equals(RoomType.ROOM_GLOBAL)) {
            throw new PermissionDenyException("공용 룸 또는 캡처 룸은 나갈 수 없습니다.");
        }
        deleteRoomEndUser(gotRoomEndUser);
//        gotRoomEndUser.deleteEndUser();

        // global 룸 아이디 반환
        return room.get().getChannel().getId();
    }
    @Transactional
    @Override
    public String updateRoom(UUID roomId, UUID userId, RoomUpdateRequest roomUpdateRequest) {
        Optional<Room> room = this.roomRepository.findById(roomId);
        if (!room.isPresent()) {
            throw new DataNotFoundException("존재하지 않는 룸입니다.");
        }
        Optional<RoomEndUser> roomEndUser = roomEndUserRepository.findByRoomAndUserIdAndIsDeleted(room.get(), userId, false);
        if (!roomEndUser.isPresent()) {
            throw new PermissionDenyException("해당 룸에 가입하지 않았습니다.");
        }
        Room gotRoom = room.get();
        if (!roomEndUser.get().getRole().equals(RoomRole.ROOM_MODE) && !roomEndUser.get().getRole().equals(RoomRole.ROOM_ADMIN)) {
            throw new PermissionDenyException("해당 룸의 관리자나 모더가 아닙니다.");
        }
        gotRoom.updateRoom(roomUpdateRequest);

        return "룸이 수정되었습니다.";
    }
    @Transactional
    @Override
    public UUID deleteRoom(UUID roomId, UUID userId) {
        Optional<Room> room = this.roomRepository.findById(roomId);
        if (!room.isPresent()) {
            throw new DataNotFoundException("존재하지 않는 룸입니다.");
        }
        Optional<RoomEndUser> roomEndUser = roomEndUserRepository.findByRoomAndUserIdAndIsDeleted(room.get(), userId, false);
        if (!roomEndUser.isPresent()) {
            throw new PermissionDenyException("해당 룸에 가입하지 않았습니다.");
        }
        Room gotRoom = room.get();
        if (!roomEndUser.get().getOwner().equals(true)) {
            throw new PermissionDenyException("해당 룸의 소유자가 아닙니다.");
        }
        List<RoomEndUser> roomEndUsers = this.roomEndUserRepository.findAllByRoomAndIsDeleted(gotRoom, false);
        for (RoomEndUser r : roomEndUsers) {
            deleteRoomEndUser(r);
        }
        UUID channelUUID = room.get().getChannel().getId();

        roomRepository.delete(gotRoom);
        return channelUUID;
    }
    @Transactional
    @Override
    public String locateRoom(UUID userId, RoomLocateRequest roomLocateRequest) {
        if (roomLocateRequest.from().equals(roomLocateRequest.to())) {
            throw new CustomException("이동하려는 룸과 현재 룸이 같습니다.");
        }

        Optional<Room> room = this.roomRepository.findById(roomLocateRequest.from());
        Optional<Room> toRoom = this.roomRepository.findById(roomLocateRequest.to());
        if (!room.isPresent() || !toRoom.isPresent()) {
            throw new DataNotFoundException("존재하지 않는 룸입니다.");
        }
        if (room.get().getType().equals(RoomType.ROOM_GLOBAL) ||
                room.get().getType().equals(RoomType.ROOM_CAPTURE) ||
                toRoom.get().getType().equals(RoomType.ROOM_GLOBAL) ||
                toRoom.get().getType().equals(RoomType.ROOM_CAPTURE)) {
            throw new PermissionDenyException("공용 룸 또는 캡처 룸은 이동할 수 없습니다.");
        }
        Optional<RoomEndUser> roomEndUser = roomEndUserRepository.findByRoomAndUserIdAndIsDeleted(room.get(), userId, false);
        Optional<RoomEndUser> toRoomEndUser = roomEndUserRepository.findByRoomAndUserIdAndIsDeleted(toRoom.get(), userId, false);
        if (!roomEndUser.isPresent() || !toRoomEndUser.isPresent() || roomEndUser.get().getIsDeleted() || toRoomEndUser.get().getIsDeleted()) {
            throw new PermissionDenyException("해당 룸에 가입하지 않았습니다.");
        }
        Channel gotChannel = room.get().getChannel();
        if (!gotChannel.equals(toRoom.get().getChannel())) {
            throw new CustomException("해당 룸과 이동하려는 룸의 채널이 다릅니다.");
        }
        RoomEndUser gotRoomEndUser = roomEndUser.get();
        RoomEndUser gotToRoomEndUser = toRoomEndUser.get();
        locateRoomEndUser(gotRoomEndUser, gotToRoomEndUser);
        return "룸의 위치가 변경되었습니다.";
    }
    @Transactional
    @Override
    public String updateUserRole(UUID userId, RoomEndUserRoleUpdateRequest roomEndUserRoleUpdateRequest) {
        Optional<Room> room = this.roomRepository.findById(roomEndUserRoleUpdateRequest.roomId());
        if (!room.isPresent()) {
            throw new DataNotFoundException("존재하지 않는 룸입니다.");
        }
        Optional<RoomEndUser> roomEndUser = roomEndUserRepository.findByRoomAndUserIdAndIsDeleted(room.get(), userId, false);
        if (!roomEndUser.isPresent()) {
            throw new PermissionDenyException("해당 룸에 가입하지 않았습니다.");
        }
        Room gotRoom = room.get();
        if (!roomEndUser.get().getRole().equals(RoomRole.ROOM_ADMIN)) {
            throw new PermissionDenyException("해당 룸의 관리자가 아닙니다.");
        }
        Optional<RoomEndUser> toRoomEndUser = roomEndUserRepository.findByRoomAndUserIdAndIsDeleted(room.get(), roomEndUserRoleUpdateRequest.userId(), false);
        if (!toRoomEndUser.isPresent()) {
            throw new PermissionDenyException("해당 타겟이 룸의 멤버가 아닙니다.");
        }
        RoomEndUser gotToRoomEndUser = toRoomEndUser.get();
        gotToRoomEndUser.updateRole(roomEndUserRoleUpdateRequest.role());
        return "룸의 유저의 역할이 변경되었습니다.";
    }
    @Transactional(readOnly = true)
    @Override
    public Boolean isSubscribed(UUID userId, UUID roomId) {
        Optional<Room> room = this.roomRepository.findById(roomId);
        if (!room.isPresent()) {
            throw new DataNotFoundException("존재하지 않는 룸입니다.");
        }
        return this.roomEndUserRepository.existsByRoomAndUserIdAndIsDeleted(room.get(), userId, false);
    }

    private Room createRoomAndSpace(RoomCreateRequest roomCreateRequest, Channel channel, UUID userId) {
        if (!roomCreateRequest.type().equals(RoomType.ROOM_PUBLIC) && !roomCreateRequest.type().equals(RoomType.ROOM_PRIVATE)
        && !roomCreateRequest.type().equals(RoomType.ROOM_OPEN)) {
            throw new PermissionDenyException("해당 룸의 타입을 공용 또는 캡처로 설정할 수 없습니다.");
        }
        Room room = Room.builder()
                .title(roomCreateRequest.title())
                .description(roomCreateRequest.description())
                .imageUrl(roomCreateRequest.imageUrl())
                .channel(channel)
                .type(roomCreateRequest.type())
                .build();
        Room savedRoom = this.roomRepository.save(room);
        Optional<ChannelEndUser> channelEndUser = channelEndUserRepository.findByChannelAndUserId(channel, userId);
        if (!channelEndUser.isPresent()) {
            throw new DataNotFoundException("해당 채널이 없거나 해당 채널에 가입하지 않았습니다.");
        }
        if (channelEndUser.get().getBan().equals(true)) {
            throw new CustomException("차단 당한 채널에서 룸을 생성할 수 없습니다.");
        }

        List<RoomEndUser> roomEndUsers = this.roomEndUserRepository.findAllByUserIdAndIsDeleted(userId, false);
        RoomEndUser lastRoomEndUser = getLastRoomEndUser(roomEndUsers);
        RoomEndUser roomEndUser = RoomEndUser.builder()
                .userId(userId)
                .room(savedRoom)
                .owner(true)
                .role(RoomRole.ROOM_ADMIN)
                .prevId(lastRoomEndUser.getId())
                .channelEndUser(channelEndUser.get())
                .channel(channel)
                .build();
        lastRoomEndUser.updateNext(this.roomEndUserRepository.save(roomEndUser).getId());
        Space feed = Space.builder()
                .room(savedRoom)
                .role(roomCreateRequest.type().equals(RoomType.ROOM_PRIVATE) ? SpaceRole.SPACEROLE_USER : SpaceRole.SPACEROLE_GUEST)
                .title("일반 피드")
                .description("일반 피드 스페이스")
                .type(SpaceType.SPACE_FEED)
                .prevId(null)
                .build();
        Space savedFeed = this.spaceRepository.save(feed);
        Space chat = Space.builder()
                .room(savedRoom)
                .role(roomCreateRequest.type().equals(RoomType.ROOM_PRIVATE) ? SpaceRole.SPACEROLE_USER : SpaceRole.SPACEROLE_GUEST)
                .title("일반 채팅")
                .description("일반 채팅 스페이스")
                .type(SpaceType.SPACE_CHAT)
                .prevId(savedFeed.getId())
                .build();
        savedFeed.updateNext(this.spaceRepository.save(chat).getId());
        return savedRoom;
    }
    public RoomEndUser getLastRoomEndUser(List<RoomEndUser> roomEndUsers) {
        return roomEndUsers.stream().filter(roomEndUser -> Objects.isNull(roomEndUser.getNextId()) && !roomEndUser.getIsDeleted())
                .findAny().orElse(null);

    }
    public RoomEndUser getFirstRoomEndUser(List<RoomEndUser> roomEndUsers) {
        return roomEndUsers.stream().filter(roomEndUser -> Objects.isNull(roomEndUser.getPrevId()) && !roomEndUser.getIsDeleted())
                .findAny().orElse(null);
    }
    public Space getLastSpace(List<Space> spaces) {
        return spaces.stream().filter(space -> Objects.isNull(space.getNextId()))
                .findAny().orElse(null);
    }
    public Space getFirstSpace(List<Space> spaces) {
        return spaces.stream().filter(space -> Objects.isNull(space.getPrevId()))
                .findAny().orElse(null);
    }
    private void deleteRoomEndUser(RoomEndUser roomEndUser) {
        Optional<RoomEndUser> tempPrev;
        Optional<RoomEndUser> tempNext;
        if (roomEndUser.getPrevId() != null) {
            tempPrev = roomEndUserRepository.findById(roomEndUser.getPrevId());
            if (roomEndUser.getNextId() != null) {
                tempNext = roomEndUserRepository.findById(roomEndUser.getNextId());
                if ( tempPrev.isPresent() ) {
                    if (tempNext.isPresent()) {
                        tempPrev.get().updateNext(tempNext.get().getId());
                    }
                }
            } else {
                tempPrev.get().updateNext(null);
            }
        }
        if (roomEndUser.getNextId() != null) {
            tempNext = roomEndUserRepository.findById(roomEndUser.getNextId());
            if (roomEndUser.getPrevId() != null) {
                tempPrev = roomEndUserRepository.findById(roomEndUser.getPrevId());
                if ( tempNext.isPresent() ) {
                    if (tempPrev.isPresent()) {
                        tempNext.get().updatePrev(tempPrev.get().getId());
                    }
                }
            } else {
                tempNext.get().updatePrev(null);
            }
        }
        roomEndUser.updateIsDeleted(true);
    }
    private void locateRoomEndUser(RoomEndUser roomEndUser, RoomEndUser toRoomEndUser) {
        UUID tempPrevId = roomEndUser.getPrevId();
        UUID tempNextId = roomEndUser.getNextId();

        UUID tempToPrevId = toRoomEndUser.getPrevId();
        UUID tempToNextId = toRoomEndUser.getNextId();
        Optional<RoomEndUser> tempPrev;
        Optional<RoomEndUser> tempNext;
        if (tempNextId != null ? tempNextId.equals(toRoomEndUser.getId()) : false ||
            tempPrevId != null ? tempPrevId.equals(toRoomEndUser.getId()) : false) {
            if (tempNextId.equals(toRoomEndUser.getId())) {
                roomEndUser.updateNext(tempToNextId);
                roomEndUser.updatePrev(toRoomEndUser.getId());
                toRoomEndUser.updateNext(roomEndUser.getId());
                toRoomEndUser.updatePrev(tempPrevId);
                if (tempPrevId != null) {
                    tempPrev = roomEndUserRepository.findById(tempPrevId);
                    if ( tempPrev.isPresent() ) {
                        tempPrev.get().updateNext(toRoomEndUser.getId());
                    }
                }
                if (tempToNextId != null) {
                    tempNext = roomEndUserRepository.findById(tempToNextId);
                    if ( tempNext.isPresent() ) {
                        tempNext.get().updatePrev(roomEndUser.getId());
                    }
                }
            } else {
                roomEndUser.updatePrev(tempToPrevId);
                roomEndUser.updateNext(toRoomEndUser.getId());
                toRoomEndUser.updatePrev(roomEndUser.getId());
                toRoomEndUser.updateNext(tempNextId);
                if (tempNextId != null) {
                    tempNext = roomEndUserRepository.findById(tempNextId);
                    if ( tempNext.isPresent() ) {
                        tempNext.get().updatePrev(toRoomEndUser.getId());
                    }
                }
                if (tempToPrevId != null) {
                    tempPrev = roomEndUserRepository.findById(tempToPrevId);
                    if ( tempPrev.isPresent() ) {
                        tempPrev.get().updateNext(roomEndUser.getId());
                    }
                }
            }
        } else {
            if (tempPrevId != null) {
                tempPrev = roomEndUserRepository.findById(tempPrevId);
                if ( tempPrev.isPresent() ) {
                    tempPrev.get().updateNext(toRoomEndUser.getId());
                }
            }
            if (tempNextId != null) {
                tempNext = roomEndUserRepository.findById(tempNextId);
                if ( tempNext.isPresent() ) {
                    tempNext.get().updatePrev(toRoomEndUser.getId());
                }
            }
            toRoomEndUser.updatePrev(tempPrevId);
            toRoomEndUser.updateNext(tempNextId);
            if (tempToPrevId != null) {
                tempPrev = roomEndUserRepository.findById(tempToPrevId);
                if ( tempPrev.isPresent() ) {
                    tempPrev.get().updateNext(roomEndUser.getId());
                }
            }
            if (tempToNextId != null) {
                tempNext = roomEndUserRepository.findById(tempToNextId);
                if ( tempNext.isPresent() ) {
                    tempNext.get().updatePrev(roomEndUser.getId());
                }
            }
            roomEndUser.updatePrev(tempToPrevId);
            roomEndUser.updateNext(tempToNextId);
        }
    }
}
