package com.pyre.community.service;


import com.pyre.community.dto.request.*;
import com.pyre.community.dto.response.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;


public interface ChannelService {
    @Transactional
    ChannelCreateViewDto createChannel(ChannelCreateDto channelCreateDto, UUID userId);
    @Transactional
    ChannelGetViewDto getChannel(UUID id);
    @Transactional
    ChannelGetAllViewDto getAllChannelByUser(
            UUID userId, String token
    );
    @Transactional
    ChannelGetAllViewDto getAllChannelByUserAndSearch(
            UUID userId, String token,
            String genre,
            String sortBy,
            String keyword,
            Boolean orderByDesc
    );
    @Transactional
    ChannelGetAllViewDto getAllChannel(
            int page,
            int count,
            String genre,
            String sortBy,
            String keyword,
            Boolean orderByDesc
    );
    @Transactional
    String updateChannelApprovalStatus(String accessToken, UUID channelId, ChannelUpdateApprovalStatusDto allow);
    @Transactional
    ChannelGetViewDto editChannel(String accessToken, UUID channelId, ChannelEditDto channelEditDto);
    @Transactional
    ChannelGetAllViewDto viewWaitApprovalChannel(String accessToken, int page, int count);
    @Transactional
    ChannelGetGenresResponseDto getGenres(String name);
    @Transactional
    ChannelJoinResponse joinChannel(UUID userId, String accessToken, ChannelJoinRequest request);
    @Transactional
    void locateChannel(UUID userId, String accessToken, ChannelLocateRequest request);
    @Transactional
    void deleteChannel(UUID userId, UUID channelId, String token);

    @Transactional
    void leaveChannel(UUID userId, UUID channelId);
    @Transactional
    void banMember(UUID userId, UUID channelId, UUID targetId);
}

