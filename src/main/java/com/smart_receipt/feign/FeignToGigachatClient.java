package com.smart_receipt.feign;

import com.smart_receipt.dto.ai.AiReplyDto;
import com.smart_receipt.request.GigachatRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@Profile("!demo")
@FeignClient(name = "${feign.ai.name}", url = "${feign.ai.url}")
public interface FeignToGigachatClient {

    @PostMapping(value = "/chat/completions", consumes = "application/json", produces = "application/json")
    AiReplyDto getAiReply(@RequestBody GigachatRequest request, @RequestHeader String authorization);
}

