package com.smart_receipt.feign;

import com.smart_receipt.dto.ai.AccessTokenDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.Map;

@Profile("!demo")
@FeignClient(name = "${feign.ai-auth.name}", url = "${feign.ai-auth.url}")
public interface FeignAuthClient {

    @PostMapping(value = "", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    AccessTokenDto getAccessToken(@RequestHeader String Authorization,
                                  @RequestHeader String RqUID,
                                  @RequestBody Map<String, String> scope);
}

