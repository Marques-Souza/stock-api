package com.marquesdev.stock;

import com.marquesdev.stock.dto.ProductResponseDto;
import com.marquesdev.stock.dto.StockMovementRequestDto;
import com.marquesdev.stock.dto.StockMovementResponseDto;
import com.marquesdev.stock.enums.Type;
import com.marquesdev.stock.exception.ErrorMessage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = "/sql/stock-movements/stock-movements-insert.sql",
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/sql/stock-movements/stock-movements-delete.sql",
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@AutoConfigureWebTestClient
public class StockMovementIT {

    private static final String STOCK_MOVEMENTS_URI = "/api/v1/stock-movements";
    private static final String PRODUCTS_URI = "/api/v1/products";

    private static final UUID PRODUCT_ID =
            UUID.fromString("4c5d6e7f-8091-4abc-def0-123456789abc");
    private static final UUID SUPPLIER_ID =
            UUID.fromString("7a5b6c7d-8e9f-4012-abcd-ef0123456789");
    private static final UUID CLIENT_ID =
            UUID.fromString("8a5b6c7d-8e9f-4012-abcd-ef0123456789");
    private static final UUID INEXISTENT_ID =
            UUID.fromString("a1b2c3d4-e5f6-7890-abcd-ef1234567890");

    private static final String ADMIN_USER = "stock-admin@gmail.com";
    private static final String ADMIN_PASS = "123456";
    private static final String ATTENDANT_USER = "stock-attendant@gmail.com";
    private static final String ATTENDANT_PASS = "123456";

    @Autowired
    private WebTestClient testClient;

    @Test
    public void createEntry_WithValidData_IncreasesProductStockAndReturnsCreatedMovement() {
        StockMovementResponseDto responseBody = testClient
                .post()
                .uri(STOCK_MOVEMENTS_URI)
                .headers(JwtAuthentication.getHeadersAuthorization(testClient, ADMIN_USER, ADMIN_PASS))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new StockMovementRequestDto(PRODUCT_ID, Type.ENTRY, 3, "Reposicao", SUPPLIER_ID, null))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(StockMovementResponseDto.class)
                .returnResult().getResponseBody();

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.id()).isNotNull();
        assertThat(responseBody.movementCode()).isNotBlank();
        assertThat(responseBody.productId()).isEqualTo(PRODUCT_ID);
        assertThat(responseBody.productName()).isEqualTo("Teclado de teste");
        assertThat(responseBody.type()).isEqualTo(Type.ENTRY);
        assertThat(responseBody.quantity()).isEqualTo(3);
        assertThat(responseBody.unitPrice()).isEqualByComparingTo("89.90");
        assertThat(responseBody.totalValue()).isEqualByComparingTo("269.70");
        assertThat(responseBody.supplierId()).isEqualTo(SUPPLIER_ID);
        assertThat(responseBody.supplierName()).isEqualTo("Fornecedor de teste");
        assertThat(responseBody.clientId()).isNull();
        assertThat(responseBody.createdBy()).isEqualTo(ADMIN_USER);
        assertProductQuantity(7);
    }

    @Test
    public void createExit_WithValidData_DecreasesProductStockAndReturnsCreatedMovement() {
        StockMovementResponseDto responseBody = testClient
                .post()
                .uri(STOCK_MOVEMENTS_URI)
                .headers(JwtAuthentication.getHeadersAuthorization(testClient, ATTENDANT_USER, ATTENDANT_PASS))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new StockMovementRequestDto(PRODUCT_ID, Type.EXIT, 2, "Venda", null, CLIENT_ID))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(StockMovementResponseDto.class)
                .returnResult().getResponseBody();

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.type()).isEqualTo(Type.EXIT);
        assertThat(responseBody.quantity()).isEqualTo(2);
        assertThat(responseBody.unitPrice()).isEqualByComparingTo("89.90");
        assertThat(responseBody.totalValue()).isEqualByComparingTo("179.80");
        assertThat(responseBody.supplierId()).isNull();
        assertThat(responseBody.clientId()).isEqualTo(CLIENT_ID);
        assertThat(responseBody.clientName()).isEqualTo("Cliente de teste");
        assertThat(responseBody.createdBy()).isEqualTo(ATTENDANT_USER);
        assertProductQuantity(2);
    }

    @Test
    public void createMovement_WithMissingRequiredFields_ReturnsStatus422() {
        ErrorMessage responseBody = testClient
                .post()
                .uri(STOCK_MOVEMENTS_URI)
                .headers(JwtAuthentication.getHeadersAuthorization(testClient, ADMIN_USER, ADMIN_PASS))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new StockMovementRequestDto(null, null, 0, null, null, null))
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getStatus()).isEqualTo(422);
    }

    @Test
    public void createEntry_WithoutSupplier_ReturnsStatus422() {
        assertInvalidMovement(new StockMovementRequestDto(PRODUCT_ID, Type.ENTRY, 1, null, null, null));
    }

    @Test
    public void createExit_WithoutClient_ReturnsStatus422() {
        assertInvalidMovement(new StockMovementRequestDto(PRODUCT_ID, Type.EXIT, 1, null, null, null));
    }

    @Test
    public void createEntry_WithClientInsteadOfSupplier_ReturnsStatus422() {
        assertInvalidMovement(new StockMovementRequestDto(PRODUCT_ID, Type.ENTRY, 1, null, null, CLIENT_ID));
    }

    @Test
    public void createMovement_WithUnknownProduct_ReturnsStatus404() {
        ErrorMessage responseBody = testClient
                .post()
                .uri(STOCK_MOVEMENTS_URI)
                .headers(JwtAuthentication.getHeadersAuthorization(testClient, ADMIN_USER, ADMIN_PASS))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new StockMovementRequestDto(INEXISTENT_ID, Type.ENTRY, 1, null, SUPPLIER_ID, null))
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getStatus()).isEqualTo(404);
    }

    @Test
    public void createExit_WithInsufficientStock_ReturnsStatus409AndPreservesStock() {
        ErrorMessage responseBody = testClient
                .post()
                .uri(STOCK_MOVEMENTS_URI)
                .headers(JwtAuthentication.getHeadersAuthorization(testClient, ADMIN_USER, ADMIN_PASS))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new StockMovementRequestDto(PRODUCT_ID, Type.EXIT, 5, null, null, CLIENT_ID))
                .exchange()
                .expectStatus().isEqualTo(409)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getStatus()).isEqualTo(409);
        assertProductQuantity(4);
    }

    @Test
    public void createMovement_WithoutAuthentication_ReturnsStatus401() {
        testClient
                .post()
                .uri(STOCK_MOVEMENTS_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new StockMovementRequestDto(PRODUCT_ID, Type.ENTRY, 1, null, SUPPLIER_ID, null))
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    public void getStockMovements_WithTypeFilter_ReturnsOnlyMatchingMovements() {
        testClient
                .get()
                .uri(STOCK_MOVEMENTS_URI + "?page=0&size=10")
                .header("type", "ENTRY")
                .headers(JwtAuthentication.getHeadersAuthorization(testClient, ATTENDANT_USER, ATTENDANT_PASS))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.totalElements").isEqualTo(1)
                .jsonPath("$.content[0].type").isEqualTo("ENTRY")
                .jsonPath("$.content[0].movementCode").isEqualTo("202601-000101");
    }

    @Test
    public void getStockMovements_WithPagination_ReturnsPageOfMovements() {
        testClient
                .get()
                .uri(STOCK_MOVEMENTS_URI + "?page=0&size=1")
                .headers(JwtAuthentication.getHeadersAuthorization(testClient, ADMIN_USER, ADMIN_PASS))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.totalElements").isEqualTo(2)
                .jsonPath("$.totalPages").isEqualTo(2)
                .jsonPath("$.content.length()").isEqualTo(1);
    }

    @Test
    public void getStockMovements_WithoutAuthentication_ReturnsStatus401() {
        testClient
                .get()
                .uri(STOCK_MOVEMENTS_URI)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    public void getStockMovementByCode_WithExistingCode_ReturnsMovement() {
        StockMovementResponseDto responseBody = testClient
                .get()
                .uri(STOCK_MOVEMENTS_URI + "/code/202601-000101")
                .headers(JwtAuthentication.getHeadersAuthorization(testClient, ADMIN_USER, ADMIN_PASS))
                .exchange()
                .expectStatus().isOk()
                .expectBody(StockMovementResponseDto.class)
                .returnResult().getResponseBody();

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.movementCode()).isEqualTo("202601-000101");
        assertThat(responseBody.type()).isEqualTo(Type.ENTRY);
        assertThat(responseBody.productId()).isEqualTo(PRODUCT_ID);
        assertThat(responseBody.supplierId()).isEqualTo(SUPPLIER_ID);
    }

    @Test
    public void getStockMovementByCode_WithUnknownCode_ReturnsStatus404() {
        ErrorMessage responseBody = testClient
                .get()
                .uri(STOCK_MOVEMENTS_URI + "/code/does-not-exist")
                .headers(JwtAuthentication.getHeadersAuthorization(testClient, ADMIN_USER, ADMIN_PASS))
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getStatus()).isEqualTo(404);
    }

    @Test
    public void getStockMovementByCode_WithoutAuthentication_ReturnsStatus401() {
        testClient
                .get()
                .uri(STOCK_MOVEMENTS_URI + "/code/202601-000101")
                .exchange()
                .expectStatus().isUnauthorized();
    }

    private void assertInvalidMovement(StockMovementRequestDto request) {
        ErrorMessage responseBody = testClient
                .post()
                .uri(STOCK_MOVEMENTS_URI)
                .headers(JwtAuthentication.getHeadersAuthorization(testClient, ADMIN_USER, ADMIN_PASS))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getStatus()).isEqualTo(422);
    }

    private void assertProductQuantity(int expectedQuantity) {
        ProductResponseDto product = testClient
                .get()
                .uri(PRODUCTS_URI + "/" + PRODUCT_ID)
                .headers(JwtAuthentication.getHeadersAuthorization(testClient, ADMIN_USER, ADMIN_PASS))
                .exchange()
                .expectStatus().isOk()
                .expectBody(ProductResponseDto.class)
                .returnResult().getResponseBody();

        assertThat(product).isNotNull();
        assertThat(product.quantity()).isEqualTo(expectedQuantity);
    }
}
