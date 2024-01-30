package com.pyre.community.controller;

import com.pyre.community.dto.request.ChannelCreateDto;
import com.pyre.community.dto.request.ChannelEditDto;
import com.pyre.community.dto.request.ChannelUpdateApprovalStatusDto;
import com.pyre.community.dto.response.ChannelCreateViewDto;
import com.pyre.community.dto.response.ChannelGetAllViewDto;
import com.pyre.community.dto.response.ChannelGetGenresResponseDto;
import com.pyre.community.dto.response.ChannelGetViewDto;
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
    public ResponseEntity<ChannelCreateViewDto> createChannel(@RequestBody @Valid ChannelCreateDto channelCreateDto, @RequestHeader(value = "id") long userId) {
        return new ResponseEntity<>(this.channelService.createChannel(channelCreateDto, userId), HttpStatus.OK);
    }
    @GetMapping("/get/{id}")
    @Operation(description = "채널 가져오기")
    public ResponseEntity<ChannelGetViewDto> getChannel(@PathVariable long id) {
        return new ResponseEntity<>(this.channelService.getChannel(id), HttpStatus.OK);
    }

    @GetMapping("/list")
    @Operation(description = "모든 채널 가져오기")
    public ResponseEntity<ChannelGetAllViewDto> getAllChannel(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "count", defaultValue = "50") int count,
            @RequestParam(value = "genre", required = false) String genre,
            @RequestParam(value = "sortBy", defaultValue = "title") String sortBy,
            @RequestParam(value = "orderByDesc", defaultValue = "true") Boolean orderByDesc
    ) {
        return new ResponseEntity<>(this.channelService.getAllChannel(page, count, genre, sortBy, orderByDesc), HttpStatus.OK);
    }
    @PatchMapping("/approval/{id}")
    @Operation(description = "채널 수용 승인 및 변경")
    public ResponseEntity<String> updateChannelApprovalStatus(
            @RequestHeader(value = "Authorization") String accessToken,
            @RequestBody ChannelUpdateApprovalStatusDto allow,
            @PathVariable long id) {
        return new ResponseEntity<>(this.channelService.updateChannelApprovalStatus(accessToken, id, allow), HttpStatus.OK);
    }
    @PutMapping("/edit/{id}")
    @Operation(description = "채널 정보 수정")
    public ResponseEntity<ChannelGetViewDto> editChannel(
            @RequestHeader(value = "Authorization") String accessToken,
            @RequestBody ChannelEditDto channelEditDto,
            @PathVariable long id) {
        return new ResponseEntity<>(this.channelService.editChannel(accessToken, id, channelEditDto), HttpStatus.OK);
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



}
