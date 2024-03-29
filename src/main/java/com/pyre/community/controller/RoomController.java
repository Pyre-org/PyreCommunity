package com.pyre.community.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.pyre.community.dto.request.*;
import com.pyre.community.dto.response.*;
import com.pyre.community.enumeration.RoomRole;
import com.pyre.community.enumeration.RoomType;
import com.pyre.community.service.RoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@Validated
@RequiredArgsConstructor
@Slf4j
@RequestMapping(value = "/community/room")
@Tag(name="Room", description = "Room API 구성")
public class RoomController {
    private final RoomService roomService;

    @PostMapping("/create")
    @Operation(description = "룸 생성하기")
    @Parameters({
            @Parameter(name = "token", description = "액세스 토큰 아이디", in = ParameterIn.HEADER, required = true, example = "asdqwfsdf-vcxvasd-asd")
    })
    public ResponseEntity<RoomCreateResponse> createRoom(
            @RequestBody @Valid RoomCreateRequest roomCreateRequest,
            @RequestHeader("id") String token
    ) throws JsonProcessingException {
        return new ResponseEntity<>(this.roomService.createRoom(UUID.fromString(token), roomCreateRequest), HttpStatus.OK);
    }
    @GetMapping("/get/{roomId}")
    @Operation(description = "공개된, 오픈된, 소속된 룸 정보 가져오기 검색 전용")
    @Parameters({
            @Parameter(name = "roomId", description = "룸 UUID", required = true, in = ParameterIn.PATH, example = "dqweqw-dascavcsd-vewrewr"),
            @Parameter(name = "userId", description = "액세스 토큰 아이디", in = ParameterIn.HEADER, required = true, example = "dqweqwd-asdcvcv-sdfsd")
    })
    public ResponseEntity<RoomGetResponse> getRoom(
            @PathVariable String roomId,
            @RequestHeader("id") String userId
    ) {
        return new ResponseEntity<>(this.roomService.getRoom(UUID.fromString(roomId), UUID.fromString(userId)), HttpStatus.OK);
    }
    @GetMapping("/list/{channelId}")
    @Operation(description = "채널의 공개된, 오픈된 모든 룸 가져오기 검색 전용")
    @Parameters({
            @Parameter(name = "channelId", description = "채널 UUID", required = true, in = ParameterIn.PATH, example = "dqweqwd-asdcvcv-sdfsd"),
            @Parameter(name = "keyword", description = "검색어", in = ParameterIn.QUERY, example = "오버워"),
            @Parameter(name = "type", description = "룸 타입", in = ParameterIn.QUERY, example = "ROOM_PUBLIC")
    })
    public ResponseEntity<RoomListByChannelResponse> listByChannelAndKeywordAndType (
            @PathVariable String channelId,
            @RequestParam(value = "keyword", defaultValue = "") String keyword,
            @RequestParam(value = "type", defaultValue = "ROOM_PUBLIC") String type,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size
    ) {
        return new ResponseEntity<>(this.roomService.listByChannelAndKeywordAndType(UUID.fromString(channelId), keyword, type, page, size), HttpStatus.OK);
    }
    @GetMapping("/my/list/{channelId}")
    @Operation(description = "채널의 소속된 모든 룸 가져오기 검색 전용")
    @Parameters({
            @Parameter(name = "channelId", description = "채널 UUID", required = true, in = ParameterIn.PATH, example = "dqweqwd-asdcvcv-sdfsd"),
            @Parameter(name = "keyword", description = "검색어", in = ParameterIn.QUERY, example = "오버워"),
            @Parameter(name = "userId", description = "액세스 토큰 아이디", in = ParameterIn.HEADER, required = true, example = "dqweqwd-asdcvcv-sdfsd")
    })
    public ResponseEntity<RoomListByChannelResponse> listByChannelAndKeywordAndUserId (
            @PathVariable String channelId,
            @RequestParam(value = "keyword", defaultValue = "") String keyword,
            @RequestHeader("id") String userId
    ) {
        return new ResponseEntity<>(this.roomService.listByChannelAndKeywordAndUserId(UUID.fromString(channelId), keyword, UUID.fromString(userId)), HttpStatus.OK);
    }
    @GetMapping("/my/{channelId}")
    @Operation(description = "채널의 소속된 모든 룸 + 스페이스 가져오기 순서대로")
    @Parameters({
            @Parameter(name = "channelId", description = "채널 UUID", required = true, in = ParameterIn.PATH, example = "dqweqwd-asdcvcv-sdfsd"),
            @Parameter(name = "userId", description = "액세스 토큰 아이디", in = ParameterIn.HEADER, required = true, example = "dqweqwd-asdcvcv-sdfsd")
    })
    public ResponseEntity<RoomGetDetailListResponse> listByChannelAndKeywordAndUserId (
            @PathVariable String channelId,
            @RequestHeader("id") String userId
    ) throws JsonProcessingException {
        return new ResponseEntity<>(this.roomService.listByChannelAndUserIdByIndexing(UUID.fromString(channelId), UUID.fromString(userId)), HttpStatus.OK);
    }
    @PostMapping("/join/{roomId}")
    @Operation(description = "해당 룸 아이디로 가입합니다.")
    @Parameters({
            @Parameter(name = "roomId", description = "룸 UUID", required = true, in = ParameterIn.PATH, example = "dqweqwd-asdcvcv-sdfsd"),
            @Parameter(name = "userId", description = "액세스 토큰 아이디", in = ParameterIn.HEADER, required = true, example = "dqweqwd-asdcvcv-sdfsd")
    })
    public ResponseEntity<RoomJoinResponse> joinRoom(
            @PathVariable String roomId,
            @RequestHeader("id") String userId,
            @RequestBody @Valid RoomJoinRequest roomJoinRequest
    ) {
        return new ResponseEntity<>(this.roomService.joinRoom(UUID.fromString(roomId), UUID.fromString(userId), UUID.fromString(roomJoinRequest.channelId())), HttpStatus.OK);
    }
    @PostMapping("/leave/{roomId}")
    @Operation(description = "해당 룸 아이디로 룸을 탈퇴합니다.")
    @Parameters({
            @Parameter(name = "roomId", description = "룸 UUID", required = true, in = ParameterIn.PATH, example = "dqweqwd-asdcvcv-sdfsd"),
            @Parameter(name = "userId", description = "액세스 토큰 아이디", in = ParameterIn.HEADER, required = true, example = "dqweqwd-asdcvcv-sdfsd")
    })
    public ResponseEntity<UUID> leaveRoom(
            @PathVariable String roomId,
            @RequestHeader("id") String userId
    ) {
        return new ResponseEntity<>(this.roomService.leaveRoom(UUID.fromString(roomId), UUID.fromString(userId)), HttpStatus.OK);
    }
    @PutMapping("/update/{roomId}")
    @Operation(description = "해당 룸 아이디로 룸을 수정합니다.")
    @Parameters({
            @Parameter(name = "roomId", description = "룸 UUID", required = true, in = ParameterIn.PATH, example = "dqweqwd-asdcvcv-sdfsd"),
            @Parameter(name = "userId", description = "액세스 토큰 아이디", in = ParameterIn.HEADER, required = true, example = "dqweqwd-asdcvcv-sdfsd")
    })
    public ResponseEntity<String> updateRoom(
            @PathVariable String roomId,
            @RequestHeader("id") String userId,
            @RequestBody @Valid RoomUpdateRequest roomUpdateRequest
    ) {
        return new ResponseEntity<>(this.roomService.updateRoom(UUID.fromString(roomId), UUID.fromString(userId), roomUpdateRequest), HttpStatus.OK);
    }
    @DeleteMapping("/delete/{roomId}")
    @Operation(description = "해당 룸 아이디로 룸을 삭제합니다.")
    @Parameters({
            @Parameter(name = "roomId", description = "룸 UUID", required = true, in = ParameterIn.PATH, example = "dqweqwd-asdcvcv-sdfsd"),
            @Parameter(name = "userId", description = "액세스 토큰 아이디", in = ParameterIn.HEADER, required = true, example = "dqweqwd-asdcvcv-sdfsd")
    })
    public ResponseEntity<UUID> deleteRoom(
            @PathVariable String roomId,
            @RequestHeader("id") String userId
    ) {
        return new ResponseEntity<>(this.roomService.deleteRoom(UUID.fromString(roomId), UUID.fromString(userId)), HttpStatus.OK);
    }
    @PatchMapping("/locate")
    @Operation(description = "해당 룸 아이디로 룸의 위치를 변경합니다.")
    @Parameters({
            @Parameter(name = "userId", description = "액세스 토큰 아이디", in = ParameterIn.HEADER, required = true, example = "dqweqwd-asdcvcv-sdfsd")
    })
    public ResponseEntity<String> locateRoom(
            @RequestHeader("id") String userId,
            @RequestBody @Valid RoomLocateRequest roomLocateRequest
    ) {
        return new ResponseEntity<>(this.roomService.locateRoom(UUID.fromString(userId), roomLocateRequest), HttpStatus.OK);
    }
    @PatchMapping("/role")
    @Operation(description = "해당 룸의 유저의 역할을 변경합니다.")
    @Parameters({
            @Parameter(name = "userId", description = "액세스 토큰 아이디", in = ParameterIn.HEADER, required = true, example = "dqweqwd-asdcvcv-sdfsd")
    })
    public ResponseEntity<String> updateUserRole(
            @RequestHeader("id") String userId,
            @RequestBody @Valid RoomEndUserRoleUpdateRequest roomEndUserRoleUpdateRequest
    ) {
        return new ResponseEntity<>(this.roomService.updateUserRole(UUID.fromString(userId), roomEndUserRoleUpdateRequest), HttpStatus.OK);
    }
    @GetMapping("/isSubscribe/{roomId}")
    @Operation(description = "룸 구독 확인")
    @Parameters({
            @Parameter(name = "userId", description = "액세스 토큰 아이디", required = true, in = ParameterIn.HEADER, example = "asdwqe-qweqwe-vcxv"),
            @Parameter(name = "roomId", description = "룸 UUID", required = true, in = ParameterIn.PATH, example = "asdwqe-qweqwe-vcxv"),
    })
    public ResponseEntity<Boolean> isSubscribed(
            @RequestHeader("id") String userId,
            @PathVariable String roomId
    ) {
        return new ResponseEntity<>(this.roomService.isSubscribed(UUID.fromString(userId), UUID.fromString(roomId)), HttpStatus.OK);
    }
    @DeleteMapping("/ban")
    @Operation(description = "해당 룸의 유저를 밴합니다.")
    @Parameters({
            @Parameter(name = "userId", description = "액세스 토큰 아이디", in = ParameterIn.HEADER, required = true, example = "dqweqwd-asdcvcv-sdfsd")
    })
    public ResponseEntity<String> banUser(
            @RequestHeader("id") String userId,
            @RequestBody @Valid RoomEndUserBanRequest roomEndUserBanRequest
    ) {
        return new ResponseEntity<>(this.roomService.banUser(UUID.fromString(userId), roomEndUserBanRequest), HttpStatus.OK);
    }
    @PatchMapping("/unban")
    @Operation(description = "해당 룸의 유저를 언밴합니다.")
    @Parameters({
            @Parameter(name = "userId", description = "액세스 토큰 아이디", in = ParameterIn.HEADER, required = true, example = "dqweqwd-asdcvcv-sdfsd")
    })
    public ResponseEntity<String> unbanUser(
            @RequestHeader("id") String userId,
            @RequestBody @Valid RoomEndUserUnbanRequest roomEndUserUnBanRequest
    ) {
        return new ResponseEntity<>(this.roomService.unbanUser(UUID.fromString(userId), roomEndUserUnBanRequest), HttpStatus.OK);
    }
    @GetMapping("/role/{roomId}")
    @Operation(description = "해당 룸의 유저의 역할을 확인합니다.")
    @Parameters({
            @Parameter(name = "userId", description = "액세스 토큰 아이디", in = ParameterIn.HEADER, required = true, example = "dqweqwd-asdcvcv-sdfsd"),
            @Parameter(name = "roomId", description = "룸 UUID", in = ParameterIn.PATH, required = true, example = "dqweqwd-asdcvcv-sdfsd")
    })
    public ResponseEntity<RoomRole> getRole(
            @RequestHeader("id") String userId,
            @PathVariable String roomId
    ) {
        return new ResponseEntity<>(this.roomService.getRoomRole(UUID.fromString(userId), UUID.fromString(roomId)), HttpStatus.OK);
    }
    @PostMapping("/invitation")
    @Operation(description = "해당 룸의 초대장을 개설합니다.")
    @Parameters({
            @Parameter(name = "userId", description = "액세스 토큰 아이디", in = ParameterIn.HEADER, required = true, example = "dqweqwd-asdcvcv-sdfsd")
    })
    public ResponseEntity<String> createInvitation(
            @RequestHeader("id") String userId,
            @RequestBody @Valid RoomInvitationCreateRequest roomInvitationCreateRequest
    ) {
        return new ResponseEntity<>(this.roomService.createInvitation(UUID.fromString(userId), roomInvitationCreateRequest), HttpStatus.OK);
    }
    @GetMapping("/invitation")
    @Operation(description = "해당 룸의 초대장 정보를 확인합니다.")
    @Parameters({
            @Parameter(name = "roomId", description = "룸 UUID", in = ParameterIn.QUERY, required = true, example = "dqweqwd-asdcvcv-sdfsd"),
            @Parameter(name = "userId", description = "액세스 토큰 아이디", in = ParameterIn.HEADER, required = true, example = "dqweqwd-asdcvcv-sdfsd")
    })
    public ResponseEntity<RoomInvitationLinkResponse> getInvitationLink(
            @RequestHeader("id") String userId,
            @RequestParam String roomId
    ) {
        return new ResponseEntity<>(this.roomService.getInvitationLink(UUID.fromString(userId), UUID.fromString(roomId)), HttpStatus.OK);
    }

    @GetMapping("/invitation/{invitationId}")
    @Operation(description = "해당 룸의 초대장으로 룸을 확인합니다.")
    @Parameters({
            @Parameter(name = "invitationId", description = "초대장 UUID", in = ParameterIn.PATH, required = true, example = "dqweqwd-asdcvcv-sdfsd"),
            @Parameter(name = "userId", description = "액세스 토큰 아이디", in = ParameterIn.HEADER, required = true, example = "dqweqwd-asdcvcv-sdfsd")
    })
    public ResponseEntity<RoomGetResponse> getInvitation(
            @PathVariable String invitationId,
            @RequestHeader("id") String userId
    ) {
        return new ResponseEntity<>(this.roomService.getInvitation(UUID.fromString(userId), invitationId), HttpStatus.OK);
    }
    @PostMapping("/invitation/accept")
    @Operation(description = "해당 룸의 초대장으로 가입합니다.")
    @Parameters({
            @Parameter(name = "userId", description = "액세스 토큰 아이디", in = ParameterIn.HEADER, required = true, example = "dqweqwd-asdcvcv-sdfsd")
    })
    public ResponseEntity<RoomJoinResponse> acceptInvitation(
            @RequestHeader("id") String userId,
            @RequestBody @Valid RoomInvitationAcceptRequest roomInvitationAcceptRequest
    ) {
        return new ResponseEntity<>(this.roomService.acceptInvitation(UUID.fromString(userId), roomInvitationAcceptRequest), HttpStatus.OK);
    }
    @GetMapping("/members/{roomId}")
    @Operation(description = "해당 룸의 멤버 리스트를 가져옵니다.")
    @Parameters({
            @Parameter(name = "roomId", description = "룸 UUID", in = ParameterIn.PATH, required = true, example = "dqweqwd-asdcvcv-sdfsd"),
            @Parameter(name = "userId", description = "액세스 토큰 아이디", in = ParameterIn.HEADER, required = true, example = "dqweqwd-asdcvcv-sdfsd")
    })
    public ResponseEntity<RoomGetMemberListResponse> getMembers(
            @RequestHeader("id") String userId,
            @PathVariable String roomId
    ) {
        return new ResponseEntity<>(this.roomService.getMembers(UUID.fromString(userId), UUID.fromString(roomId)), HttpStatus.OK);
    }


}
