package com.pyre.community.service.impl;

import com.pyre.community.dto.request.RoomCreateRequest;
import com.pyre.community.dto.response.RoomCreateResponse;
import com.pyre.community.entity.Channel;
import com.pyre.community.entity.ChannelEndUser;
import com.pyre.community.entity.Room;
import com.pyre.community.entity.Space;
import com.pyre.community.enumeration.SpaceRole;
import com.pyre.community.enumeration.SpaceType;
import com.pyre.community.exception.customexception.CustomException;
import com.pyre.community.exception.customexception.DataNotFoundException;
import com.pyre.community.repository.*;
import com.pyre.community.service.RoomService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

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
        Room room = Room.builder()
                .title(roomCreateRequest.title())
                .description(roomCreateRequest.description())
                .imageUrl(roomCreateRequest.imageUrl())
                .channel(gotChannel)
                .type(roomCreateRequest.type())
                .build();
        Room savedRoom = this.roomRepository.save(room);
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
        RoomCreateResponse roomCreateResponse = RoomCreateResponse.makeDto(savedRoom);
        return roomCreateResponse;
    }

}
