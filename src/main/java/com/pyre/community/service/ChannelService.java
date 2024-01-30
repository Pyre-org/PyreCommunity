package com.pyre.community.service;


import com.pyre.community.dto.request.ChannelCreateDto;
import com.pyre.community.dto.request.ChannelEditDto;
import com.pyre.community.dto.request.ChannelUpdateApprovalStatusDto;
import com.pyre.community.dto.response.ChannelCreateViewDto;
import com.pyre.community.dto.response.ChannelGetAllViewDto;
import com.pyre.community.dto.response.ChannelGetGenresResponseDto;
import com.pyre.community.dto.response.ChannelGetViewDto;
import org.springframework.transaction.annotation.Transactional;


public interface ChannelService {
    @Transactional
    ChannelCreateViewDto createChannel(ChannelCreateDto channelCreateDto, long userId);
    @Transactional
    ChannelGetViewDto getChannel(long id);
    @Transactional
    ChannelGetAllViewDto getAllChannel(
            int page,
            int count,
            String genre,
            String sortBy,
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
}

