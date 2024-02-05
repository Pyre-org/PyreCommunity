package com.pyre.community.service.impl;

import com.pyre.community.dto.request.RoomCreateRequest;
import com.pyre.community.dto.response.RoomCreateResponse;
import com.pyre.community.dto.response.RoomGetResponse;
import com.pyre.community.dto.response.RoomJoinResponse;
import com.pyre.community.dto.response.RoomListByChannelResponse;
import com.pyre.community.entity.*;
import com.pyre.community.enumeration.RoomRole;
import com.pyre.community.enumeration.RoomType;
import com.pyre.community.enumeration.SpaceRole;
import com.pyre.community.enumeration.SpaceType;
import com.pyre.community.exception.customexception.CustomException;
import com.pyre.community.exception.customexception.DataNotFoundException;
import com.pyre.community.exception.customexception.PermissionDenyException;
import com.pyre.community.repository.*;
import com.pyre.community.service.RoomService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.enums.Enum;
import org.apache.tomcat.websocket.AuthenticationException;
import org.springframework.stereotype.Service;

import java.util.*;

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
        Room savedRoom = createRoomAndSpace(roomCreateRequest, gotChannel, userId);
        RoomCreateResponse roomCreateResponse = RoomCreateResponse.makeDto(savedRoom);
        return roomCreateResponse;
    }
    @Transactional
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
            if (!this.roomEndUserRepository.existsByIdAndAndUserId(id, userId)) {
                throw new PermissionDenyException("해당 룸에 가입하지 않은 상태입니다.");
            }
            RoomGetResponse roomGetResponse = RoomGetResponse.makeDto(gotRoom);
            return roomGetResponse;
        }
    }
    @Transactional
    @Override
    public RoomListByChannelResponse listByChannelAndKeywordAndType(UUID channelId, String keyword, String type) {
        Optional<Channel> channel = this.channelRepository.findById(channelId);
        if (!channel.isPresent()) {
            throw new DataNotFoundException("존재하지 않는 채널입니다.");
        }
        if (!type.equals("ROOM_PUBLIC") && !type.equals("ROOM_OPEN")) {
            keyword = "ROOM_PUBLIC";
        }
        List<Room> rooms = this.roomRepository.findAllByChannelAndTypeAndTitleStartingWithOrderByTitle(channel.get(), RoomType.valueOf(type), keyword);
        return RoomListByChannelResponse.makeDto(rooms);
    }
    @Transactional
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
            if (!r.getRoom().getType().equals(RoomType.ROOM_GLOBAL) && r.getRoom().getType().equals(RoomType.ROOM_CAPTURE)) {
                rooms.add(r.getRoom());
            }
        }
        List<Room> sortedRoom = rooms.stream().sorted(Comparator.comparing(Room::getTitle)).toList();
        return RoomListByChannelResponse.makeDto(sortedRoom);
    }
    @Transactional
    @Override
    public RoomListByChannelResponse listByChannelAndUserIdByIndexing(UUID channelId, UUID userId) {
        Optional<Channel> channel = this.channelRepository.findById(channelId);
        if (!channel.isPresent()) {
            throw new DataNotFoundException("존재하지 않는 채널입니다.");
        }
        if (!this.channelEndUserRepository.existsByChannelAndUserId(channel.get(), userId)) {
            throw new DataNotFoundException("해당 채널에 가입하지 않았습니다.");
        }
        List<RoomEndUser> roomEndUsers = this.roomEndUserRepository.findAllByChannelAndUserIdOrderByIndexingAsc(channel.get(), userId);
        List<Room> rooms = new ArrayList<>();
        for (RoomEndUser r : roomEndUsers) {
            rooms.add(r.getRoom());
        }

        return RoomListByChannelResponse.makeDto(rooms);
    }
    @Transactional
    @Override
    public RoomJoinResponse joinRoom(UUID roomId, UUID userId, UUID channelId) {
        Optional<Channel> channel = this.channelRepository.findById(channelId);
        if (!channel.isPresent()) {
            throw new DataNotFoundException("존재하지 않는 채널입니다.");
        }
        if (!this.channelEndUserRepository.existsByChannelAndUserId(channel.get(), userId)) {
            throw new PermissionDenyException("해당 채널에 가입하지 않았습니다.");
        }
        Optional<Room> room = this.roomRepository.findById(roomId);
        if (!room.isPresent()) {
            throw new DataNotFoundException("존재하지 않는 룸입니다.");
        }
        Room gotRoom = room.get();
        List<RoomEndUser> roomEndUsers = this.roomEndUserRepository.findTop1ByUserIdOrderByIndexingDesc(userId);
        RoomEndUser savedRoomEndUser;
        if (gotRoom.getType().equals(RoomType.ROOM_PUBLIC)) {
            RoomEndUser roomEndUser = RoomEndUser.builder()
                    .userId(userId)
                    .room(gotRoom)
                    .owner(false)
                    .indexing(roomEndUsers.get(0).getIndexing() + 1)
                    .role(RoomRole.ROOM_GUEST)
                    .build();
            savedRoomEndUser = this.roomEndUserRepository.save(roomEndUser);
        } else if (!gotRoom.getType().equals(RoomType.ROOM_OPEN)) {
            throw new PermissionDenyException("해당 룸은 초대장을 통해서 가입할 수 있습니다.");
        } else {
            RoomEndUser roomEndUser = RoomEndUser.builder()
                    .userId(userId)
                    .room(gotRoom)
                    .owner(false)
                    .indexing(roomEndUsers.get(0).getIndexing() + 1)
                    .role(RoomRole.ROOM_USER)
                    .build();
            savedRoomEndUser = this.roomEndUserRepository.save(roomEndUser);
        }
        RoomJoinResponse roomJoinResponse = RoomJoinResponse.makeDto(savedRoomEndUser);

        return roomJoinResponse;
    }

    private Room createRoomAndSpace(RoomCreateRequest roomCreateRequest, Channel channel, UUID userId) {
        Room room = Room.builder()
                .title(roomCreateRequest.title())
                .description(roomCreateRequest.description())
                .imageUrl(roomCreateRequest.imageUrl())
                .channel(channel)
                .type(roomCreateRequest.type())
                .build();
        Room savedRoom = this.roomRepository.save(room);
        RoomEndUser roomEndUser = RoomEndUser.builder()
                .userId(userId)
                .room(room)
                .owner(true)
                .role(RoomRole.ROOM_ADMIN)
                .build();
        this.roomEndUserRepository.save(roomEndUser);
        Space feed = Space.builder()
                .room(savedRoom)
                .role(SpaceRole.SPACEROLE_GUEST)
                .type(SpaceType.SPACE_FEED)
                .build();
        this.spaceRepository.save(feed);
        Space chat = Space.builder()
                .room(savedRoom)
                .role(SpaceRole.SPACEROLE_GUEST)
                .type(SpaceType.SPACE_CHAT)
                .build();
        this.spaceRepository.save(chat);
        return savedRoom;
    }

}
