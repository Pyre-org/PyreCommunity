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
            if (!this.roomEndUserRepository.existsByRoomAndUserId(room.get(), userId)) {
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
        List<RoomEndUser> roomEndUsers = this.roomEndUserRepository.findAllByChannelAndUserId(channel.get(), userId);
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
        List<RoomEndUser> roomEndUsers = this.roomEndUserRepository.findAllByChannelAndUserId(channel.get(), userId);
        RoomEndUser roomEndUser = getFirstRoomEndUser(roomEndUsers);
        List<RoomEndUser> sortedRoomEndUsers = new ArrayList<>();
        if (roomEndUser == null) {
            RoomGetDetailListResponse.makeDto(new ArrayList<>());
        }
        while (roomEndUser != null) {
            sortedRoomEndUsers.add(roomEndUser);
            roomEndUser = roomEndUser.getNext();
        }
        List<Room> rooms = new ArrayList<>();
        for (RoomEndUser r : sortedRoomEndUsers) {
            rooms.add(r.getRoom());
        }
        List<RoomGetDetailResponse> roomGetDetailResponses = new ArrayList<>();
        for (Room r : rooms) {
            List<Space> sortedSpaces = new ArrayList<>();
            Optional<RoomEndUser> roomEndUser2 = roomEndUserRepository.findByRoomAndUserId(r, userId);
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
            while (firstSpace != null) {
                sortedSpaces.add(firstSpace);
                firstSpace = firstSpace.getNext();
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
        if (this.roomEndUserRepository.existsByRoomAndUserId(room.get(), userId)) {
            throw new DuplicateException("이미 가입한 룸입니다.");
        }
        Room gotRoom = room.get();
        List<RoomEndUser> roomEndUsers = this.roomEndUserRepository.findAllByUserId(userId);
        RoomEndUser lastRoomEndUser = getLastRoomEndUser(roomEndUsers);
        RoomEndUser savedRoomEndUser;
        if (gotRoom.getType().equals(RoomType.ROOM_PUBLIC)) {
            RoomEndUser roomEndUser = RoomEndUser.builder()
                    .userId(userId)
                    .room(gotRoom)
                    .owner(false)
                    .prev(lastRoomEndUser)
                    .channelEndUser(channelEndUser.get())
                    .role(RoomRole.ROOM_GUEST)
                    .channel(channel.get())
                    .build();
            lastRoomEndUser.updateNext(roomEndUser);
            savedRoomEndUser = this.roomEndUserRepository.save(roomEndUser);
        } else if (!gotRoom.getType().equals(RoomType.ROOM_OPEN)) {
            throw new PermissionDenyException("해당 룸은 초대장을 통해서 가입할 수 있습니다.");
        } else {
            RoomEndUser roomEndUser = RoomEndUser.builder()
                    .userId(userId)
                    .room(gotRoom)
                    .owner(false)
                    .prev(lastRoomEndUser)
                    .channelEndUser(channelEndUser.get())
                    .role(RoomRole.ROOM_USER)
                    .channel(channel.get())
                    .build();
            lastRoomEndUser.updateNext(roomEndUser);
            savedRoomEndUser = this.roomEndUserRepository.save(roomEndUser);
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
        Optional<RoomEndUser> roomEndUser = roomEndUserRepository.findByRoomAndUserId(room.get(), userId);
        if (!roomEndUser.isPresent()) {
            throw new PermissionDenyException("해당 룸에 가입하지 않았습니다.");
        }
        RoomEndUser gotRoomEndUser = roomEndUser.get();
        if (gotRoomEndUser.getRole().equals(RoomRole.ROOM_ADMIN)) {
            throw new PermissionDenyException("룸의 관리자는 룸을 탈퇴할 수 없습니다.");
        }
        gotRoomEndUser.getPrev().updateNext(gotRoomEndUser.getNext());
        if (!Objects.isNull(gotRoomEndUser.getNext())) {
            gotRoomEndUser.getNext().updatePrev(gotRoomEndUser.getPrev());
        }

        roomEndUserRepository.delete(gotRoomEndUser);
        // global 룸 아이디 반환
        return room.get().getChannel().getRooms().get(0).getId();
    }
    @Transactional
    @Override
    public String updateRoom(UUID roomId, UUID userId, RoomUpdateRequest roomUpdateRequest) {
        Optional<Room> room = this.roomRepository.findById(roomId);
        if (!room.isPresent()) {
            throw new DataNotFoundException("존재하지 않는 룸입니다.");
        }
        Optional<RoomEndUser> roomEndUser = roomEndUserRepository.findByRoomAndUserId(room.get(), userId);
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
        Optional<RoomEndUser> roomEndUser = roomEndUserRepository.findByRoomAndUserId(room.get(), userId);
        if (!roomEndUser.isPresent()) {
            throw new PermissionDenyException("해당 룸에 가입하지 않았습니다.");
        }
        Room gotRoom = room.get();
        if (!roomEndUser.get().getOwner().equals(userId)) {
            throw new PermissionDenyException("해당 룸의 소유자가 아닙니다.");
        }
        List<RoomEndUser> roomEndUsers = this.roomEndUserRepository.findAllByRoom(gotRoom);
        for (RoomEndUser r : roomEndUsers) {
            r.getPrev().updateNext(r.getNext());
            if (!Objects.isNull(r.getNext())) {
                r.getNext().updatePrev(r.getPrev());
            }
        }
        UUID globalRoomUUID = room.get().getChannel().getRooms().get(0).getId();
        roomRepository.delete(gotRoom);
        return globalRoomUUID;
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

        Optional<RoomEndUser> roomEndUser = roomEndUserRepository.findByRoomAndUserId(room.get(), userId);
        Optional<RoomEndUser> toRoomEndUser = roomEndUserRepository.findByRoomAndUserId(toRoom.get(), userId);
        if (!roomEndUser.isPresent() || !toRoomEndUser.isPresent()) {
            throw new PermissionDenyException("해당 룸에 가입하지 않았습니다.");
        }
        Channel gotChannel = room.get().getChannel();
        if (!gotChannel.equals(toRoom.get().getChannel())) {
            throw new CustomException("해당 룸과 이동하려는 룸의 채널이 다릅니다.");
        }
        RoomEndUser gotRoomEndUser = roomEndUser.get();
        RoomEndUser gotToRoomEndUser = toRoomEndUser.get();
        RoomEndUser tempPrev = gotRoomEndUser.getPrev();
        RoomEndUser tempNext = gotRoomEndUser.getNext();
        if (gotRoomEndUser.getPrev() != null) {
            gotRoomEndUser.getPrev().updateNext(gotToRoomEndUser);
        }
        if (gotRoomEndUser.getNext() != null) {
            gotRoomEndUser.getNext().updatePrev(gotToRoomEndUser);
        }
        gotRoomEndUser.updatePrev(gotToRoomEndUser.getPrev());
        gotRoomEndUser.updateNext(gotToRoomEndUser.getNext());
        if (gotToRoomEndUser.getPrev() != null) {
            gotToRoomEndUser.getPrev().updateNext(gotRoomEndUser);
        }
        if (gotToRoomEndUser.getNext() != null) {
            gotToRoomEndUser.getNext().updatePrev(gotRoomEndUser);
        }
        gotToRoomEndUser.updatePrev(tempPrev);
        gotToRoomEndUser.updateNext(tempNext);
        return "룸의 위치가 변경되었습니다.";
    }
    @Transactional
    @Override
    public String updateUserRole(UUID userId, RoomEndUserRoleUpdateRequest roomEndUserRoleUpdateRequest) {
        Optional<Room> room = this.roomRepository.findById(roomEndUserRoleUpdateRequest.roomId());
        if (!room.isPresent()) {
            throw new DataNotFoundException("존재하지 않는 룸입니다.");
        }
        Optional<RoomEndUser> roomEndUser = roomEndUserRepository.findByRoomAndUserId(room.get(), userId);
        if (!roomEndUser.isPresent()) {
            throw new PermissionDenyException("해당 룸에 가입하지 않았습니다.");
        }
        Room gotRoom = room.get();
        if (!roomEndUser.get().getRole().equals(RoomRole.ROOM_ADMIN)) {
            throw new PermissionDenyException("해당 룸의 관리자가 아닙니다.");
        }
        Optional<RoomEndUser> toRoomEndUser = roomEndUserRepository.findByRoomAndUserId(room.get(), roomEndUserRoleUpdateRequest.userId());
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
        return this.roomEndUserRepository.existsByRoomAndUserId(room.get(), userId);
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

        List<RoomEndUser> roomEndUsers = this.roomEndUserRepository.findAllByUserId(userId);
        RoomEndUser lastRoomEndUser = getLastRoomEndUser(roomEndUsers);
        RoomEndUser roomEndUser = RoomEndUser.builder()
                .userId(userId)
                .room(savedRoom)
                .owner(true)
                .role(RoomRole.ROOM_ADMIN)
                .prev(lastRoomEndUser)
                .channelEndUser(channelEndUser.get())
                .channel(channel)
                .build();
        lastRoomEndUser.updateNext(this.roomEndUserRepository.save(roomEndUser));
        Space feed = Space.builder()
                .room(savedRoom)
                .role(roomCreateRequest.type().equals(RoomType.ROOM_PRIVATE) ? SpaceRole.SPACEROLE_USER : SpaceRole.SPACEROLE_GUEST)
                .title("일반 피드")
                .description("일반 피드 스페이스")
                .type(SpaceType.SPACE_FEED)
                .prev(null)
                .build();
        Space savedFeed = this.spaceRepository.save(feed);
        Space chat = Space.builder()
                .room(savedRoom)
                .role(roomCreateRequest.type().equals(RoomType.ROOM_PRIVATE) ? SpaceRole.SPACEROLE_USER : SpaceRole.SPACEROLE_GUEST)
                .title("일반 채팅")
                .description("일반 채팅 스페이스")
                .type(SpaceType.SPACE_CHAT)
                .prev(savedFeed)
                .build();
        savedFeed.updateNext(this.spaceRepository.save(chat));
        return savedRoom;
    }
    public RoomEndUser getLastRoomEndUser(List<RoomEndUser> roomEndUsers) {
        return roomEndUsers.stream().filter(roomEndUser -> Objects.isNull(roomEndUser.getNext()))
                .findAny().orElse(null);

    }
    public RoomEndUser getFirstRoomEndUser(List<RoomEndUser> roomEndUsers) {
        return roomEndUsers.stream().filter(roomEndUser -> Objects.isNull(roomEndUser.getPrev()))
                .findAny().orElse(null);
    }
    public Space getLastSpace(List<Space> spaces) {
        return spaces.stream().filter(space -> Objects.isNull(space.getNext()))
                .findAny().orElse(null);
    }
    public Space getFirstSpace(List<Space> spaces) {
        return spaces.stream().filter(space -> Objects.isNull(space.getPrev()))
                .findAny().orElse(null);
    }

}
