package com.smart_receipt.kafka;

import com.smart_receipt.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class ReceiptEventListener {

    private final StatisticsService statisticsService;

    @KafkaListener(topics = "receipt.processed", groupId = "smart-receipt-group")
    public void handleReceiptProcessed(String userIdStr) {
        Long userId = Long.valueOf(userIdStr);
        log.info("Kafka listener triggered for [receipt.processed], userId: {}", userId);
        statisticsService.updateStatistics(userId);
    }
}
