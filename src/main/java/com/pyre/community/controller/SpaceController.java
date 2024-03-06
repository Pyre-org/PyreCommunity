package com.pyre.community.controller;

import com.pyre.community.dto.request.SpaceCreateRequest;
import com.pyre.community.dto.response.SpaceCreateResponse;
import com.pyre.community.dto.response.SpaceGetListByRoomResponse;
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
    public ResponseEntity<SpaceCreateResponse> createSpace(SpaceCreateRequest spaceCreateRequest, @RequestHeader("id") String userId) {
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
}
