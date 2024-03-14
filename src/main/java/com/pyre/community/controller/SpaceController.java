package com.pyre.community.controller;

import com.pyre.community.dto.request.SpaceCreateRequest;
import com.pyre.community.dto.request.SpaceLocateRequest;
import com.pyre.community.dto.request.SpaceUpdateRequest;
import com.pyre.community.dto.response.SpaceCreateResponse;
import com.pyre.community.dto.response.SpaceGetListByRoomResponse;
import com.pyre.community.dto.response.SpaceGetResponse;
import com.pyre.community.service.SpaceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/community/space")
@Slf4j
@Validated
@Tag(name = "Space", description = "Space API 구성")
public class SpaceController {
    private final SpaceService spaceService;
    @PostMapping("/create")
    @Operation(description = "스페이스를 생성하는 엔드포인트")
    @Parameters({
            @Parameter(name = "userId", description = "액세스 토큰 아이디", in = ParameterIn.HEADER, required = true, example = "dqweqwd-asdcvcv-sdfsd")
    })
    public ResponseEntity<SpaceCreateResponse> createSpace(@RequestBody @Validated SpaceCreateRequest spaceCreateRequest, @RequestHeader("id") String userId) {
        return new ResponseEntity<>(this.spaceService.createSpace(spaceCreateRequest, UUID.fromString(userId)), HttpStatus.OK);
    }
    @GetMapping("/list")
    @Operation(description = "룸의 스페이스 리스트를 조회하는 엔드포인트")
    @Parameters({
            @Parameter(name = "roomId", description = "룸 UUID", required = true, in = ParameterIn.QUERY, example = "dqweqw-dascavcsd-vewrewr"),
            @Parameter(name = "userId", description = "액세스 토큰 아이디", in = ParameterIn.HEADER, required = true, example = "dqweqwd-asdcvcv-sdfsd")
    })
    public ResponseEntity<SpaceGetListByRoomResponse> getSpaceListByRoom(
            @RequestHeader("id") String userId,
            @RequestParam("roomId") String roomId
    ) {
        return new ResponseEntity<>(this.spaceService.getSpaceListByRoom(UUID.fromString(userId), roomId), HttpStatus.OK);
    }
    @GetMapping("/info/{spaceId}")
    @Operation(description = "스페이스 정보를 조회하는 엔드포인트")
    @Parameters({
            @Parameter(name = "roomId", description = "룸 UUID", required = true, in = ParameterIn.PATH, example = "dqweqw-dascavcsd-vewrewr"),
            @Parameter(name = "userId", description = "액세스 토큰 아이디", in = ParameterIn.HEADER, required = true, example = "dqweqwd-asdcvcv-sdfsd")
    })
    public ResponseEntity<SpaceGetResponse> getSpaceInfoByRoom(
            @RequestHeader("id") String userId,
            @PathVariable("spaceId") String roomId
    ) {
        return new ResponseEntity<>(this.spaceService.getSpace(UUID.fromString(userId), roomId), HttpStatus.OK);
    }
    @PutMapping("/update/{spaceId}")
    @Operation(description = "스페이스 정보를 수정하는 엔드포인트")
    @Parameters({
            @Parameter(name = "roomId", description = "룸 UUID", required = true, in = ParameterIn.PATH, example = "dqweqw-dascavcsd-vewrewr"),
            @Parameter(name = "userId", description = "액세스 토큰 아이디", in = ParameterIn.HEADER, required = true, example = "dqweqwd-asdcvcv-sdfsd")
    })
    public ResponseEntity<String> updateSpaceInfoByRoom(
            @RequestHeader("id") String userId,
            @PathVariable("spaceId") String roomId,
            @RequestBody @Validated SpaceUpdateRequest spaceUpdateRequest
    ) {
        return new ResponseEntity<>(this.spaceService.updateSpace(UUID.fromString(userId), roomId, spaceUpdateRequest), HttpStatus.OK);
    }
    @DeleteMapping("/delete/{spaceId}")
    @Operation(description = "스페이스를 삭제하는 엔드포인트")
    @Parameters({
            @Parameter(name = "roomId", description = "룸 UUID", required = true, in = ParameterIn.PATH, example = "dqweqw-dascavcsd-vewrewr"),
            @Parameter(name = "userId", description = "액세스 토큰 아이디", in = ParameterIn.HEADER, required = true, example = "dqweqwd-asdcvcv-sdfsd")
    })
    public ResponseEntity<String> deleteSpaceByRoom(
            @RequestHeader("id") String userId,
            @PathVariable("spaceId") String roomId
    ) {
        return new ResponseEntity<>(this.spaceService.deleteSpace(UUID.fromString(userId), roomId), HttpStatus.OK);
    }
    @PatchMapping("/locate")
    @Operation(description = "스페이스의 위치를 변경하는 엔드포인트")
    @Parameters({
            @Parameter(name = "roomId", description = "룸 UUID", required = true, in = ParameterIn.PATH, example = "dqweqw-dascavcsd-vewrewr"),
            @Parameter(name = "userId", description = "액세스 토큰 아이디", in = ParameterIn.HEADER, required = true, example = "dqweqwd-asdcvcv-sdfsd")
    })
    public ResponseEntity<String> locateSpaceByRoom(
            @RequestHeader("id") String userId,
            @RequestBody @Validated SpaceLocateRequest spaceLocateRequest
    ) {
        return new ResponseEntity<>(this.spaceService.locateSpace(UUID.fromString(userId), spaceLocateRequest), HttpStatus.OK);
    }

    @GetMapping("/canWrite/{spaceId}")
    @Operation(description = "스페이스의 권한을 통과할 수 있는지 여부를 조회하는 엔드포인트")
    @Parameters({
            @Parameter(name = "spaceId", description = "스페이스 UUID", required = true, in = ParameterIn.PATH, example = "dqweqw-dascavcsd-vewrewr"),
            @Parameter(name = "userId", description = "액세스 토큰 아이디", in = ParameterIn.HEADER, required = true, example = "dqweqwd-asdcvcv-sdfsd")
    })
    public ResponseEntity<Boolean> canWriteSpace(
            @RequestHeader("id") String userId,
            @PathVariable("spaceId") String spaceId
    ) {
        return new ResponseEntity<>(this.spaceService.canWriteSpace(UUID.fromString(userId), spaceId), HttpStatus.OK);
    }
    @GetMapping("/getCapture/{channelId}")
    @Operation(description = "채널 캡쳐 스페이스 가져오기")
    @Parameters({
            @Parameter(name = "channelId", description = "채널 UUID", required = true, in = ParameterIn.PATH, example = "dqweqw-dascavcsd-vewrewr"),
            @Parameter(name = "userId", description = "액세스 토큰 아이디", in = ParameterIn.HEADER, required = true, example = "dqweqwd-asdcvcv-sdfsd")
    })
    public ResponseEntity<String> getCaptureSpace(
            @RequestHeader("id") String userId,
            @PathVariable("channelId") String channelId
    ) {
        return new ResponseEntity<>(this.spaceService.getCaptureSpace(userId, channelId), HttpStatus.OK);
    }
}
