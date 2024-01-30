package com.pyre.community.service.impl;


import com.pyre.community.client.UserClient;
import com.pyre.community.dto.request.ChannelCreateDto;
import com.pyre.community.dto.request.ChannelEditDto;
import com.pyre.community.dto.request.ChannelUpdateApprovalStatusDto;
import com.pyre.community.dto.response.*;
import com.pyre.community.entity.Channel;
import com.pyre.community.enumeration.ApprovalStatus;
import com.pyre.community.enumeration.ChannelGenre;
import com.pyre.community.exception.customexception.AuthenticationFailException;
import com.pyre.community.exception.customexception.DataNotFoundException;
import com.pyre.community.repository.ChannelRepository;
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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChannelServiceImpl implements ChannelService {

    private final ChannelRepository channelRepository;
    private final UserClient userClient;


    @Override
    @Transactional
    public ChannelCreateViewDto createChannel(ChannelCreateDto channelCreateDto, long id) {
        ChannelGenre genre = channelCreateDto.genre();
        if (channelCreateDto.genre() == null) {
            genre = ChannelGenre.GENERAL;
        }
        Channel channel = Channel.createChannel(
                channelCreateDto.title(),
                channelCreateDto.description(),
                genre,
                channelCreateDto.imageUrl()
        );

        Channel savedChannel = this.channelRepository.save(channel);
        ChannelCreateViewDto channelCreateViewDto = ChannelCreateViewDto.makeDto(savedChannel);
        return channelCreateViewDto;
    }
    @Override
    @Transactional
    public ChannelGetViewDto getChannel(long id) {
        Optional<Channel> channel = this.channelRepository.findById(id);
        if (!channel.isPresent()) {
            throw new DataNotFoundException("해당 아이디를 가진 채널을 찾을 수 없습니다.");
        }
        Channel getChannel = channel.get();
        ChannelGetViewDto channelGetViewDto = ChannelGetViewDto.createChannelGetViewDto(getChannel);
        return channelGetViewDto;
    }
    @Override
    @Transactional
    public ChannelGetAllViewDto getAllChannel(
            int page,
            int count,
            String genre,
            String sortBy,
            Boolean orderByDesc
    ) {
        if (!(sortBy == "title" || sortBy == "cAt" || sortBy == "memberCounts" || sortBy == "roomCounts")) {
            sortBy = "title";
        }

        Sort sort = (orderByDesc) ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();


        Pageable pageable = PageRequest.of(page, count, sort);

        Page<Channel> channels = this.channelRepository.findAllByApprovalStatus(ApprovalStatus.ALLOW, pageable);
        if (genre != null) {
            channels = this.channelRepository.findAllByGenreAndApprovalStatus(genre, ApprovalStatus.ALLOW, pageable);
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
    public String updateChannelApprovalStatus(String accessToken, long channelId, ChannelUpdateApprovalStatusDto allow) {
        if (allow == null) {
            throw new RuntimeException("해당 요청은 바디가 필수입니다.");
        }
        UserInfoFeignResponse userInfo = this.userClient.getUserInfo(accessToken);
        Optional<Channel> channel = this.channelRepository.findById(channelId);
        if (userInfo == null) {
            throw new AuthenticationFailException("권한이 없습니다.");
        }
        if (userInfo.role() != "ROLE_ADMIN") {
            throw new AuthenticationFailException("권한이 없습니다.");
        }

        if (!channel.isPresent()) {
            throw new DataNotFoundException("해당 채널을 찾을 수 없습니다.");
        }
        Channel gotChannel = channel.get();

        if (allow.status().equals(ApprovalStatus.DENY)) {
            this.channelRepository.delete(gotChannel);
        } else {
            gotChannel.setApprovalStatus(allow.status());
            gotChannel.setMAt(LocalDateTime.now());
            this.channelRepository.save(gotChannel);
        }
        return "성공적으로 채널이 " + allow.status() +" 되었습니다.";
    }

    @Override
    @Transactional
    public ChannelGetViewDto editChannel(String accessToken, long channelId, ChannelEditDto channelEditDto) {
        UserInfoFeignResponse userInfo = this.userClient.getUserInfo(accessToken);
        Optional<Channel> channel = this.channelRepository.findById(channelId);
        if (userInfo == null) {
            throw new AuthenticationFailException("권한이 없습니다.");
        }
        if (userInfo.role() != "ROLE_ADMIN") {
            throw new AuthenticationFailException("권한이 없습니다.");
        }

        if (!channel.isPresent()) {
            throw new DataNotFoundException("해당 채널을 찾을 수 없습니다.");
        }
        Channel gotChannel = channel.get();
        Channel updatedChannel = Channel.updateChannel(
                channelEditDto.title(),
                channelEditDto.description(),
                channelEditDto.genre(),
                channelEditDto.imageUrl(),
                gotChannel
        );

        Channel savedChannel = this.channelRepository.save(updatedChannel);
        ChannelGetViewDto channelGetViewDto = ChannelGetViewDto.createChannelGetViewDto(savedChannel);
        return channelGetViewDto;
    }
    @Override
    @Transactional
    public ChannelGetAllViewDto viewWaitApprovalChannel(String accessToken, int page, int count) {

        UserInfoFeignResponse userInfo = this.userClient.getUserInfo(accessToken);
        if (userInfo == null) {
            throw new AuthenticationFailException("권한이 없습니다.");
        }
        if (userInfo.role() != "ROLE_ADMIN") {
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



    private String localDateToString(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        String dateString = localDateTime.format(DateTimeFormatter.ofPattern("YYYY-MM-dd HH:mm"));

        return dateString;
    }
}
