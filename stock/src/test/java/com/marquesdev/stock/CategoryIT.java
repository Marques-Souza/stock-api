package com.marquesdev.stock;

import com.marquesdev.stock.JwtAuthentication;
import com.marquesdev.stock.dto.CategoryResponseDto;
import com.marquesdev.stock.exception.ErrorMessage;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = "app.upload.images=target/category-it-uploads")
@Sql(scripts = "/sql/categories/categories-insert.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/sql/categories/categories-delete.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@AutoConfigureWebTestClient
public class CategoryIT {

    private static final String CATEGORIES_URI = "/api/v1/categories";
    private static final Path UPLOAD_DIRECTORY = Path.of("target/category-it-uploads");
    private static final byte[] IMAGE_CONTENT = {
            (byte) 0x89, 0x50, 0x4e, 0x47, 0x0d, 0x0a, 0x1a, 0x0a
    };

    private static final UUID CATEGORY_ID_1 =
            UUID.fromString("0a1b2c3d-4e5f-6789-abcd-ef0123456789");
    private static final UUID INEXISTENT_ID =
            UUID.fromString("a1b2c3d4-e5f6-7890-abcd-ef1234567890");

    private static final String ADMIN_USER = "support@gmail.com";
    private static final String ADMIN_PASS = "123456";
    private static final String ATTENDANT_USER = "karla@gmail.com";
    private static final String ATTENDANT_PASS = "123456";

    @Autowired
    private WebTestClient testClient;

    @BeforeEach
    void createStoredImageFixtures() throws IOException {
        Path categoriesDirectory = UPLOAD_DIRECTORY.resolve("categories");
        Files.createDirectories(categoriesDirectory);
        Files.write(categoriesDirectory.resolve("eletronicos.png"), IMAGE_CONTENT);
        Files.write(categoriesDirectory.resolve("moveis.png"), IMAGE_CONTENT);
    }

    @Test
    public void createCategory_WithValidData_ReturnsCreatedCategoryAndStoresImage() {
        CategoryResponseDto responseBody = testClient
                .post()
                .uri(CATEGORIES_URI)
                .headers(JwtAuthentication.getHeadersAuthorization(testClient, ADMIN_USER, ADMIN_PASS))
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(categoryForm("Nova Categoria", "Descricao da categoria")))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(CategoryResponseDto.class)
                .returnResult().getResponseBody();

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.id()).isNotNull();
        assertThat(responseBody.name()).isEqualTo("Nova Categoria");
        assertThat(responseBody.description()).isEqualTo("Descricao da categoria");
        assertThat(responseBody.imageUrl()).contains("/uploads/categories/");
        assertThat(storedImage(responseBody.imageUrl())).exists();
    }

    @Test
    public void createCategory_WithDuplicateName_ReturnsStatus409() {
        ErrorMessage responseBody = testClient
                .post()
                .uri(CATEGORIES_URI)
                .headers(JwtAuthentication.getHeadersAuthorization(testClient, ADMIN_USER, ADMIN_PASS))
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(categoryForm("ELETRONICOS", "Outra descricao")))
                .exchange()
                .expectStatus().isEqualTo(409)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getStatus()).isEqualTo(409);
    }

    @Test
    public void createCategory_WithInvalidData_ReturnsStatus422() {
        ErrorMessage responseBody = testClient
                .post()
                .uri(CATEGORIES_URI)
                .headers(JwtAuthentication.getHeadersAuthorization(testClient, ADMIN_USER, ADMIN_PASS))
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(categoryForm("", "")))
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getStatus()).isEqualTo(422);
    }

    @Test
    public void createCategory_WithoutAuthentication_ReturnsStatus401() {
        testClient
                .post()
                .uri(CATEGORIES_URI)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(categoryForm("Nova Categoria", "Descricao")))
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    public void createCategory_WithAttendantUser_ReturnsStatus403() {
        testClient
                .post()
                .uri(CATEGORIES_URI)
                .headers(JwtAuthentication.getHeadersAuthorization(testClient, ATTENDANT_USER, ATTENDANT_PASS))
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(categoryForm("Nova Categoria", "Descricao")))
                .exchange()
                .expectStatus().isForbidden();
    }

    @Test
    public void getCategoryById_WithValidId_ReturnsCategoryWithStatus200() {
        CategoryResponseDto responseBody = testClient
                .get()
                .uri(CATEGORIES_URI + "/" + CATEGORY_ID_1)
                .headers(JwtAuthentication.getHeadersAuthorization(testClient, ADMIN_USER, ADMIN_PASS))
                .exchange()
                .expectStatus().isOk()
                .expectBody(CategoryResponseDto.class)
                .returnResult().getResponseBody();

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.id()).isEqualTo(CATEGORY_ID_1);
        assertThat(responseBody.name()).isEqualTo("Eletronicos");
        assertThat(responseBody.description()).isEqualTo("Aparelhos e acessorios");
        assertThat(responseBody.imageUrl()).contains("categories/eletronicos.png");
    }

    @Test
    public void getCategoryById_WithInexistentId_ReturnsStatus404() {
        ErrorMessage responseBody = testClient
                .get()
                .uri(CATEGORIES_URI + "/" + INEXISTENT_ID)
                .headers(JwtAuthentication.getHeadersAuthorization(testClient, ADMIN_USER, ADMIN_PASS))
                .exchange()
                .expectStatus().isEqualTo(404)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getStatus()).isEqualTo(404);
    }

    @Test
    public void getCategoryById_WithoutAuthentication_ReturnsStatus401() {
        testClient
                .get()
                .uri(CATEGORIES_URI + "/" + CATEGORY_ID_1)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    public void getAllCategories_WithSearch_ReturnsMatchingCategories() {
        testClient
                .get()
                .uri(CATEGORIES_URI)
                .headers(JwtAuthentication.getHeadersAuthorization(testClient, ATTENDANT_USER, ATTENDANT_PASS))
                .header("search", "tronic")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.content.length()").isEqualTo(1)
                .jsonPath("$.content[0].id").isEqualTo(CATEGORY_ID_1.toString());
    }

    @Test
    public void getAllCategories_WithoutAuthentication_ReturnsStatus401() {
        testClient
                .get()
                .uri(CATEGORIES_URI)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    public void updateCategory_WithValidDataAndNewImage_ReturnsUpdatedCategory() {
        CategoryResponseDto responseBody = testClient
                .put()
                .uri(CATEGORIES_URI + "/" + CATEGORY_ID_1)
                .headers(JwtAuthentication.getHeadersAuthorization(testClient, ADMIN_USER, ADMIN_PASS))
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(categoryForm("Eletronicos Atualizados", "Descricao atualizada")))
                .exchange()
                .expectStatus().isOk()
                .expectBody(CategoryResponseDto.class)
                .returnResult().getResponseBody();

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.id()).isEqualTo(CATEGORY_ID_1);
        assertThat(responseBody.name()).isEqualTo("Eletronicos Atualizados");
        assertThat(responseBody.description()).isEqualTo("Descricao atualizada");
        assertThat(responseBody.imageUrl()).contains("/uploads/categories/");
        assertThat(storedImage(responseBody.imageUrl())).exists();
        assertThat(UPLOAD_DIRECTORY.resolve("categories/eletronicos.png")).doesNotExist();
    }

    @Test
    public void updateCategory_WithoutNewImage_PreservesExistingImage() {
        CategoryResponseDto responseBody = testClient
                .put()
                .uri(CATEGORIES_URI + "/" + CATEGORY_ID_1)
                .headers(JwtAuthentication.getHeadersAuthorization(testClient, ADMIN_USER, ADMIN_PASS))
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(categoryFormWithoutImage(
                        "Eletronicos Atualizados", "Descricao atualizada")))
                .exchange()
                .expectStatus().isOk()
                .expectBody(CategoryResponseDto.class)
                .returnResult().getResponseBody();

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.name()).isEqualTo("Eletronicos Atualizados");
        assertThat(responseBody.imageUrl()).contains("categories/eletronicos.png");
        assertThat(UPLOAD_DIRECTORY.resolve("categories/eletronicos.png")).exists();
    }

    @Test
    public void updateCategory_WithDuplicateName_ReturnsStatus409() {
        ErrorMessage responseBody = testClient
                .put()
                .uri(CATEGORIES_URI + "/" + CATEGORY_ID_1)
                .headers(JwtAuthentication.getHeadersAuthorization(testClient, ADMIN_USER, ADMIN_PASS))
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(categoryFormWithoutImage(
                        "Moveis", "Descricao atualizada")))
                .exchange()
                .expectStatus().isEqualTo(409)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getStatus()).isEqualTo(409);
    }

    @Test
    public void updateCategory_WithInexistentId_ReturnsStatus404() {
        ErrorMessage responseBody = testClient
                .put()
                .uri(CATEGORIES_URI + "/" + INEXISTENT_ID)
                .headers(JwtAuthentication.getHeadersAuthorization(testClient, ADMIN_USER, ADMIN_PASS))
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(categoryFormWithoutImage("Nova categoria", "Descricao")))
                .exchange()
                .expectStatus().isEqualTo(404)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getStatus()).isEqualTo(404);
    }

    @Test
    public void updateCategory_WithInvalidData_ReturnsStatus422() {
        ErrorMessage responseBody = testClient
                .put()
                .uri(CATEGORIES_URI + "/" + CATEGORY_ID_1)
                .headers(JwtAuthentication.getHeadersAuthorization(testClient, ADMIN_USER, ADMIN_PASS))
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(categoryFormWithoutImage("x".repeat(201), "Descricao")))
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getStatus()).isEqualTo(422);
    }

    @Test
    public void updateCategory_WithoutAuthentication_ReturnsStatus401() {
        testClient
                .put()
                .uri(CATEGORIES_URI + "/" + CATEGORY_ID_1)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(categoryFormWithoutImage("Nome", "Descricao")))
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    public void updateCategory_WithAttendantUser_ReturnsStatus403() {
        testClient
                .put()
                .uri(CATEGORIES_URI + "/" + CATEGORY_ID_1)
                .headers(JwtAuthentication.getHeadersAuthorization(testClient, ATTENDANT_USER, ATTENDANT_PASS))
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(categoryFormWithoutImage("Nome", "Descricao")))
                .exchange()
                .expectStatus().isForbidden();
    }

    @AfterAll
    static void cleanUploadedImages() throws IOException {
        if (Files.exists(UPLOAD_DIRECTORY)) {
            try (Stream<Path> paths = Files.walk(UPLOAD_DIRECTORY)) {
                paths.sorted(Comparator.reverseOrder()).forEach(path -> {
                    try {
                        Files.deleteIfExists(path);
                    } catch (IOException exception) {
                        throw new UncheckedIOException(exception);
                    }
                });
            } catch (UncheckedIOException exception) {
                throw exception.getCause();
            }
        }
    }

    private static MultiValueMap<String, HttpEntity<?>> categoryForm(
            String name,
            String description) {
        MultipartBodyBuilder builder = categoryFormBuilder(name, description);
        builder.part("file", new ByteArrayResource(IMAGE_CONTENT) {
            @Override
            public String getFilename() {
                return "category.png";
            }
        }).contentType(MediaType.IMAGE_PNG);
        return builder.build();
    }

    private static MultiValueMap<String, HttpEntity<?>> categoryFormWithoutImage(
            String name,
            String description) {
        return categoryFormBuilder(name, description).build();
    }

    private static MultipartBodyBuilder categoryFormBuilder(String name, String description) {
        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("name", name);
        builder.part("description", description);
        return builder;
    }

    private static Path storedImage(String imageUrl) {
        String imageKey = imageUrl.substring(imageUrl.indexOf("/uploads/") + "/uploads/".length());
        return UPLOAD_DIRECTORY.resolve(imageKey);
    }
}
