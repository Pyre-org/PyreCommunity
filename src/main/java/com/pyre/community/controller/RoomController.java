package com.pyre.community.controller;

import com.pyre.community.dto.request.RoomCreateRequest;
import com.pyre.community.dto.response.RoomCreateResponse;
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
}
