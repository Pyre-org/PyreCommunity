package com.pyre.community.controller;

import com.pyre.community.dto.response.search.RoomSearchListResponse;
import com.pyre.community.dto.response.search.SpaceSearchListResponse;
import com.pyre.community.service.SearchService;
import feign.Response;
import io.swagger.v3.oas.annotations.Operation;
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
@RequestMapping(value = "/community/search")
@Validated
@Tag(name="Search", description = "Search API 구성")
@Slf4j
public class SearchController {
    private final SearchService searchService;
    @GetMapping("/room")
    @Operation(description = "룸 이름으로 검색")
    public ResponseEntity<RoomSearchListResponse> searchRoom(
            @RequestHeader("id") String userId,
            @RequestParam(value = "keyword", defaultValue = "") String keyword,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size
    ) {
        return new ResponseEntity<>(searchService.searchRooms(UUID.fromString(userId), keyword, page, size), HttpStatus.OK);
    }
    @GetMapping("/space")
    @Operation(description = "스페이스 이름으로 검색")
    public ResponseEntity<SpaceSearchListResponse> searchSpace (
            @RequestHeader("id") String userId,
            @RequestParam(value = "keyword", defaultValue = "") String keyword,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size
    ) {
        return new ResponseEntity<>(searchService.searchSpaces(UUID.fromString(userId), keyword, page, size), HttpStatus.OK);
    }
    @GetMapping("/feed")
    @Operation(description = "피드 제목으로 검색")
    public void searchFeed (
            @RequestHeader("id") String userId,
            @RequestParam(value = "keyword", defaultValue = "") String keyword,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size
    ) {
    }
    @GetMapping("/user")
    @Operation(description = "유저 이름으로 검색")
    public void searchUser (
            @RequestHeader("id") String userId,
            @RequestParam(value = "keyword", defaultValue = "") String keyword,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size
    ) {
    }
}
