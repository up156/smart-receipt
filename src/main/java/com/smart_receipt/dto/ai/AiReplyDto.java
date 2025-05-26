package com.smart_receipt.dto.ai;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.util.List;

@Builder
public record AiReplyDto(

        @JsonProperty("choices")
        List<GigachatReplyDto> choices
) {
}
