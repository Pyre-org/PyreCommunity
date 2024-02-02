package com.pyre.community.controller;

import com.pyre.community.service.RoomService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequiredArgsConstructor
@Slf4j
@RequestMapping(value = "/community/room")
@Tag(name="Room", description = "Room API 구성")
public class RoomController {
    private final RoomService roomService;

}
