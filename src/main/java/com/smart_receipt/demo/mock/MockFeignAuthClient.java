package com.smart_receipt.demo.mock;

import com.smart_receipt.dto.ai.AccessTokenDto;
import com.smart_receipt.feign.FeignAuthClient;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;

@Component
@Profile("demo")
public class MockFeignAuthClient implements FeignAuthClient {

    @Override
    public AccessTokenDto getAccessToken(String authorization, String rqUid, Map<String, String> scope) {
        return AccessTokenDto
                .builder()
                .accessToken("token")
                .expiresAt(Instant.now().plus(30, ChronoUnit.MINUTES))
                .build();
    }
}

