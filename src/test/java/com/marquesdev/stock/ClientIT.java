package com.marquesdev.stock;

import com.marquesdev.stock.dto.ClientRequestDto;
import com.marquesdev.stock.dto.ClientResponseDto;
import com.marquesdev.stock.exception.ErrorMessage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = "/sql/clients/clients-insert.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/sql/clients/clients-delete.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@AutoConfigureWebTestClient
public class ClientIT {

    @Autowired
    private WebTestClient testClient;

    private static final String CLIENTS_URI = "/api/v1/clients";

    private final String ADMIN_USER = "support@gmail.com";
    private final String ADMIN_PASS = "123456";
    private final String ATTENDANT_USER = "karla@gmail.com";
    private final String ATTENDANT_PASS = "123456";

    // ===========================================
    // TEST BLOCK: POST /api/v1/clients (Create Client)
    // =============================================

    @Test
    public void createClient_WithValidData_ReturnsCreatedClientWithStatus201(){
        ClientResponseDto responseBody = testClient
                .post()
                .uri(CLIENTS_URI)
                .headers(JwtAuthentication.getHeadersAuthorization(testClient, ADMIN_USER, ADMIN_PASS))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new ClientRequestDto("Nova Empresa Ltda", "12345678000190", "nova@empresa.com", "11999998888"))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(ClientResponseDto.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.id()).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.name()).isEqualTo("Nova Empresa Ltda");
        org.assertj.core.api.Assertions.assertThat(responseBody.cnpj()).isEqualTo("12345678000190");
        org.assertj.core.api.Assertions.assertThat(responseBody.email()).isEqualTo("nova@empresa.com");
        org.assertj.core.api.Assertions.assertThat(responseBody.phone()).isEqualTo("11999998888");
    }

    @Test
    public void createClient_WithDuplicateEmail_ReturnsStatus409(){
        ErrorMessage responseBody = testClient
                .post()
                .uri(CLIENTS_URI)
                .headers(JwtAuthentication.getHeadersAuthorization(testClient, ADMIN_USER, ADMIN_PASS))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new ClientRequestDto("Empresa Diferente", "12345678000190", "contato@inovatech.com", "11999998888"))
                .exchange()
                .expectStatus().isEqualTo(409)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(409);
    }

    @Test
    public void createClient_WithDuplicateCnpj_ReturnsStatus409(){
        ErrorMessage responseBody = testClient
                .post()
                .uri(CLIENTS_URI)
                .headers(JwtAuthentication.getHeadersAuthorization(testClient, ADMIN_USER, ADMIN_PASS))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new ClientRequestDto("Empresa Diferente", "26881665000145", "diferente@empresa.com", "11999998888"))
                .exchange()
                .expectStatus().isEqualTo(409)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(409);
    }

    @Test
    public void createClient_WithInvalidData_ReturnsStatus422(){
        ErrorMessage responseBody = testClient
                .post()
                .uri(CLIENTS_URI)
                .headers(JwtAuthentication.getHeadersAuthorization(testClient, ADMIN_USER, ADMIN_PASS))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new ClientRequestDto("", "", "", ""))
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getStatus()).isEqualTo(422);
    }

    @Test
    public void createClient_WithoutAuthentication_ReturnsStatus401(){
        testClient
                .post()
                .uri(CLIENTS_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new ClientRequestDto("Nova Empresa Ltda", "12345678000190", "nova@empresa.com", "11999998888"))
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    public void createClient_WithAttendantUser_ReturnsStatus403(){
        testClient
                .post()
                .uri(CLIENTS_URI)
                .headers(JwtAuthentication.getHeadersAuthorization(testClient, ATTENDANT_USER, ATTENDANT_PASS))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new ClientRequestDto("Nova Empresa Ltda", "12345678000190", "nova@empresa.com", "11999998888"))
                .exchange()
                .expectStatus().isForbidden();
    }

}
