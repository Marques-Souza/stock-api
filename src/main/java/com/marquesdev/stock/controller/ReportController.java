package com.marquesdev.stock.controller;

import com.marquesdev.stock.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.time.LocalDateTime;

@RestController
@RequestMapping("${app.api.endpoints.reports}")
@RequiredArgsConstructor
@Tag(name = "Relatórios", description = "Endpoints para geração de relatórios")
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/stock-movements/pdf")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'ATTENDANT')")
    @Operation(summary = "Gerar relatório de movimentações de estoque em PDF")
    public void generateStockMovementReport(
            HttpServletResponse response,
            @Parameter(description = "Tipo de movimentação (ENTRY ou EXIT)")
            @RequestParam(required = false) String type,
            @Parameter(description = "Data inicial (formato: yyyy-MM-dd'T'HH:mm:ss)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "Data final (formato: yyyy-MM-dd'T'HH:mm:ss)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @Parameter(description = "Código do produto para filtro")
            @RequestParam(required = false) String productCode
    ) throws IOException {
        byte[] bytes = reportService.generateStockMovementReport(type, startDate, endDate, productCode);

        response.setContentType(MediaType.APPLICATION_PDF_VALUE);
        response.setHeader("Content-disposition", "inline; filename=" + System.currentTimeMillis() + ".pdf");
        response.getOutputStream().write(bytes);
    }
}
