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
            @Parameter(name = "roomCreateRequest", description = "룸 생성 바디", required = true),
            @Parameter(name = "token", description = "액세스 토큰 아이디", in = ParameterIn.HEADER, required = true, example = "asdqwfsdf-vcxvasd-asd")
    })
    public ResponseEntity<RoomCreateResponse> createRoom(
            @RequestBody @Valid RoomCreateRequest roomCreateRequest,
            @RequestHeader("id") String token
    ) {
        return new ResponseEntity<>(this.roomService.createRoom(UUID.fromString(token), roomCreateRequest), HttpStatus.OK);
    }
    @GetMapping("/get/{id}")
    @Operation(description = "공개된, 오픈된, 소속된 룸 정보 가져오기 검색 전용")
    @Parameters({
            @Parameter(name = "id", description = "룸 UUID", required = true, in = ParameterIn.PATH, example = "dqweqw-dascavcsd-vewrewr"),
            @Parameter(name = "userId", description = "액세스 토큰 아이디", in = ParameterIn.HEADER, required = true, example = "dqweqwd-asdcvcv-sdfsd")
    })
    public ResponseEntity<RoomGetResponse> getRoom(
            @PathVariable String id,
            @RequestHeader("id") String userId
    ) {
        return new ResponseEntity<>(this.roomService.getRoom(UUID.fromString(id), UUID.fromString(userId)), HttpStatus.OK);
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
            @RequestParam(value = "type", defaultValue = "ROOM_PUBLIC") String type
    ) {
        return new ResponseEntity<>(this.roomService.listByChannelAndKeywordAndType(UUID.fromString(channelId), keyword, type), HttpStatus.OK);
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
    @Operation(description = "채널의 소속된 모든 룸 가져오기 순서대로")
    @Parameters({
            @Parameter(name = "channelId", description = "채널 UUID", required = true, in = ParameterIn.PATH, example = "dqweqwd-asdcvcv-sdfsd"),
            @Parameter(name = "userId", description = "액세스 토큰 아이디", in = ParameterIn.HEADER, required = true, example = "dqweqwd-asdcvcv-sdfsd")
    })
    public ResponseEntity<RoomListByChannelResponse> listByChannelAndKeywordAndUserId (
            @PathVariable String channelId,
            @RequestHeader("id") String userId
    ) {
        return new ResponseEntity<>(this.roomService.listByChannelAndUserIdByIndexing(UUID.fromString(channelId), UUID.fromString(userId)), HttpStatus.OK);
    }
    @PostMapping("/join/{roomId}")
    @Operation(description = "해당 룸 아이디로 가입합니다.")
    @Parameters({
            @Parameter(name = "roomId", description = "룸 UUID", required = true, in = ParameterIn.PATH, example = "dqweqwd-asdcvcv-sdfsd"),
            @Parameter(name = "userId", description = "액세스 토큰 아이디", in = ParameterIn.HEADER, required = true, example = "dqweqwd-asdcvcv-sdfsd"),
            @Parameter(name = "roomJoinRequest", description = "룸 조인 바디 (채널 UUID)", required = true)
    })
    public ResponseEntity<RoomJoinResponse> joinRoom(
            @PathVariable String roomId,
            @RequestHeader("id") String userId,
            @RequestBody RoomJoinRequest roomJoinRequest
    ) {
        return new ResponseEntity<>(this.roomService.joinRoom(UUID.fromString(roomId), UUID.fromString(userId), UUID.fromString(roomJoinRequest.channelId())), HttpStatus.OK);
    }
}
