package com.pyre.community.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(value = "feed", path = "/feed")
public interface FeedClient {

}
