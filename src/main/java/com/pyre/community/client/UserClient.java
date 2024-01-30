package com.pyre.community.client;

import com.pyre.community.dto.response.UserInfoFeignResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "EndUserFeign", url = "https://localhost:8000/auth-service")
public interface UserClient {
    String AUTHORIZATION = "AUTHORIZATION";
    @GetMapping("/auth-service/info")
    UserInfoFeignResponse getUserInfo(@RequestHeader(AUTHORIZATION) String token);


}
