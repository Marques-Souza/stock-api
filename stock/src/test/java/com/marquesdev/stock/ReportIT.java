package com.marquesdev.stock;

import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.parser.PdfTextExtractor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = "/sql/reports/stock-movement-report-insert.sql",
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/sql/reports/stock-movement-report-delete.sql",
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@AutoConfigureWebTestClient
public class ReportIT {

    private static final String REPORT_URI = "/api/v1/reports/stock-movements/pdf";
    private static final String ADMIN_USER = "report-admin@gmail.com";
    private static final String ATTENDANT_USER = "report-attendant@gmail.com";
    private static final String USER_PASS = "123456";

    @Autowired
    private WebTestClient testClient;

    @Test
    public void generateStockMovementReport_WithMovements_ReturnsPdfWithReportData() throws IOException {
        byte[] pdf = testClient
                .get()
                .uri(REPORT_URI)
                .headers(JwtAuthentication.getHeadersAuthorization(testClient, ADMIN_USER, USER_PASS))
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_PDF)
                .expectBody(byte[].class)
                .returnResult().getResponseBody();

        assertThat(pdf).isNotNull();
        assertThat(pdf).startsWith("%PDF".getBytes());

        String reportText = extractText(pdf);
        assertThat(reportText).contains("Relatório de Movimentações de Estoque");
        assertThat(reportText).contains("Produto relatório");
        assertThat(reportText).contains("Entrada");
        assertThat(reportText).contains("Saída");
        assertThat(reportText).contains("Fornecedor relatório");
        assertThat(reportText).contains("Cliente relatório");
    }

    @Test
    public void generateStockMovementReport_WithFilters_ReturnsOnlyMatchingMovement() throws IOException {
        byte[] pdf = testClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(REPORT_URI)
                        .queryParam("type", "entry")
                        .queryParam("startDate", "2026-01-01T00:00:00")
                        .queryParam("endDate", "2026-01-01T23:59:59")
                        .queryParam("productCode", "REPORT-001")
                        .build())
                .headers(JwtAuthentication.getHeadersAuthorization(testClient, ATTENDANT_USER, USER_PASS))
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_PDF)
                .expectBody(byte[].class)
                .returnResult().getResponseBody();

        assertThat(pdf).isNotNull();
        String reportText = extractText(pdf);
        assertThat(reportText).contains("Entrada");
        assertThat(reportText).doesNotContain("Saída");
        assertThat(reportText).contains("01/01/2026");
    }

    @Test
    public void generateStockMovementReport_WithNoMatchingMovements_ReturnsPdfWithNoDataMessage() throws IOException {
        byte[] pdf = testClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(REPORT_URI)
                        .queryParam("productCode", "DOES-NOT-EXIST")
                        .build())
                .headers(JwtAuthentication.getHeadersAuthorization(testClient, ADMIN_USER, USER_PASS))
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_PDF)
                .expectBody(byte[].class)
                .returnResult().getResponseBody();

        assertThat(pdf).isNotNull();
        String reportText = extractText(pdf);
        assertThat(reportText).contains("Nenhuma movimentação encontrada");
        assertThat(reportText).contains("Nenhum registro corresponde aos filtros informados");
        assertThat(reportText).doesNotContain("Produto relatório");
    }

    @Test
    public void generateStockMovementReport_WithoutAuthentication_ReturnsStatus401() {
        testClient
                .get()
                .uri(REPORT_URI)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    private String extractText(byte[] pdf) throws IOException {
        PdfReader reader = new PdfReader(pdf);
        try {
            return new PdfTextExtractor(reader).getTextFromPage(1);
        } finally {
            reader.close();
        }
    }
}
