package com.pyre.community.service.impl;


import com.pyre.community.client.UserClient;
import com.pyre.community.dto.request.*;
import com.pyre.community.dto.response.*;
import com.pyre.community.entity.*;
import com.pyre.community.enumeration.*;
import com.pyre.community.exception.customexception.AuthenticationFailException;
import com.pyre.community.exception.customexception.CustomException;
import com.pyre.community.exception.customexception.DataNotFoundException;
import com.pyre.community.exception.customexception.PermissionDenyException;
import com.pyre.community.repository.*;
import com.pyre.community.service.ChannelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChannelServiceImpl implements ChannelService {

    private final ChannelRepository channelRepository;
    private final ChannelEndUserRepository channelEndUserRepository;
    private final SpaceRepository spaceRepository;
    private final UserClient userClient;
    private final RoomRepository roomRepository;
    private final RoomEndUserRepository roomEndUserRepository;
    @Override
    @Transactional
    public ChannelCreateViewDto createChannel(ChannelCreateDto channelCreateDto, UUID id) {
        ChannelGenre genre = channelCreateDto.genre();
        if (channelCreateDto.genre() == null) {
            genre = ChannelGenre.GENERAL;
        }
        Channel channel = Channel.builder()
                .title(channelCreateDto.title())
                .description(channelCreateDto.description())
                .genre(genre)
                .imageUrl(channelCreateDto.imageUrl())
                .build();
        Channel savedChannel = this.channelRepository.save(channel);
        createDefaultRoomAndSpace(savedChannel);

        ChannelCreateViewDto channelCreateViewDto = ChannelCreateViewDto.makeDto(savedChannel);
        return channelCreateViewDto;
    }
    @Override
    @Transactional
    public ChannelGetViewDto getChannel(UUID id) {
        Optional<Channel> channel = this.channelRepository.findById(id);
        if (!channel.isPresent()) {
            throw new DataNotFoundException("해당 아이디를 가진 채널을 찾을 수 없습니다.");
        }
        Channel getChannel = channel.get();
        ChannelGetViewDto channelGetViewDto = ChannelGetViewDto.createChannelGetViewDto(getChannel);
        return channelGetViewDto;
    }
    @Transactional
    @Override
    public ChannelGetAllViewDto getAllChannelByUser(
            UUID userId, String token
    ) {

        List<ChannelEndUser> channelEndUsers = this.channelEndUserRepository.findAllByUserId(userId, Sort.by(Sort.Direction.ASC, "indexing"));
        List<Channel> channels = new ArrayList<>();
        for (ChannelEndUser ce: channelEndUsers) {
            channels.add(ce.getChannel());
        }

        ChannelGetAllViewDto channelGetViewDtos = new ChannelGetAllViewDto(channels.size(), new ArrayList<>());

        for (Channel channel: channels) {
            ChannelGetViewDto channelGetViewDto =
                    ChannelGetViewDto.createChannelGetViewDto(channel);
            channelGetViewDtos.hits().add(channelGetViewDto);
        }
        return channelGetViewDtos;
    }
    @Transactional
    @Override
    public ChannelGetAllViewDto getAllChannelByUserAndSearch(
            UUID userId, String token,
            String genre,
            String sortBy,
            String keyword,
            Boolean orderByDesc
    ) {
        if (!(sortBy.equals("title") || sortBy.equals("cAt") || sortBy.equals("memberCounts") || sortBy.equals("roomCounts"))) {
            sortBy = "title";
        }
        if (sortBy.equals("memberCounts")) sortBy = "endUsers";
        if (sortBy.equals("roomCounts")) sortBy = "rooms";



        List<ChannelEndUser> channelEndUsers = this.channelEndUserRepository.findAllByUserId(userId);
        List<Channel> channels = new ArrayList<>();
        for (ChannelEndUser ce: channelEndUsers) {
            if (genre != null) {
                if (ce.getChannel().getGenre().equals(genre)) {
                    if (keyword.equals(null) || keyword.equals("")) {
                        channels.add(ce.getChannel());
                    } else {
                        if (ce.getChannel().getTitle().startsWith(keyword)) {
                            channels.add(ce.getChannel());
                        }
                    }
                }
            } else {
                if (keyword.equals(null) || keyword.equals("")) {
                    channels.add(ce.getChannel());
                } else {
                    if (ce.getChannel().getTitle().startsWith(keyword)) {
                        channels.add(ce.getChannel());
                    }
                }
            }
        }
        if (orderByDesc.equals(false)) {
            if (sortBy.equals("title")) channels = channels.stream().sorted(Comparator.comparing(Channel::getTitle)).toList();
            if (sortBy.equals("cAt")) channels = channels.stream().sorted(Comparator.comparing(Channel::getCAt)).toList();
            if (sortBy.equals("endUsers")) channels = channels.stream().sorted(Comparator.comparing(Channel::getMemberCounts)).toList();
            if (sortBy.equals("rooms")) channels = channels.stream().sorted(Comparator.comparing(Channel::getRoomCounts)).toList();
        } else {
            if (sortBy.equals("title")) channels = channels.stream().sorted(Comparator.comparing(Channel::getTitle).reversed()).toList();
            if (sortBy.equals("cAt")) channels = channels.stream().sorted(Comparator.comparing(Channel::getCAt).reversed()).toList();
            if (sortBy.equals("endUsers")) channels = channels.stream().sorted(Comparator.comparing(Channel::getMemberCounts).reversed()).toList();
            if (sortBy.equals("rooms")) channels = channels.stream().sorted(Comparator.comparing(Channel::getRoomCounts).reversed()).toList();
        }



        ChannelGetAllViewDto channelGetViewDtos = new ChannelGetAllViewDto(channels.size(), new ArrayList<>());

        for (Channel channel: channels) {
            ChannelGetViewDto channelGetViewDto =
                    ChannelGetViewDto.createChannelGetViewDto(channel);
            channelGetViewDtos.hits().add(channelGetViewDto);
        }
        return channelGetViewDtos;
    }
    @Override
    @Transactional
    public ChannelGetAllViewDto getAllChannel(
            int page,
            int count,
            String genre,
            String sortBy,
            String keyword,
            Boolean orderByDesc
    ) {
        if (!(sortBy.equals("title") || sortBy.equals("cAt") || sortBy.equals("memberCounts") || sortBy.equals("roomCounts"))) {
            sortBy = "title";
        }
        if (sortBy.equals("memberCounts")) sortBy = "endUsers";
        if (sortBy.equals("roomCounts")) sortBy = "rooms";

        Sort sort = (orderByDesc) ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, count, sort);

        Page<Channel> channels = this.channelRepository.findAllByApprovalStatusAndTitleStartingWith(ApprovalStatus.ALLOW, keyword, pageable);
        if (genre != null) {
            channels = this.channelRepository.findAllByGenreAndApprovalStatusAndTitleContaining(genre, ApprovalStatus.ALLOW, keyword, pageable);
        }

        ChannelGetAllViewDto channelGetViewDtos = new ChannelGetAllViewDto(channels.getTotalElements(), new ArrayList<>());

        for (Channel channel: channels.getContent()) {
            ChannelGetViewDto channelGetViewDto =
                    ChannelGetViewDto.createChannelGetViewDto(channel);
            channelGetViewDtos.hits().add(channelGetViewDto);
        }

        return channelGetViewDtos;
    }
    @Override
    @Transactional
    public String updateChannelApprovalStatus(String accessToken, UUID channelId, ChannelUpdateApprovalStatusDto allow) {
        if (allow == null) {
            throw new RuntimeException("해당 요청은 바디가 필수입니다.");
        }
        UserInfoFeignResponse userInfo = this.userClient.getUserInfo(accessToken);
        Optional<Channel> channel = this.channelRepository.findById(channelId);
        if (userInfo == null) {
            throw new PermissionDenyException("권한이 없습니다.");
        }
        if (!userInfo.role().equals("ROLE_ADMIN")) {
            throw new PermissionDenyException("권한이 없습니다.");
        }

        if (!channel.isPresent()) {
            throw new DataNotFoundException("해당 채널을 찾을 수 없습니다.");
        }
        Channel gotChannel = channel.get();

        if (allow.status().equals(ApprovalStatus.DENY)) {
            this.channelRepository.delete(gotChannel);
        } else {
            gotChannel.updateApprovalStatus(allow.status());
            this.channelRepository.save(gotChannel);
        }
        return "성공적으로 채널이 " + allow.status() +" 되었습니다.";
    }

    @Override
    @Transactional
    public ChannelGetViewDto editChannel(String accessToken, UUID channelId, ChannelEditDto channelEditDto) {
        UserInfoFeignResponse userInfo = this.userClient.getUserInfo(accessToken);
        Optional<Channel> channel = this.channelRepository.findById(channelId);
        if (userInfo == null) {
            throw new PermissionDenyException("권한이 없습니다.");
        }
        if (!userInfo.role().equals("ROLE_ADMIN")) {
            throw new PermissionDenyException("권한이 없습니다.");
        }

        if (!channel.isPresent()) {
            throw new DataNotFoundException("해당 채널을 찾을 수 없습니다.");
        }
        Channel gotChannel = channel.get();
        gotChannel.updateChannel(
                channelEditDto.title(),
                channelEditDto.description(),
                channelEditDto.genre(),
                channelEditDto.imageUrl());

        Channel savedChannel = this.channelRepository.save(gotChannel);
        ChannelGetViewDto channelGetViewDto = ChannelGetViewDto.createChannelGetViewDto(savedChannel);
        return channelGetViewDto;
    }
    @Override
    @Transactional
    public ChannelGetAllViewDto viewWaitApprovalChannel(String accessToken, int page, int count) {

        UserInfoFeignResponse userInfo = this.userClient.getUserInfo(accessToken);
        log.error("{}",userInfo);
        if (userInfo == null) {
            throw new AuthenticationFailException("권한이 없습니다.");
        }
        if (!userInfo.role().equals("ROLE_ADMIN")) {
            throw new AuthenticationFailException("권한이 없습니다.");
        }

        Sort sort = Sort.by("cAt").descending();
        Pageable pageable = PageRequest.of(page, count, sort);

        Page<Channel> channels = this.channelRepository.findAllByApprovalStatus(ApprovalStatus.CHECKING, pageable);
        ChannelGetAllViewDto allViewDto = new ChannelGetAllViewDto(channels.getTotalElements(), new ArrayList<>());
        for (Channel channel: channels.getContent()) {
            ChannelGetViewDto getViewDto = ChannelGetViewDto.createChannelGetViewDto(channel);
            allViewDto.hits().add(getViewDto);
        }
        return allViewDto;
    }
    @Override
    @Transactional
    public ChannelGetGenresResponseDto getGenres(String name) {
        List<ChannelGenre> genres = new ArrayList<>();

        for (ChannelGenre genre: ChannelGenre.values()) {
            if (genre.getKey().startsWith(name)) {
                genres.add(genre);
            }
        }
        ChannelGetGenresResponseDto dto = ChannelGetGenresResponseDto.makeDto(genres);

        return dto;
    }
    @Transactional
    @Override
    public ChannelJoinResponse joinChannel(UUID userId, String accessToken, ChannelJoinRequest request) {
        if (!request.agreement()) {
            throw new CustomException("필수 동의 사항에 동의를 해야 채널에 가입할 수 있습니다.");
        }
        Optional<Channel> channel = this.channelRepository.findById(request.channelId());
        List<ChannelEndUser> channelEndUsers = this.channelEndUserRepository.findTop1ByUserIdOrderByIndexingDesc(userId);
        if (!channel.isPresent()) {
            throw new DataNotFoundException("해당 채널을 찾을 수 없습니다.");
        }
        if (this.channelEndUserRepository.existsByChannelAndUserId(channel.get(), userId)) {
            throw new DuplicateFormatFlagsException("해당 채널에 이미 가입했습니다.");
        }
        if (!channel.get().getApprovalStatus().equals(ApprovalStatus.ALLOW)) {
            throw new CustomException("해당 채널은 승인되지 않은 채널입니다.");
        }
        int indexing = (!channelEndUsers.isEmpty()) ? channelEndUsers.get(0).getIndexing() + 1 : 0;
        Channel gotChannel = channel.get();

        ChannelEndUser channelEndUser = ChannelEndUser.builder()
                .channel(gotChannel)
                .userId(userId)
                .agreement(request.agreement())
                .indexing(indexing)
                .build();
        ChannelEndUser saveChannelEndUser = this.channelEndUserRepository.save(channelEndUser);
        RoomEndUser global = RoomEndUser.builder()
                .room(this.roomRepository.findByChannelAndType(gotChannel, RoomType.ROOM_GLOBAL))
                .owner(false)
                .userId(userId)
                .role(RoomRole.ROOM_USER)
                .indexing(0).build();
        this.roomEndUserRepository.save(global);
        RoomEndUser capture = RoomEndUser.builder()
                .room(this.roomRepository.findByChannelAndType(gotChannel, RoomType.ROOM_CAPTURE))
                .owner(false)
                .userId(userId)
                .role(RoomRole.ROOM_USER)
                .indexing(1).build();
        this.roomEndUserRepository.save(capture);
        ChannelJoinResponse channelJoinResponse = ChannelJoinResponse.makeDto(gotChannel.getId(), request.agreement());
        return channelJoinResponse;
    }
    @Transactional
    @Override
    public void locateChannel(UUID userId, String accessToken, ChannelLocateRequest request) {
        List<ChannelEndUser> channelEndUsers = this.channelEndUserRepository.findAllByUserId(userId, Sort.by(Sort.Direction.ASC, "indexing"));
        Optional<Channel> channel = this.channelRepository.findById(request.channelId());
        if (!channel.isPresent()) {
            throw new DataNotFoundException("해당 채널을 찾을 수 없습니다.");
        }
        Optional<ChannelEndUser> channelEndUser = this.channelEndUserRepository.findByChannelAndUserId(channel.get(), userId);
        if (!channelEndUser.isPresent()) {
            throw new DataNotFoundException("해당 채널유저를 찾을 수 없습니다.");
        }
        int fromIndexing = channelEndUser.get().getIndexing();
        for (ChannelEndUser ce: channelEndUsers) {
            if (request.to() <= fromIndexing) {
                if (channelEndUsers.indexOf(ce) == fromIndexing) {
                    ce.updateIndexing(request.to());
                    this.channelEndUserRepository.save(ce);
                    break;
                }
                if (channelEndUsers.indexOf(ce) >= request.to()) {
                    ce.updateIndexing(ce.getIndexing()+1);
                    this.channelEndUserRepository.save(ce);
                }
            } else {
                if (channelEndUsers.indexOf(ce) == request.to()) {
                    ChannelEndUser from = channelEndUsers.get(fromIndexing);
                    ce.updateIndexing(ce.getIndexing()+1);
                    this.channelEndUserRepository.save(ce);
                    from.updateIndexing(request.to());
                    this.channelEndUserRepository.save(from);
                    break;
                }
                if (channelEndUsers.indexOf(ce) > fromIndexing) {
                    ce.updateIndexing(ce.getIndexing()-1);
                    this.channelEndUserRepository.save(ce);
                }
            }
        }
    }
    @Transactional
    @Override
    public void deleteChannel(UUID userId, UUID channelId, String token) {
        Optional<Channel> channel = this.channelRepository.findById(channelId);

        if (!channel.isPresent()) {
            throw new DataNotFoundException("해당 채널은 존재하지 않습니다.");
        }
        Channel gotChannel = channel.get();

        UserInfoFeignResponse userInfo = this.userClient.getUserInfo(token);
        if (userInfo == null) {
            throw new AuthenticationFailException("권한이 없습니다.");
        }
        if (!userInfo.role().equals("ROLE_ADMIN")) {
            throw new AuthenticationFailException("권한이 없습니다.");
        }
        this.channelRepository.delete(gotChannel);
    }
    @Transactional
    @Override
    public void leaveChannel(UUID userId, UUID channelId) {
        Optional<Channel> channel = this.channelRepository.findById(channelId);
        if (!channel.isPresent()) {
            throw new DataNotFoundException("해당 채널은 존재하지 않습니다.");
        }
        Channel gotChannel = channel.get();
        Optional<ChannelEndUser> channelEndUser = this.channelEndUserRepository.findByChannelAndUserId(gotChannel, userId);
        if (!channelEndUser.isPresent()) {
            throw new DataNotFoundException("해당 채널이 없거나 해당 채널에 가입하지 않았습니다.");
        }
        if (channelEndUser.get().getBan().equals(true)) {
            throw new CustomException("차단 당한 채널은 탈퇴할 수 없습니다.");
        }
        List<ChannelEndUser> channelEndUsers = this.channelEndUserRepository.findAllByUserId(userId);
        for (ChannelEndUser c : channelEndUsers) {
            if (c.getIndexing() > channelEndUser.get().getIndexing()) {
                c.updateIndexing(c.getIndexing()-1);
                this.channelEndUserRepository.save(c);
            }
        }

        this.channelEndUserRepository.delete(channelEndUser.get());

    }
    @Transactional
    @Override
    public void banMember(UUID userId, UUID channelId, UUID targetId) {
        Optional<Channel> channel = this.channelRepository.findById(channelId);
        if (!channel.isPresent()) {
            throw new DataNotFoundException("해당 채널은 존재하지 않습니다.");
        }
        Channel gotChannel = channel.get();
        Optional<ChannelEndUser> channelEndUser = this.channelEndUserRepository.findByChannelAndUserId(gotChannel, userId);
        if (!channelEndUser.isPresent()) {
            throw new DataNotFoundException("해당 채널이 없거나 해당 채널에 가입하지 않았습니다.");
        }

        if (channelEndUser.get().getRole() != ChannelRole.CHANNEL_ADMIN) {
            throw new PermissionDenyException("해당 채널의 관리자가 아닙니다.");
        }
        Optional<ChannelEndUser> target = this.channelEndUserRepository.findByChannelAndUserId(gotChannel, targetId);
        if (!target.isPresent()) {
            throw new DataNotFoundException("해당 유저를 찾을 수 없습니다.");
        }
        ChannelEndUser gotTarget = target.get();
        if (gotTarget.getRole() == ChannelRole.CHANNEL_ADMIN) {
            throw new CustomException("관리자를 차단할 수 없습니다.");
        }
        gotTarget.updateBan(true);
        this.channelEndUserRepository.save(gotTarget);

    }

    private String localDateToString(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        String dateString = localDateTime.format(DateTimeFormatter.ofPattern("YYYY-MM-dd HH:mm"));

        return dateString;
    }
    private void createDefaultRoomAndSpace(Channel channel) {
        Room globalRoom = Room.builder()
                .channel(channel)
                .type(RoomType.ROOM_GLOBAL)
                .title("공용")
                .description("채널 공용 방")
                .imageUrl(null) // 이미지 추후 추가
                .build();
        this.roomRepository.save(globalRoom);
        Room captureRoom = Room.builder()
                .channel(channel)
                .type(RoomType.ROOM_CAPTURE)
                .title("방금 캡처 됨")
                .description("채널 캡처 방")
                .imageUrl(null) // 이미지 추후 추가
                .build();
        this.roomRepository.save(captureRoom);
        Space globalFeed = Space.builder()
                .room(globalRoom)
                .role(SpaceRole.SPACEROLE_GUEST)
                .type(SpaceType.SPACE_FEED)
                .prev(null)
                .build();
        Space savedFeed = this.spaceRepository.save(globalFeed);
        Space globalChat = Space.builder()
                .room(globalRoom)
                .role(SpaceRole.SPACEROLE_GUEST)
                .type(SpaceType.SPACE_CHAT)
                .prev(savedFeed)
                .build();
        this.spaceRepository.save(globalChat);
        Space feed = Space.builder()
                .room(captureRoom)
                .role(SpaceRole.SPACEROLE_GUEST)
                .type(SpaceType.SPACE_FEED)
                .prev(null)
                .build();
        this.spaceRepository.save(feed);
    }
}
