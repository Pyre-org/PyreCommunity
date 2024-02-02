package com.pyre.community.service;


import com.pyre.community.dto.request.*;
import com.pyre.community.dto.response.*;
import org.springframework.transaction.annotation.Transactional;


public interface ChannelService {
    @Transactional
    ChannelCreateViewDto createChannel(ChannelCreateDto channelCreateDto, long userId);
    @Transactional
    ChannelGetViewDto getChannel(long id);
    @Transactional
    ChannelGetAllViewDto getAllChannelByUser(
            long userId, String token
    );
    @Transactional
    ChannelGetAllViewDto getAllChannelByUserAndSearch(
            long userId, String token,
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
    String updateChannelApprovalStatus(String accessToken, long channelId, ChannelUpdateApprovalStatusDto allow);
    @Transactional
    ChannelGetViewDto editChannel(String accessToken, long channelId, ChannelEditDto channelEditDto);
    @Transactional
    ChannelGetAllViewDto viewWaitApprovalChannel(String accessToken, int page, int count);
    @Transactional
    ChannelGetGenresResponseDto getGenres(String name);
    @Transactional
    ChannelJoinResponse joinChannel(long userId, String accessToken, ChannelJoinRequest request);
    @Transactional
    void locateChannel(long userId, String accessToken, ChannelLocateRequest request);
    @Transactional
    void deleteChannel(long userId, long channelId, String token);

    @Transactional
    void leaveChannel(long userId, long channelId);
    @Transactional
    void banMember(long userId, long channelId, long targetId);
}

