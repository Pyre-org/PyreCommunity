package com.pyre.community.controller;

import com.pyre.community.dto.request.*;
import com.pyre.community.dto.response.*;
import com.pyre.community.service.ChannelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/community/channel")
@Tag(name="Channel", description = "Channel API 구성")
@Validated
public class ChannelController {
    private final ChannelService channelService;

    @PostMapping("/create")
    @Operation(description = "채널 생성")
    public ResponseEntity<ChannelCreateViewDto> createChannel(@RequestBody @Valid ChannelCreateDto channelCreateDto, @RequestHeader(value = "id") String userId) {
        return new ResponseEntity<>(this.channelService.createChannel(channelCreateDto, Long.parseLong(userId)), HttpStatus.OK);
    }
    @Operation(description = "유저의 모든 채널 가져오기")
    @GetMapping()
    public ResponseEntity<ChannelGetAllViewDto> getCommunityList(
            @RequestHeader("Authorization") String token,
            @RequestHeader("id") String userId
    ) {
        log.info("GET /community/channel");
        ChannelGetAllViewDto response = this.channelService.getAllChannelByUser(Long.parseLong(userId), token);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @Operation(description = "유저의 채널 검색 및 정렬")
    @GetMapping("/my/search")
    public ResponseEntity<ChannelGetAllViewDto> getCommunityListBySearch(
            @RequestHeader("Authorization") String token,
            @RequestHeader("id") String userId,
            @RequestParam(value = "genre", required = false) String genre,
            @RequestParam(value = "sortBy", defaultValue = "title") String sortBy,
            @RequestBody ChannelSearchRequest channelSearchRequest,
            @RequestParam(value = "orderByDesc", defaultValue = "true") Boolean orderByDesc
    ) {
        log.info("GET /community/channel");
        ChannelGetAllViewDto response = this.channelService.getAllChannelByUserAndSearch(Long.parseLong(userId), token, genre, sortBy, channelSearchRequest.keyword(), orderByDesc);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @GetMapping("/get/{channelId}")
    @Operation(description = "채널 가져오기")
    public ResponseEntity<ChannelGetViewDto> getChannel(@PathVariable long channelId) {
        return new ResponseEntity<>(this.channelService.getChannel(channelId), HttpStatus.OK);
    }
    @GetMapping("/list")
    @Operation(description = "모든 채널 검색 및 정렬")
    public ResponseEntity<ChannelGetAllViewDto> getAllChannel(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "count", defaultValue = "50") int count,
            @RequestParam(value = "genre", required = false) String genre,
            @RequestParam(value = "sortBy", defaultValue = "title") String sortBy,
            @RequestBody ChannelSearchRequest channelSearchRequest,
            @RequestParam(value = "orderByDesc", defaultValue = "true") Boolean orderByDesc
    ) {
        return new ResponseEntity<>(this.channelService.getAllChannel(page, count, genre, sortBy, channelSearchRequest.keyword(), orderByDesc), HttpStatus.OK);
    }
    @PatchMapping("/approval/{channelId}")
    @Operation(description = "채널 수용 승인 및 변경")
    public ResponseEntity<String> updateChannelApprovalStatus(
            @RequestHeader(value = "Authorization") String accessToken,
            @RequestBody ChannelUpdateApprovalStatusDto allow,
            @PathVariable long channelId) {
        return new ResponseEntity<>(this.channelService.updateChannelApprovalStatus(accessToken, channelId, allow), HttpStatus.OK);
    }
    @PutMapping("/edit/{channelId}")
    @Operation(description = "채널 정보 수정")
    public ResponseEntity<ChannelGetViewDto> editChannel(
            @RequestHeader(value = "Authorization") String accessToken,
            @RequestBody ChannelEditDto channelEditDto,
            @PathVariable long channelId) {
        return new ResponseEntity<>(this.channelService.editChannel(accessToken, channelId, channelEditDto), HttpStatus.OK);
    }
    @GetMapping("/genres")
    @Operation(description = "채널의 모든 장르 리스트를 가져옴")
    public ResponseEntity<ChannelGetGenresResponseDto> getGenres(
            @RequestParam(value = "name", defaultValue = "") String name
    ) {
        return new ResponseEntity<>(this.channelService.getGenres(name), HttpStatus.OK);
    }
    @GetMapping("/approval/list")
    @Operation(description = "채널 수용 보류 중인 채널 확인")
    public ResponseEntity<ChannelGetAllViewDto> viewWaitApprovalChannel(
            @RequestHeader(value = "Authorization") String accessToken,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "count", defaultValue = "50") int count
    ) {
        return new ResponseEntity<>(this.channelService.viewWaitApprovalChannel(accessToken, page, count), HttpStatus.OK);
    }
    @Operation(description = "채널 참가하기")
    @PostMapping("/join")
    public ResponseEntity<ChannelJoinResponse> join(
            @RequestHeader("Authorization") String token,
            @RequestHeader("id") String userId,
            @RequestBody ChannelJoinRequest request
            ) {
        log.info("POST /community-server/community/member");

        ChannelJoinResponse response = this.channelService.joinChannel(Long.parseLong(userId), token, request);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @Operation(description = "채널 위치 옮기기")
    @PatchMapping("/locate")
    public ResponseEntity<String> locateChannel(
            @RequestHeader("Authorization") String token,
            @RequestHeader("id") String userId,
            @RequestBody ChannelLocateRequest request
    ) {
        this.channelService.locateChannel(Long.parseLong(userId), token, request);
        return new ResponseEntity<>("성공적으로 위치가 변경되었습니다.", HttpStatus.OK);
    }
    @Operation(description = "채널 삭제하기")
    @DeleteMapping("/delete/{channelId}")
    public ResponseEntity<?> deleteChannel(
            @RequestHeader("id") String userId,
            @PathVariable Long channelId,
            @RequestHeader("Authorization") String token
    ) {
        this.channelService.deleteChannel(Long.parseLong(userId), channelId, token);
        return new ResponseEntity<>("성공적으로 채널이 삭제 되었습니다.", HttpStatus.OK);
    }

    @Operation(description = "채널 탈퇴하기")
    @DeleteMapping("/leave/{channelId}")
    public ResponseEntity<?> deleteMember(
        @RequestHeader("id") String userId,
        @PathVariable Long channelId
    ) {
        this.channelService.leaveChannel(Long.parseLong(userId), channelId);
        return new ResponseEntity<>("성공적으로 탈퇴 되었습니다.", HttpStatus.OK);
    }
    @Operation(description = "사용자 밴 하기")
    @DeleteMapping("/ban/{channelId}")
    public ResponseEntity<?> banMember(
            @RequestHeader("id") String userId,
            @PathVariable Long channelId,
            @RequestParam(name = "id") Long targetId
    ) {
        log.info("DELETE /community/channel/ban/{}",channelId);
        this.channelService.banMember(Long.parseLong(userId), channelId, targetId);
        return new ResponseEntity<>("성공적으로 해당 유저가 차단 되었습니다.", HttpStatus.OK);
    }
}
