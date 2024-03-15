package com.pyre.community.service;


import com.pyre.community.dto.request.*;
import com.pyre.community.dto.response.*;
import com.pyre.community.enumeration.ChannelGenre;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;


public interface ChannelService {
    @Transactional
    ChannelCreateViewDto createChannel(ChannelCreateDto channelCreateDto, UUID userId);
    @Transactional(readOnly = true)
    ChannelGetViewDto getChannel(UUID id);
    @Transactional(readOnly = true)
    ChannelGetAllViewDto getAllChannelByUser(
            UUID userId, String token
    );
    @Transactional(readOnly = true)
    ChannelGetAllViewDto getAllChannelByUserAndSearch(
            UUID userId, String token,
            String genre,
            String sortBy,
            String keyword,
            Boolean orderByDesc
    );
    @Transactional(readOnly = true)
    ChannelGetAllViewDto getAllChannel(
            int page,
            int count,
            ChannelGenre genre,
            String sortBy,
            String keyword,
            Boolean orderByDesc
    );
    @Transactional
    String updateChannelApprovalStatus(String accessToken, UUID channelId, ChannelUpdateApprovalStatusDto allow);
    @Transactional
    ChannelGetViewDto editChannel(String accessToken, UUID channelId, ChannelEditDto channelEditDto);
    @Transactional(readOnly = true)
    ChannelGetAllViewDto viewWaitApprovalChannel(String accessToken, int page, int count);
    @Transactional(readOnly = true)
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
    @Transactional(readOnly = true)
    Boolean isSubscribed(UUID userId, UUID channelId);
}

