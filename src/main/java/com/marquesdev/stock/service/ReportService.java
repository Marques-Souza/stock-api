package com.marquesdev.stock.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.*;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportService {

    private final ResourceLoader resourceLoader;
    private final DataSource dataSource;

    private static final String JASPER_DIRETORIO = "classpath:/reports/";

    public byte[] generateStockMovementReport(String type, LocalDateTime startDate, LocalDateTime endDate, String productCode) {
        byte[] bytes = null;

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("REPORT_TITLE", "Relat\u00f3rio de Movimenta\u00e7\u00f5es de Estoque");
        parameters.put("GENERATED_AT", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
        parameters.put("REPORT_LOCALE", Locale.forLanguageTag("pt-BR"));
        parameters.put("TYPE", type);
        parameters.put("START_DATE", startDate != null ? java.sql.Timestamp.valueOf(startDate) : null);
        parameters.put("END_DATE", endDate != null ? java.sql.Timestamp.valueOf(endDate) : null);
        parameters.put("PRODUCT_CODE", productCode);

        Resource resource = resourceLoader.getResource(JASPER_DIRETORIO.concat("relatorio_movimentacoes.jrxml"));
        try (InputStream stream = resource.getInputStream();
            Connection connection = dataSource.getConnection()) {
            JasperReport jasperReport = JasperCompileManager.compileReport(stream);
            JasperPrint print = JasperFillManager.fillReport(jasperReport, parameters, connection);
            bytes = JasperExportManager.exportReportToPdf(print);
        } catch (IOException | JRException | SQLException e) {
            log.error("Erro ao gerar relatório", e);
            throw new RuntimeException(e);
        }
        return bytes;
    }
}
