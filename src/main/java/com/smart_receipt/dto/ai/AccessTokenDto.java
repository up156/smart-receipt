package com.smart_receipt.dto.ai;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.time.Instant;

@Builder
public record AccessTokenDto(

        String accessToken,
        Instant expiresAt
) {
    @JsonCreator
    public AccessTokenDto(@JsonProperty("access_token") String accessToken, @JsonProperty("expires_at") Long expiresAtMillis) {
        this(accessToken, Instant.ofEpochMilli(expiresAtMillis));
    }

    public boolean isExpired() {
        return expiresAt == null || Instant.now().isAfter(expiresAt.minusSeconds(60));
    }
}
