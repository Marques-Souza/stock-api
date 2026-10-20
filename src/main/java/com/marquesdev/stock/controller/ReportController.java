package com.marquesdev.stock.controller;

import com.marquesdev.stock.exception.ErrorMessage;
import com.marquesdev.stock.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
@Tag(name = "Reports", description = "Contains operations for generating reports.")
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/stock-movements/pdf")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'ATTENDANT')")
    @Operation(summary = "Generate a stock movement report",
            description = "Generates a PDF report of stock movements using optional filters. " +
                    "If no movements match the filters, the PDF displays a message. " +
                    "Accessible by admins and attendants.",
            security = @SecurityRequirement(name = "security"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Stock movement report generated successfully",
                            content = @Content(mediaType = MediaType.APPLICATION_PDF_VALUE,
                                    schema = @Schema(type = "string", format = "binary"))),
                    @ApiResponse(responseCode = "400", description = "Invalid date format",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "401", description = "Authentication required",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "403", description = "User not authorized",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ErrorMessage.class)))
            })
    public void generateStockMovementReport(
            HttpServletResponse response,
            @Parameter(description = "Optional movement type filter: ENTRY or EXIT", example = "ENTRY")
            @RequestParam(required = false) String type,
            @Parameter(description = "Optional start date in ISO date-time format", example = "2026-10-20T00:00:00")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "Optional end date in ISO date-time format", example = "2026-10-20T23:59:59")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @Parameter(description = "Optional product code filter", example = "CP-V-1")
            @RequestParam(required = false) String productCode
    ) throws IOException {
        byte[] bytes = reportService.generateStockMovementReport(type, startDate, endDate, productCode);

        response.setContentType(MediaType.APPLICATION_PDF_VALUE);
        response.setHeader("Content-disposition", "inline; filename=" + System.currentTimeMillis() + ".pdf");
        response.getOutputStream().write(bytes);
    }
}
