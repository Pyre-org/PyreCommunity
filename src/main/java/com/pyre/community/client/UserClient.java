package com.pyre.community.client;

import com.pyre.community.dto.response.NicknameAndProfileImgResponse;
import com.pyre.community.dto.response.UserInfoFeignResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(value = "auth-service", path = "/auth-service/user")
public interface UserClient {
    String AUTHORIZATION = "Authorization";
    @GetMapping("/info")
    UserInfoFeignResponse getUserInfo(@RequestHeader(AUTHORIZATION) String token);
    @GetMapping("/get/nickname/{userId}")
    ResponseEntity<NicknameAndProfileImgResponse> getNicknameAndProfileImage(@PathVariable String userId);
}
