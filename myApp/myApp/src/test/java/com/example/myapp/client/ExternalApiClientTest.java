package com.example.myapp.client;
import com.example.myapp.client.ExternalApiClient;
import com.example.myapp.model.ProductDetail;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

public class ExternalApiClientTest {
    @Mock
    private RestTemplate restTemplate;

    private ExternalApiClient externalApiClient;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        externalApiClient = new ExternalApiClient(restTemplate);
    }

    @Test
    public void testFetchSimilarProductIds() {
        String productId = "123";
        String[] productIdsArray = {"456", "789"};
        List<String> expectedSimilarProductIds = Arrays.asList(productIdsArray);
        ResponseEntity<String[]> responseEntity = new ResponseEntity<>(productIdsArray, HttpStatus.OK);

        when(restTemplate.getForEntity(anyString(), eq(String[].class), eq(productId))).thenReturn(responseEntity);

        List<String> similarProductIds = externalApiClient.fetchSimilarProductIds(productId);

        assertEquals(expectedSimilarProductIds, similarProductIds);
    }

    @Test
    public void testGetFutureProductDetail() {
        String productId = "123";
        ProductDetail expectedProductDetail = new ProductDetail();
        expectedProductDetail.setId("123");
        expectedProductDetail.setName("Product 123");

        ResponseEntity<ProductDetail> responseEntity = new ResponseEntity<>(expectedProductDetail, HttpStatus.OK);

        when(restTemplate.getForEntity(anyString(), eq(ProductDetail.class), eq(productId))).thenReturn(responseEntity);

        CompletableFuture<ProductDetail> futureProductDetail = externalApiClient.getFutureProductDetail(productId);

        assertEquals(expectedProductDetail, futureProductDetail.join());
    }
}
