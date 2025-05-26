package com.smart_receipt.dto.ai;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record GigachatReplyDto(

        @JsonProperty("message")
        GigachatReplyMessageDto message
) {
}
