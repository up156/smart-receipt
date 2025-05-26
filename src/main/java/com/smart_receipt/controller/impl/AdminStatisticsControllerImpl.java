package com.smart_receipt.controller.impl;

import com.smart_receipt.controller.AdminStatisticsController;
import com.smart_receipt.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminStatisticsControllerImpl implements AdminStatisticsController {

    private final StatisticsService statisticsService;

    public ResponseEntity<Void> recalculateAll() {
        statisticsService.recalculateAll();
        return ResponseEntity.ok().build();
    }
}
