package com.smart_receipt.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "gigachat")
public class GigachatProperties {
    private String model;
    private Double temperature;
    private Long n;
    private Long maxTokens;
    private Double repetitionPenalty;
}
