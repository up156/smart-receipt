package com.smart_receipt.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class ReceiptEventPublisher {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public void publishReceiptProcessed(Long userId) {
        log.info("Published Kafka event to [receipt.processed] for userId: {}", userId);
        kafkaTemplate.send("receipt.processed", userId.toString());
    }
}
