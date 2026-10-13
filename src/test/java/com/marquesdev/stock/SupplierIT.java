package com.marquesdev.stock;

import com.marquesdev.stock.dto.SupplierRequestDto;
import com.marquesdev.stock.dto.SupplierResponseDto;
import com.marquesdev.stock.dto.SupplierUpdateDto;
import com.marquesdev.stock.exception.ErrorMessage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.UUID;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = "/sql/suppliers/suppliers-insert.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/sql/suppliers/suppliers-delete.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@AutoConfigureWebTestClient
public class SupplierIT {

    @Autowired
    private WebTestClient testClient;

    private static final String SUPPLIERS_URI = "/api/v1/suppliers";

    // Database IDs constants (SQL Script)
    private final UUID SUPPLIER_ID_1 =
            UUID.fromString("4a5b6c7d-8e9f-0a1b-2c3d-4e5f6a7b8c9d");
    private final UUID SUPPLIER_ID_2 =
            UUID.fromString("5b6c7d8e-9f0a-1b2c-3d4e-5f6a7b8c9d0e");
    private final UUID INEXISTENT_ID =
            UUID.fromString("a1b2c3d4-e5f6-7890-abcd-ef1234567890");

    private final String ADMIN_USER = "support@gmail.com";
    private final String ADMIN_PASS = "123456";
    private final String ATTENDANT_USER = "karla@gmail.com";
    private final String ATTENDANT_PASS = "123456";

    // ===========================================
    // TEST BLOCK: POST /api/v1/suppliers (Create Supplier)
    // =============================================

    @Test
    public void createSupplier_WithValidData_ReturnsCreatedSupplierWithStatus201(){
 SupplierResponseDto responseBody = testClient
                .post()
                .uri(SUPPLIERS_URI)
                .headers(JwtAuthentication.getHeadersAuthorization(testClient, ADMIN_USER, ADMIN_PASS))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new SupplierRequestDto("Novo Fornecedor Ltda", "47115782000178", "novo@fornecedor.com", "11999998888"))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(SupplierResponseDto.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.id()).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.name()).isEqualTo("Novo Fornecedor Ltda");
        org.assertj.core.api.Assertions.assertThat(responseBody.cnpj()).isEqualTo("47115782000178");
        org.assertj.core.api.Assertions.assertThat(responseBody.email()).isEqualTo("novo@fornecedor.com");
        org.assertj.core.api.Assertions.assertThat(responseBody.phone()).isEqualTo("11999998888");
    }

    @Test
    public void createSupplier_WithDuplicateEmail_ReturnsStatus409(){
        ErrorMessage responseBody = testClient
                .post()
                .uri(SUPPLIERS_URI)
                .headers(JwtAuthentication.getHeadersAuthorization(testClient, ADMIN_USER, ADMIN_PASS))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new SupplierRequestDto("Fornecedor Diferente", "95409111000162", "contato@distribuidoranacional.com", "11999998888"))
                .exchange()
                .expectStatus().isEqualTo(409)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(409);
    }

    @Test
    public void createSupplier_WithDuplicateCnpj_ReturnsStatus409(){
        ErrorMessage responseBody = testClient
                .post()
                .uri(SUPPLIERS_URI)
                .headers(JwtAuthentication.getHeadersAuthorization(testClient, ADMIN_USER, ADMIN_PASS))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new SupplierRequestDto("Fornecedor Diferente", "05424453000169", "diferente@fornecedor.com", "11999998888"))
                .exchange()
                .expectStatus().isEqualTo(409)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(409);
    }

    @Test
    public void createSupplier_WithInvalidData_ReturnsStatus422(){
        ErrorMessage responseBody = testClient
                .post()
                .uri(SUPPLIERS_URI)
                .headers(JwtAuthentication.getHeadersAuthorization(testClient, ADMIN_USER, ADMIN_PASS))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new SupplierRequestDto("", "", "", ""))
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(422);
    }

    @Test
    public void createSupplier_WithoutAuthentication_ReturnsStatus401(){
        testClient
                .post()
                .uri(SUPPLIERS_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new SupplierRequestDto("Novo Fornecedor Ltda", "12345678000190", "novo@fornecedor.com", "11999998888"))
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    public void createSupplier_WithAttendantUser_ReturnsStatus403(){
        testClient
                .post()
                .uri(SUPPLIERS_URI)
                .headers(JwtAuthentication.getHeadersAuthorization(testClient, ATTENDANT_USER, ATTENDANT_PASS))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new SupplierRequestDto("Novo Fornecedor Ltda", "95409111000162", "novo@fornecedor.com", "11999998888"))
                .exchange()
                .expectStatus().isForbidden();
    }

    // ===========================================
    // TEST BLOCK: GET /api/v1/suppliers/{id} (Get Supplier by ID)
    // =============================================

    @Test
    public void getSupplierById_WithValidId_ReturnsSupplierWithStatus200(){
        SupplierResponseDto responseBody = testClient
                .get()
                .uri(SUPPLIERS_URI + "/" + SUPPLIER_ID_1)
                .headers(JwtAuthentication.getHeadersAuthorization(testClient, ADMIN_USER, ADMIN_PASS))
                .exchange()
                .expectStatus().isOk()
                .expectBody(SupplierResponseDto.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.id()).isEqualTo(SUPPLIER_ID_1);
        org.assertj.core.api.Assertions.assertThat(responseBody.name()).isEqualTo("Distribuidora Nacional de Produtos Ltda");
        org.assertj.core.api.Assertions.assertThat(responseBody.email()).isEqualTo("contato@distribuidoranacional.com");
    }

    @Test
    public void getSupplierById_WithInexistentId_ReturnsStatus404(){
        ErrorMessage responseBody = testClient
                .get()
                .uri(SUPPLIERS_URI + "/" + INEXISTENT_ID)
                .headers(JwtAuthentication.getHeadersAuthorization(testClient, ADMIN_USER, ADMIN_PASS))
                .exchange()
                .expectStatus().isEqualTo(404)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(404);
    }

    @Test
    public void getSupplierById_WithoutAuthentication_ReturnsStatus401(){
        testClient
                .get()
                .uri(SUPPLIERS_URI + "/" + SUPPLIER_ID_1)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    // ===========================================
    // TEST BLOCK: GET /api/v1/suppliers (Get All Suppliers with Pagination)
    // =============================================

    @Test
    public void getAllSuppliers_WithoutAuthentication_ReturnsStatus401(){
        testClient
                .get()
                .uri(SUPPLIERS_URI)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    // ===========================================
    // TEST BLOCK: PUT /api/v1/suppliers/{id} (Update Supplier)
    // =============================================

    @Test
    public void updateSupplier_WithValidData_ReturnsUpdatedSupplierWithStatus200(){
        SupplierResponseDto responseBody = testClient
                .put()
                .uri(SUPPLIERS_URI + "/" + SUPPLIER_ID_1)
                .headers(JwtAuthentication.getHeadersAuthorization(testClient, ADMIN_USER, ADMIN_PASS))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new SupplierUpdateDto("Distribuidora Nacional Atualizada", "novocontato@distribuidoranacional.com", "11955554444"))
                .exchange()
                .expectStatus().isOk()
                .expectBody(SupplierResponseDto.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.id()).isEqualTo(SUPPLIER_ID_1);
        org.assertj.core.api.Assertions.assertThat(responseBody.name()).isEqualTo("Distribuidora Nacional Atualizada");
        org.assertj.core.api.Assertions.assertThat(responseBody.email()).isEqualTo("novocontato@distribuidoranacional.com");
        org.assertj.core.api.Assertions.assertThat(responseBody.phone()).isEqualTo("11955554444");
    }

    @Test
    public void updateSupplier_WithInexistentId_ReturnsStatus404(){
        ErrorMessage responseBody = testClient
                .put()
                .uri(SUPPLIERS_URI + "/" + INEXISTENT_ID)
                .headers(JwtAuthentication.getHeadersAuthorization(testClient, ADMIN_USER, ADMIN_PASS))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new SupplierUpdateDto("Nome Atualizado", "email@atualizado.com", "11955554444"))
                .exchange()
                .expectStatus().isEqualTo(404)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(404);
    }

    @Test
    public void updateSupplier_WithInvalidData_ReturnsStatus422(){
        ErrorMessage responseBody = testClient
                .put()
                .uri(SUPPLIERS_URI + "/" + SUPPLIER_ID_1)
                .headers(JwtAuthentication.getHeadersAuthorization(testClient, ADMIN_USER, ADMIN_PASS))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new SupplierUpdateDto("", "invalid-email", ""))
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(422);
    }

    @Test
    public void updateSupplier_WithoutAuthentication_ReturnsStatus401(){
        testClient
                .put()
                .uri(SUPPLIERS_URI + "/" + SUPPLIER_ID_1)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new SupplierUpdateDto("Nome Atualizado", "email@atualizado.com", "11955554444"))
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    public void updateSupplier_WithAttendantUser_ReturnsStatus403(){
        testClient
                .put()
                .uri(SUPPLIERS_URI + "/" + SUPPLIER_ID_1)
                .headers(JwtAuthentication.getHeadersAuthorization(testClient, ATTENDANT_USER, ATTENDANT_PASS))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new SupplierUpdateDto("Nome Atualizado", "email@atualizado.com", "11955554444"))
                .exchange()
                .expectStatus().isForbidden();
    }

}
