package com.example.myapp.client;

import com.example.myapp.model.ProductDetail;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class ExternalApiClientTest {

    private ExternalApiClient externalApiClient;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private Environment environment;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        externalApiClient = new ExternalApiClient(restTemplate, environment);
    }

    @Test
    void fetchSimilarProductIds_ShouldReturnSimilarProductIds() {
        String productId = "123";
        String[] similarProductIds = {"456", "789"};
        String apiUrl = "http://example.com/similar-products/{productId}";

        when(environment.getProperty(anyString())).thenReturn(apiUrl);
        when(restTemplate.getForEntity(apiUrl.replace("{productId}",productId), String[].class, productId))
                .thenReturn(new ResponseEntity<>(similarProductIds, HttpStatus.OK));

        List<String> expectedProductIds = Arrays.asList(similarProductIds);
        List<String> actualProductIds = externalApiClient.fetchSimilarProductIds(productId);

        assertEquals(expectedProductIds, actualProductIds);
    }

    @Test
    void getProductDetail_ShouldReturnProductDetail() {
        String productId = "123";
        ProductDetail productDetail = new ProductDetail();
        productDetail.setId(productId);
        productDetail.setName("Product 1");
        productDetail.setPrice(10.0);
        String apiUrl = "http://example.com/product/{productId}";

        when(environment.getProperty(anyString())).thenReturn(apiUrl);
        when(restTemplate.getForEntity(apiUrl.replace("{productId}",productId), ProductDetail.class, productId))
                .thenReturn(new ResponseEntity<>(productDetail, HttpStatus.OK));

        ProductDetail expectedProductDetail = productDetail;
        ProductDetail actualProductDetail = externalApiClient.getProductDetail(productId);

        assertEquals(expectedProductDetail, actualProductDetail);
    }
}
