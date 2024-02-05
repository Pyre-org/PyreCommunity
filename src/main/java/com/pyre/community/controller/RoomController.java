package com.pyre.community.controller;

import com.pyre.community.dto.request.RoomCreateRequest;
import com.pyre.community.dto.request.RoomJoinRequest;
import com.pyre.community.dto.response.RoomCreateResponse;
import com.pyre.community.dto.response.RoomGetResponse;
import com.pyre.community.dto.response.RoomJoinResponse;
import com.pyre.community.dto.response.RoomListByChannelResponse;
import com.pyre.community.enumeration.RoomType;
import com.pyre.community.service.RoomService;
import io.swagger.v3.oas.annotations.Operation;
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
    public ResponseEntity<RoomCreateResponse> createRoom(
            @RequestBody @Valid RoomCreateRequest roomCreateRequest,
            @RequestHeader("id") String token
    ) {
        return new ResponseEntity<>(this.roomService.createRoom(UUID.fromString(token), roomCreateRequest), HttpStatus.OK);
    }
    @GetMapping("/get/{id}")
    @Operation(description = "공개된, 오픈된, 소속된 룸 정보 가져오기 검색 전용")
    public ResponseEntity<RoomGetResponse> getRoom(
            @PathVariable String id,
            @RequestHeader("id") String userId
    ) {
        return new ResponseEntity<>(this.roomService.getRoom(UUID.fromString(id), UUID.fromString(userId)), HttpStatus.OK);
    }
    @GetMapping("/list/{channelId}")
    @Operation(description = "채널의 공개된, 오픈된 모든 룸 가져오기 검색 전용")
    public ResponseEntity<RoomListByChannelResponse> listByChannelAndKeywordAndType (
            @PathVariable String channelId,
            @RequestParam(value = "keyword", defaultValue = "") String keyword,
            @RequestParam(value = "type", defaultValue = "ROOM_PUBLIC") String type
    ) {
        return new ResponseEntity<>(this.roomService.listByChannelAndKeywordAndType(UUID.fromString(channelId), keyword, type), HttpStatus.OK);
    }
    @GetMapping("/my/list/{channelId}")
    @Operation(description = "채널의 소속된 모든 룸 가져오기 검색 전용")
    public ResponseEntity<RoomListByChannelResponse> listByChannelAndKeywordAndUserId (
            @PathVariable String channelId,
            @RequestParam(value = "keyword", defaultValue = "") String keyword,
            @RequestHeader("id") String userId
    ) {
        return new ResponseEntity<>(this.roomService.listByChannelAndKeywordAndUserId(UUID.fromString(channelId), keyword, UUID.fromString(userId)), HttpStatus.OK);
    }
    @GetMapping("/my/{channelId}")
    @Operation(description = "채널의 소속된 모든 룸 가져오기 순서대로")
    public ResponseEntity<RoomListByChannelResponse> listByChannelAndKeywordAndUserId (
            @PathVariable String channelId,
            @RequestHeader("id") String userId
    ) {
        return new ResponseEntity<>(this.roomService.listByChannelAndUserIdByIndexing(UUID.fromString(channelId), UUID.fromString(userId)), HttpStatus.OK);
    }
    @PostMapping("/join/{roomId}")
    @Operation(description = "해당 룸 아이디로 가입합니다.")
    public ResponseEntity<RoomJoinResponse> joinRoom(
            @PathVariable String roomId,
            @RequestHeader("id") String userId,
            @RequestBody RoomJoinRequest roomJoinRequest
    ) {
        return new ResponseEntity<>(this.roomService.joinRoom(UUID.fromString(roomId), UUID.fromString(userId), UUID.fromString(roomJoinRequest.channelId())), HttpStatus.OK);
    }
}
