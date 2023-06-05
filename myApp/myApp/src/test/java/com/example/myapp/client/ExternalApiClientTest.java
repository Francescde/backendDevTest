package com.example.myapp.client;

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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class ExternalApiClientTest {

    private ExternalApiClient externalApiClient;

    @Mock
    private RestTemplate restTemplate;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        externalApiClient = new ExternalApiClient(restTemplate);
    }

    @Test
    public void testFetchSimilarProductIds() {
        // Mock the response from restTemplate
        String productId = "123";
        String[] productIdsArray = {"456", "789"};
        ResponseEntity<String[]> responseEntity = new ResponseEntity<>(productIdsArray, HttpStatus.OK);
        when(restTemplate.getForEntity(anyString(), eq(String[].class), eq(productId))).thenReturn(responseEntity);

        // Call the method under test
        List<String> similarProductIds = externalApiClient.fetchSimilarProductIds(productId);

        // Verify the restTemplate was called with the correct arguments
        String expectedUrl = "http://host.docker.internal:3001/product/123/similarids";
        verify(restTemplate).getForEntity(expectedUrl, String[].class, productId);

        // Verify the returned list is correct
        List<String> expectedProductIds = Arrays.asList(productIdsArray);
        assertEquals(expectedProductIds, similarProductIds);
    }

    @Test
    public void testGetProductDetail() {
        // Mock the response from restTemplate
        String productId = "123";
        ProductDetail productDetail = new ProductDetail();
        productDetail.setId(productId);
        productDetail.setName("Product 1");
        ResponseEntity<ProductDetail> responseEntity = new ResponseEntity<>(productDetail, HttpStatus.OK);
        when(restTemplate.getForEntity(anyString(), eq(ProductDetail.class), eq(productId))).thenReturn(responseEntity);

        // Call the method under test
        ProductDetail fetchedProductDetail = externalApiClient.getProductDetail(productId);

        // Verify the restTemplate was called with the correct arguments
        String expectedUrl = "http://host.docker.internal:3001/product/123";
        verify(restTemplate).getForEntity(expectedUrl, ProductDetail.class, productId);

        // Verify the returned ProductDetail is correct
        assertEquals(productDetail, fetchedProductDetail);
    }
}
