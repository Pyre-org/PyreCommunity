package com.pyre.community.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.pyre.community.dto.request.*;
import com.pyre.community.dto.response.*;
import com.pyre.community.enumeration.RoomRole;
import org.springframework.transaction.annotation.Transactional;


import java.util.UUID;

public interface RoomService {
    @Transactional
    RoomCreateResponse createRoom(UUID userId, RoomCreateRequest roomCreateRequest);
    @Transactional(readOnly = true)
    RoomGetResponse getRoom(UUID id, UUID userId);
    @Transactional(readOnly = true)
    RoomListByChannelResponse listByChannelAndKeywordAndType(UUID channelId, String keyword, String type);
    @Transactional(readOnly = true)
    RoomListByChannelResponse listByChannelAndKeywordAndUserId(UUID channelId, String keyword, UUID userId);
    @Transactional(readOnly = true)
    RoomGetDetailListResponse listByChannelAndUserIdByIndexing(UUID channelId, UUID userId);
    @Transactional
    RoomJoinResponse joinRoom(UUID roomId, UUID userId, UUID channelId);
    @Transactional
    UUID leaveRoom(UUID roomId, UUID userId);
    @Transactional
    String updateRoom(UUID roomId, UUID userId, RoomUpdateRequest roomUpdateRequest);
    @Transactional
    UUID deleteRoom(UUID roomId, UUID userId);
    @Transactional
    String locateRoom(UUID userId, RoomLocateRequest roomLocateRequest);
    @Transactional
    String updateUserRole(UUID userId, RoomEndUserRoleUpdateRequest roomEndUserRoleUpdateRequest);
    @Transactional(readOnly = true)
    Boolean isSubscribed(UUID userId, UUID roomId);
    @Transactional
    String banUser(UUID userId, RoomEndUserBanRequest roomEndUserBanRequest);
    @Transactional
    String unbanUser(UUID userId, RoomEndUserUnbanRequest roomEndUserUnbanRequest);
    @Transactional(readOnly = true)
    RoomRole getRoomRole(UUID userId, UUID roomId);
    @Transactional
    String createInvitation(UUID userId, RoomInvitationCreateRequest roomInviteLinkCreateRequest);
    @Transactional(readOnly = true)
    RoomInvitationLinkResponse getInvitationLink(UUID userId, UUID roomId);
    @Transactional(readOnly = true)
    RoomGetResponse getInvitation(UUID userId, String inviteId);
    @Transactional
    RoomJoinResponse acceptInvitation(UUID userId, RoomInvitationAcceptRequest roomInviteLinkAcceptRequest);
    @Transactional(readOnly = true)
    RoomGetMemberListResponse getMembers(UUID userId, UUID roomId);
}
