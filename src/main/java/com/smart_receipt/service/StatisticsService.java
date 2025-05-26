package com.smart_receipt.service;

import io.swagger.v3.oas.annotations.tags.Tag;


@Tag(name = "StatisticsService", description = "Сервис для работы со статистикой по чекам")
public interface StatisticsService {

    void updateStatistics(Long userId);

    void recalculateAll();
}