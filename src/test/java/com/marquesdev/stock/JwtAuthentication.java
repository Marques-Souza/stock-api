package com.marquesdev.stock;

import com.marquesdev.stock.dto.UserLoginDto;
import com.marquesdev.stock.jwt.JwtToken;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.http.HttpHeaders;

import java.time.Duration;
import java.util.function.Consumer;

public class JwtAuthentication {

    private static final Duration AUTHENTICATION_TIMEOUT = Duration.ofSeconds(30);

    public static Consumer<HttpHeaders> getHeadersAuthorization(WebTestClient client, String email, String password){
        String token = client.mutate()
                .responseTimeout(AUTHENTICATION_TIMEOUT)
                .build()
                .post()
                .uri("/api/v1/auth")
                .bodyValue(new UserLoginDto(email, password))
                .exchange()
                .expectStatus().isOk()
                .expectBody(JwtToken.class)
                .returnResult().getResponseBody().getToken();
        return headers -> headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + token);

    }
}
