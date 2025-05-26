package com.smart_receipt.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@RequestMapping("api/v1/admin/statistics")
@Tag(name = "AdminStatisticsController", description = "Контроллер для работы администратора со статистикой пользователей")
public interface AdminStatisticsController {

    @GetMapping("/recalculate-all")
    @Operation(summary = "Пересчет всей статистики по всем пользователям")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Статистика пересчитана",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "401",
                    description = "Пользователь не аутентифицирован",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "403",
                    description = "Недостаточно прав доступа (необходима роль ADMIN)",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
    })
    public ResponseEntity<Void> recalculateAll();
}