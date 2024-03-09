package com.pyre.community.controller;

import com.pyre.community.dto.request.*;
import com.pyre.community.dto.response.*;
import com.pyre.community.enumeration.ChannelGenre;
import com.pyre.community.service.ChannelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

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
    @Parameters({
            @Parameter(name = "userId", description = "액세스 토큰 아이디", required = true, example = "afasdwq-xcvxwe-sacdsd"),
    })
    public ResponseEntity<ChannelCreateViewDto> createChannel(@RequestBody @Valid ChannelCreateDto channelCreateDto, @RequestHeader(value = "id") String userId) {
        return new ResponseEntity<>(this.channelService.createChannel(channelCreateDto, UUID.fromString(userId)), HttpStatus.OK);
    }
    @Operation(description = "유저의 모든 채널 가져오기")
    @GetMapping()
    @Parameters({
            @Parameter(name = "token", description = "액세스 토큰", in = ParameterIn.HEADER, required = true, example = "accestoken.accesstoken"),
            @Parameter(name = "userId", description = "액세스 토큰 아이디", in = ParameterIn.HEADER, required = true, example = "adsaf-wqdasd-zvczv")
    })
    public ResponseEntity<ChannelGetAllViewDto> getCommunityList(
            @RequestHeader("Authorization") String token,
            @RequestHeader("id")  String userId
    ) {
        ChannelGetAllViewDto response = this.channelService.getAllChannelByUser(UUID.fromString(userId), token);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @Operation(description = "유저의 채널 검색 및 정렬")
    @GetMapping("/my/search")
    @Parameters({
            @Parameter(name = "token", description = "액세스 토큰", in = ParameterIn.HEADER, required = true, example = "accestoken.accesstoken"),
            @Parameter(name = "userId", description = "액세스 토큰 아이디", in = ParameterIn.HEADER, required = true, example = "asdf-qwe-fsvcx"),
            @Parameter(name = "genre", description = "장르", in = ParameterIn.QUERY, example = "FPS"),
            @Parameter(name = "sortBy", description = "정렬 기준", in = ParameterIn.QUERY, example = "title"),
            @Parameter(name = "keyword", description = "검색어", in = ParameterIn.QUERY, example = "오버워"),
            @Parameter(name = "orderByDesc", description = "내림차순", in = ParameterIn.QUERY, example = "true")
    })
    public ResponseEntity<ChannelGetAllViewDto> getCommunityListBySearch(
            @RequestHeader("Authorization")  String token,
            @RequestHeader("id") String userId,
            @RequestParam(value = "genre", defaultValue = "GENERAL")  String genre,
            @RequestParam(value = "sortBy", defaultValue = "title")  String sortBy,
            @RequestParam(value = "keyword", defaultValue = "") String keyword,
            @RequestParam(value = "orderByDesc", defaultValue = "true") Boolean orderByDesc
    ) {
        ChannelGetAllViewDto response = this.channelService.getAllChannelByUserAndSearch(UUID.fromString(userId), token, ChannelGenre.valueOf(genre), sortBy, keyword, orderByDesc);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @GetMapping("/get/{channelId}")
    @Operation(description = "채널 가져오기")
    @Parameters({
            @Parameter(name = "channelId", description = "채널 UUID", in = ParameterIn.PATH, required = true, example = "asdf-qwe-vcx")
    })
    public ResponseEntity<ChannelGetViewDto> getChannel(@PathVariable String channelId) {
        return new ResponseEntity<>(this.channelService.getChannel(UUID.fromString(channelId)), HttpStatus.OK);
    }
    @GetMapping("/list")
    @Operation(description = "모든 채널 검색 및 정렬")
    @Parameters({
            @Parameter(name = "page", description = "페이지", in = ParameterIn.QUERY, required = true, example = "0"),
            @Parameter(name = "count", description = "페이지 내 아이템 수", in = ParameterIn.QUERY, required = true, example = "20"),
            @Parameter(name = "genre", description = "장르", in = ParameterIn.QUERY, example = "FPS"),
            @Parameter(name = "sortBy", description = "정렬 기준", in = ParameterIn.QUERY, example = "title"),
            @Parameter(name = "keyword", description = "검색어", in = ParameterIn.QUERY, example = "오버워"),
            @Parameter(name = "orderByDesc", description = "내림차순", in = ParameterIn.QUERY, example = "true")
    })
    public ResponseEntity<ChannelGetAllViewDto> getAllChannel(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "count", defaultValue = "50") int count,
            @RequestParam(value = "genre", defaultValue = "GENERAL") String genre,
            @RequestParam(value = "sortBy", defaultValue = "title") String sortBy,
            @RequestParam(value = "keyword", defaultValue = "") String keyword,
            @RequestParam(value = "orderByDesc", defaultValue = "true") Boolean orderByDesc
    ) {
        return new ResponseEntity<>(this.channelService.getAllChannel(page, count, ChannelGenre.valueOf(genre), sortBy, keyword, orderByDesc), HttpStatus.OK);
    }
    @PatchMapping("/approval/{channelId}")
    @Operation(description = "채널 수용 승인 및 변경")
    @Parameters({
            @Parameter(name = "accessToken", description = "액세스 토큰", required = true, in = ParameterIn.HEADER, example = "accestoken.accesstoken"),
            @Parameter(name = "channelId", description = "채널 UUID", required = true, in = ParameterIn.PATH, example = "asfdsfq-adascvcx-asd")
    })
    public ResponseEntity<String> updateChannelApprovalStatus(
            @RequestHeader(value = "Authorization") String accessToken,
            @RequestBody ChannelUpdateApprovalStatusDto allow,
            @PathVariable String channelId) {
        return new ResponseEntity<>(this.channelService.updateChannelApprovalStatus(accessToken, UUID.fromString(channelId), allow), HttpStatus.OK);
    }
    @PutMapping("/edit/{channelId}")
    @Operation(description = "채널 정보 수정")
    @Parameters({
            @Parameter(name = "accessToken", description = "액세스 토큰", required = true, in = ParameterIn.HEADER, example = "accestoken.accesstoken"),
            @Parameter(name = "channelId", description = "채널 UUID", required = true, in = ParameterIn.PATH, example = "asfadf-vcsrew-dasd")
    })
    public ResponseEntity<ChannelGetViewDto> editChannel(
            @RequestHeader(value = "Authorization") String accessToken,
            @RequestBody ChannelEditDto channelEditDto,
            @PathVariable String channelId) {
        return new ResponseEntity<>(this.channelService.editChannel(accessToken, UUID.fromString(channelId), channelEditDto), HttpStatus.OK);
    }
    @GetMapping("/genres")
    @Operation(description = "채널의 모든 장르 리스트를 가져옴")
    @Parameters({
            @Parameter(name = "name", description = "장르 이름", in = ParameterIn.QUERY, example = "FP")
    })
    public ResponseEntity<ChannelGetGenresResponseDto> getGenres(
            @RequestParam(value = "name", defaultValue = "") String name
    ) {
        return new ResponseEntity<>(this.channelService.getGenres(name), HttpStatus.OK);
    }
    @GetMapping("/approval/list")
    @Operation(description = "채널 수용 보류 중인 채널 확인")
    @Parameters({
            @Parameter(name = "accessToken", description = "액세스 토큰", required = true, in = ParameterIn.HEADER, example = "accestoken.accesstoken"),
            @Parameter(name = "page", description = "페이지", required = true, in = ParameterIn.PATH, example = "0"),
            @Parameter(name = "count", description = "페이지 내 아이템 수", required = true, in = ParameterIn.PATH, example = "20")
    })
    public ResponseEntity<ChannelGetAllViewDto> viewWaitApprovalChannel(
            @RequestHeader(value = "Authorization") String accessToken,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "count", defaultValue = "50") int count
    ) {
        return new ResponseEntity<>(this.channelService.viewWaitApprovalChannel(accessToken, page, count), HttpStatus.OK);
    }
    @Operation(description = "채널 참가하기")
    @PostMapping("/join")
    @Parameters({
            @Parameter(name = "accessToken", description = "액세스 토큰", required = true, in = ParameterIn.HEADER, example = "accestoken.accesstoken"),
            @Parameter(name = "userId", description = "액세스 토큰 아이디", required = true, in = ParameterIn.HEADER, example = "asdafwq-aczv-asdfqw")
    })
    public ResponseEntity<ChannelJoinResponse> join(
            @RequestHeader("Authorization") String token,
            @RequestHeader("id") String userId,
            @RequestBody ChannelJoinRequest request
            ) {
        log.info("POST /community-server/community/member");

        ChannelJoinResponse response = this.channelService.joinChannel(UUID.fromString(userId), token, request);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @Operation(description = "채널 위치 옮기기")
    @PatchMapping("/locate")
    @Parameters({
            @Parameter(name = "accessToken", description = "액세스 토큰", required = true, in = ParameterIn.HEADER, example = "accestoken.accesstoken"),
            @Parameter(name = "userId", description = "액세스 토큰 아이디", required = true, in = ParameterIn.HEADER, example = "asdqwe-qweaxc-cxvxc")
    })
    public ResponseEntity<String> locateChannel(
            @RequestHeader("Authorization") String token,
            @RequestHeader("id") String userId,
            @RequestBody ChannelLocateRequest request
    ) {
        this.channelService.locateChannel(UUID.fromString(userId), token, request);
        return new ResponseEntity<>("성공적으로 위치가 변경되었습니다.", HttpStatus.OK);
    }
    @Operation(description = "채널 삭제하기")
    @DeleteMapping("/delete/{channelId}")
    @Parameters({
            @Parameter(name = "userId", description = "액세스 토큰 아이디", required = true, in = ParameterIn.HEADER, example = "asdvqwewq-fqwesdvcx"),
            @Parameter(name = "channelId", description = "채널 UUID", required = true, in = ParameterIn.PATH, example = "asdwqer-svcsv-ewrwer"),
            @Parameter(name = "token", description = "액세스 토큰", required = true, in = ParameterIn.HEADER, example = "accestoken.accesstoken"),
    })
    public ResponseEntity<?> deleteChannel(
            @RequestHeader("id") String userId,
            @PathVariable String channelId,
            @RequestHeader("Authorization") String token
    ) {
        this.channelService.deleteChannel(UUID.fromString(userId), UUID.fromString(channelId), token);
        return new ResponseEntity<>("성공적으로 채널이 삭제 되었습니다.", HttpStatus.OK);
    }
    @Operation(description = "채널 탈퇴하기")
    @DeleteMapping("/leave/{channelId}")
    @Parameters({
            @Parameter(name = "userId", description = "액세스 토큰 아이디", required = true, in = ParameterIn.HEADER, example = "asdqwe-qwedasc-vcxv"),
            @Parameter(name = "channelId", description = "채널 UUID", required = true, in = ParameterIn.PATH, example = "asdqwesadxc-vcxvewr"),
    })
    public ResponseEntity<?> deleteMember(
        @RequestHeader("id") String userId,
        @PathVariable String channelId
    ) {
        this.channelService.leaveChannel(UUID.fromString(userId), UUID.fromString(channelId));
        return new ResponseEntity<>("성공적으로 탈퇴 되었습니다.", HttpStatus.OK);
    }
    @Operation(description = "사용자 밴 하기")
    @DeleteMapping("/ban/{channelId}")
    @Parameters({
            @Parameter(name = "userId", description = "액세스 토큰 아이디", required = true, in = ParameterIn.HEADER, example = "asdwqe-cxzv-ewref-asd"),
            @Parameter(name = "channelId", description = "채널 UUID", required = true, in = ParameterIn.PATH, example = "asdwqe-xcewf-vcs"),
            @Parameter(name = "targetId", description = "타겟 유저 UUID", required = true, in = ParameterIn.QUERY, example = "sadwqe-wqc-vsdewr"),
    })
    public ResponseEntity<?> banMember(
            @RequestHeader("id") String userId,
            @PathVariable String channelId,
            @RequestParam(name = "id") String targetId
    ) {
        log.info("DELETE /community/channel/ban/{}",channelId);
        this.channelService.banMember(UUID.fromString(userId), UUID.fromString(channelId), UUID.fromString(targetId));
        return new ResponseEntity<>("성공적으로 해당 유저가 차단 되었습니다.", HttpStatus.OK);
    }
    @GetMapping("/isSubscribe/{channelId}")
    @Operation(description = "채널 구독 확인")
    @Parameters({
            @Parameter(name = "userId", description = "액세스 토큰 아이디", required = true, in = ParameterIn.HEADER, example = "asdwqe-qweqwe-vcxv"),
            @Parameter(name = "channelId", description = "채널 UUID", required = true, in = ParameterIn.PATH, example = "asdwqe-qweqwe-vcxv"),
    })
    public ResponseEntity<Boolean> isSubscribed(
            @RequestHeader("id") String userId,
            @PathVariable String channelId
    ) {
        return new ResponseEntity<>(this.channelService.isSubscribed(UUID.fromString(userId), UUID.fromString(channelId)), HttpStatus.OK);
    }
}
