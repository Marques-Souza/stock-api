package com.marquesdev.stock;

import com.marquesdev.stock.dto.ProductResponseDto;
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
@TestPropertySource(properties = "app.upload.images=target/product-it-uploads")
@Sql(scripts = "/sql/products/products-insert.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/sql/products/products-delete.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@AutoConfigureWebTestClient
public class ProductIT {

    private static final String PRODUCTS_URI = "/api/v1/products";
    private static final Path UPLOAD_DIRECTORY = Path.of("target/product-it-uploads");
    private static final byte[] IMAGE_CONTENT = {
            (byte) 0x89, 0x50, 0x4e, 0x47, 0x0d, 0x0a, 0x1a, 0x0a
    };

    private static final UUID CATEGORY_ID_1 =
            UUID.fromString("0a1b2c3d-4e5f-6789-abcd-ef0123456789");
    private static final UUID CATEGORY_ID_2 =
            UUID.fromString("1b2c3d4e-5f60-789a-bcde-f0123456789a");
    private static final UUID PRODUCT_ID_1 =
            UUID.fromString("2c3d4e5f-6071-489a-bcde-f0123456789a");
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
        Path productsDirectory = UPLOAD_DIRECTORY.resolve("products");
        Files.createDirectories(productsDirectory);
        Files.write(productsDirectory.resolve("teclado.png"), IMAGE_CONTENT);
        Files.write(productsDirectory.resolve("cadeira.png"), IMAGE_CONTENT);
    }

    @Test
    public void createProduct_WithValidData_ReturnsCreatedProductAndStoresImage() {
        ProductResponseDto responseBody = testClient
                .post()
                .uri(PRODUCTS_URI)
                .headers(JwtAuthentication.getHeadersAuthorization(testClient, ADMIN_USER, ADMIN_PASS))
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(productForm(
                        "Mouse", "Mouse sem fio", CATEGORY_ID_1, "PR-003", "59.90", "1")))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(ProductResponseDto.class)
                .returnResult().getResponseBody();

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.id()).isNotNull();
        assertThat(responseBody.name()).isEqualTo("Mouse");
        assertThat(responseBody.description()).isEqualTo("Mouse sem fio");
        assertThat(responseBody.categoryId()).isEqualTo(CATEGORY_ID_1);
        assertThat(responseBody.categoryName()).isEqualTo("Eletronicos");
        assertThat(responseBody.code()).isEqualTo("PR-003");
        assertThat(responseBody.quantity()).isZero();
        assertThat(responseBody.unitPrice()).isEqualByComparingTo("59.90");
        assertThat(responseBody.minimumStock()).isEqualTo(1);
        assertThat(responseBody.imageUrl()).contains("/uploads/products/");
        assertThat(storedImage(responseBody.imageUrl())).exists();
    }

    @Test
    public void createProduct_WithDuplicateCode_ReturnsStatus409() {
        ErrorMessage responseBody = testClient
                .post()
                .uri(PRODUCTS_URI)
                .headers(JwtAuthentication.getHeadersAuthorization(testClient, ADMIN_USER, ADMIN_PASS))
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(productForm(
                        "Outro teclado", "Descricao", CATEGORY_ID_1, "PR-001", "39.90", "1")))
                .exchange()
                .expectStatus().isEqualTo(409)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getStatus()).isEqualTo(409);
    }

    @Test
    public void createProduct_WithInvalidData_ReturnsStatus422() {
        ErrorMessage responseBody = testClient
                .post()
                .uri(PRODUCTS_URI)
                .headers(JwtAuthentication.getHeadersAuthorization(testClient, ADMIN_USER, ADMIN_PASS))
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(productForm(
                        "", "", CATEGORY_ID_1, "", "0", "-1")))
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getStatus()).isEqualTo(422);
    }

    @Test
    public void createProduct_WithUnsupportedImageFormat_ReturnsStatus400() {
        ErrorMessage responseBody = testClient
                .post()
                .uri(PRODUCTS_URI)
                .headers(JwtAuthentication.getHeadersAuthorization(testClient, ADMIN_USER, ADMIN_PASS))
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(productFormWithUnsupportedImage(
                        "Mouse", "Descricao", CATEGORY_ID_1, "PR-003", "59.90", "1")))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getStatus()).isEqualTo(400);
        assertThat(responseBody.getMessage()).isEqualTo("Unsupported image format.");
    }

    @Test
    public void createProduct_WithoutAuthentication_ReturnsStatus401() {
        testClient
                .post()
                .uri(PRODUCTS_URI)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(productForm(
                        "Mouse", "Descricao", CATEGORY_ID_1, "PR-003", "59.90", "1")))
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    public void createProduct_WithAttendantUser_ReturnsStatus403() {
        testClient
                .post()
                .uri(PRODUCTS_URI)
                .headers(JwtAuthentication.getHeadersAuthorization(testClient, ATTENDANT_USER, ATTENDANT_PASS))
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(productForm(
                        "Mouse", "Descricao", CATEGORY_ID_1, "PR-003", "59.90", "1")))
                .exchange()
                .expectStatus().isForbidden();
    }

    @Test
    public void getProductById_WithValidId_ReturnsProductWithStatus200() {
        ProductResponseDto responseBody = testClient
                .get()
                .uri(PRODUCTS_URI + "/" + PRODUCT_ID_1)
                .headers(JwtAuthentication.getHeadersAuthorization(testClient, ADMIN_USER, ADMIN_PASS))
                .exchange()
                .expectStatus().isOk()
                .expectBody(ProductResponseDto.class)
                .returnResult().getResponseBody();

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.id()).isEqualTo(PRODUCT_ID_1);
        assertThat(responseBody.name()).isEqualTo("Teclado");
        assertThat(responseBody.description()).isEqualTo("Teclado USB");
        assertThat(responseBody.categoryId()).isEqualTo(CATEGORY_ID_1);
        assertThat(responseBody.categoryName()).isEqualTo("Eletronicos");
        assertThat(responseBody.code()).isEqualTo("PR-001");
        assertThat(responseBody.quantity()).isEqualTo(4);
        assertThat(responseBody.imageUrl()).contains("products/teclado.png");
    }

    @Test
    public void getProductById_WithInexistentId_ReturnsStatus404() {
        ErrorMessage responseBody = testClient
                .get()
                .uri(PRODUCTS_URI + "/" + INEXISTENT_ID)
                .headers(JwtAuthentication.getHeadersAuthorization(testClient, ADMIN_USER, ADMIN_PASS))
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getStatus()).isEqualTo(404);
    }

    @Test
    public void getProductById_WithoutAuthentication_ReturnsStatus401() {
        testClient
                .get()
                .uri(PRODUCTS_URI + "/" + PRODUCT_ID_1)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    public void getAllProducts_WithNameSearch_ReturnsMatchingProducts() {
        testClient
                .get()
                .uri(PRODUCTS_URI)
                .headers(JwtAuthentication.getHeadersAuthorization(testClient, ATTENDANT_USER, ATTENDANT_PASS))
                .header("search", "tecl")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.content.length()").isEqualTo(1)
                .jsonPath("$.content[0].id").isEqualTo(PRODUCT_ID_1.toString());
    }

    @Test
    public void getAllProducts_WithCodeSearch_ReturnsMatchingProducts() {
        testClient
                .get()
                .uri(PRODUCTS_URI)
                .headers(JwtAuthentication.getHeadersAuthorization(testClient, ATTENDANT_USER, ATTENDANT_PASS))
                .header("search", "PR-002")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.content.length()").isEqualTo(1)
                .jsonPath("$.content[0].code").isEqualTo("PR-002");
    }

    @Test
    public void getAllProducts_WithoutAuthentication_ReturnsStatus401() {
        testClient
                .get()
                .uri(PRODUCTS_URI)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    public void updateProduct_WithValidDataAndNewImage_ReturnsUpdatedProduct() {
        ProductResponseDto responseBody = testClient
                .put()
                .uri(PRODUCTS_URI + "/" + PRODUCT_ID_1)
                .headers(JwtAuthentication.getHeadersAuthorization(testClient, ADMIN_USER, ADMIN_PASS))
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(productForm(
                        "Teclado atualizado", "Descricao atualizada", CATEGORY_ID_2,
                        "PR-010", "120.50", "5")))
                .exchange()
                .expectStatus().isOk()
                .expectBody(ProductResponseDto.class)
                .returnResult().getResponseBody();

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.id()).isEqualTo(PRODUCT_ID_1);
        assertThat(responseBody.name()).isEqualTo("Teclado atualizado");
        assertThat(responseBody.description()).isEqualTo("Descricao atualizada");
        assertThat(responseBody.categoryId()).isEqualTo(CATEGORY_ID_2);
        assertThat(responseBody.categoryName()).isEqualTo("Moveis");
        assertThat(responseBody.code()).isEqualTo("PR-010");
        assertThat(responseBody.unitPrice()).isEqualByComparingTo("120.50");
        assertThat(responseBody.minimumStock()).isEqualTo(5);
        assertThat(responseBody.quantity()).isEqualTo(4);
        assertThat(responseBody.imageUrl()).contains("/uploads/products/");
        assertThat(storedImage(responseBody.imageUrl())).exists();
        assertThat(UPLOAD_DIRECTORY.resolve("products/teclado.png")).doesNotExist();
    }

    @Test
    public void updateProduct_WithoutNewImage_PreservesExistingImageAndUnsentFields() {
        ProductResponseDto responseBody = testClient
                .put()
                .uri(PRODUCTS_URI + "/" + PRODUCT_ID_1)
                .headers(JwtAuthentication.getHeadersAuthorization(testClient, ADMIN_USER, ADMIN_PASS))
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(productFormWithoutImage("Teclado atualizado")))
                .exchange()
                .expectStatus().isOk()
                .expectBody(ProductResponseDto.class)
                .returnResult().getResponseBody();

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.name()).isEqualTo("Teclado atualizado");
        assertThat(responseBody.description()).isEqualTo("Teclado USB");
        assertThat(responseBody.code()).isEqualTo("PR-001");
        assertThat(responseBody.imageUrl()).contains("products/teclado.png");
        assertThat(UPLOAD_DIRECTORY.resolve("products/teclado.png")).exists();
    }

    @Test
    public void updateProduct_WithDuplicateCode_ReturnsStatus409() {
        ErrorMessage responseBody = testClient
                .put()
                .uri(PRODUCTS_URI + "/" + PRODUCT_ID_1)
                .headers(JwtAuthentication.getHeadersAuthorization(testClient, ADMIN_USER, ADMIN_PASS))
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(productFormWithoutImageAndCode("PR-002")))
                .exchange()
                .expectStatus().isEqualTo(409)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getStatus()).isEqualTo(409);
    }

    @Test
    public void updateProduct_WithInexistentCategory_ReturnsStatus404() {
        ErrorMessage responseBody = testClient
                .put()
                .uri(PRODUCTS_URI + "/" + PRODUCT_ID_1)
                .headers(JwtAuthentication.getHeadersAuthorization(testClient, ADMIN_USER, ADMIN_PASS))
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(productFormWithoutImageAndCategory(INEXISTENT_ID)))
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getStatus()).isEqualTo(404);
    }

    @Test
    public void updateProduct_WithInexistentId_ReturnsStatus404() {
        ErrorMessage responseBody = testClient
                .put()
                .uri(PRODUCTS_URI + "/" + INEXISTENT_ID)
                .headers(JwtAuthentication.getHeadersAuthorization(testClient, ADMIN_USER, ADMIN_PASS))
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(productFormWithoutImage("Novo produto")))
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getStatus()).isEqualTo(404);
    }

    @Test
    public void updateProduct_WithInvalidData_ReturnsStatus422() {
        ErrorMessage responseBody = testClient
                .put()
                .uri(PRODUCTS_URI + "/" + PRODUCT_ID_1)
                .headers(JwtAuthentication.getHeadersAuthorization(testClient, ADMIN_USER, ADMIN_PASS))
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(productFormWithoutImageAndLongName("x".repeat(151))))
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getStatus()).isEqualTo(422);
    }

    @Test
    public void updateProduct_WithoutAuthentication_ReturnsStatus401() {
        testClient
                .put()
                .uri(PRODUCTS_URI + "/" + PRODUCT_ID_1)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(productFormWithoutImage("Novo nome")))
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    public void updateProduct_WithAttendantUser_ReturnsStatus403() {
        testClient
                .put()
                .uri(PRODUCTS_URI + "/" + PRODUCT_ID_1)
                .headers(JwtAuthentication.getHeadersAuthorization(testClient, ATTENDANT_USER, ATTENDANT_PASS))
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(productFormWithoutImage("Novo nome")))
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

    private static MultiValueMap<String, HttpEntity<?>> productForm(
            String name,
            String description,
            UUID categoryId,
            String code,
            String unitPrice,
            String minimumStock) {
        MultipartBodyBuilder builder = productFormBuilder(
                name, description, categoryId, code, unitPrice, minimumStock);
        builder.part("file", imageResource("product.png"))
                .contentType(MediaType.IMAGE_PNG);
        return builder.build();
    }

    private static MultiValueMap<String, HttpEntity<?>> productFormWithUnsupportedImage(
            String name,
            String description,
            UUID categoryId,
            String code,
            String unitPrice,
            String minimumStock) {
        MultipartBodyBuilder builder = productFormBuilder(
                name, description, categoryId, code, unitPrice, minimumStock);
        builder.part("file", imageResource("product.gif"))
                .contentType(MediaType.IMAGE_GIF);
        return builder.build();
    }

    private static MultiValueMap<String, HttpEntity<?>> productFormWithoutImage(String name) {
        return productFormBuilder(name, null, null, null, null, null).build();
    }

    private static MultiValueMap<String, HttpEntity<?>> productFormWithoutImageAndCode(String code) {
        return productFormBuilder(null, null, null, code, null, null).build();
    }

    private static MultiValueMap<String, HttpEntity<?>> productFormWithoutImageAndCategory(UUID categoryId) {
        return productFormBuilder(null, null, categoryId, null, null, null).build();
    }

    private static MultiValueMap<String, HttpEntity<?>> productFormWithoutImageAndLongName(String name) {
        return productFormBuilder(name, null, null, null, null, null).build();
    }

    private static MultipartBodyBuilder productFormBuilder(
            String name,
            String description,
            UUID categoryId,
            String code,
            String unitPrice,
            String minimumStock) {
        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        if (name != null) builder.part("name", name);
        if (description != null) builder.part("description", description);
        if (categoryId != null) builder.part("categoryId", categoryId.toString());
        if (code != null) builder.part("code", code);
        if (unitPrice != null) builder.part("unitPrice", unitPrice);
        if (minimumStock != null) builder.part("minimumStock", minimumStock);
        return builder;
    }

    private static ByteArrayResource imageResource(String filename) {
        return new ByteArrayResource(IMAGE_CONTENT) {
            @Override
            public String getFilename() {
                return filename;
            }
        };
    }

    private static Path storedImage(String imageUrl) {
        String imageKey = imageUrl.substring(imageUrl.indexOf("/uploads/") + "/uploads/".length());
        return UPLOAD_DIRECTORY.resolve(imageKey);
    }
}
