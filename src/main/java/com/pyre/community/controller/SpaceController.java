package com.pyre.community.controller;

import com.pyre.community.dto.request.SpaceCreateRequest;
import com.pyre.community.dto.response.SpaceCreateResponse;
import com.pyre.community.service.SpaceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
            @Parameter(),
    })
    public ResponseEntity<SpaceCreateResponse> createSpace(SpaceCreateRequest spaceCreateRequest, @RequestHeader("id") String userId) {
        return new ResponseEntity<>(this.spaceService.createSpace(spaceCreateRequest, UUID.fromString(userId)), HttpStatus.OK);
    }
}
