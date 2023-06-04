package com.example.myapp.client;

import com.example.myapp.client.ExternalApiClient;
import com.example.myapp.model.ProductDetail;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class ExternalApiClientTest {
    private final RestTemplate restTemplate = mock(RestTemplate.class);
    private final ExternalApiClient externalApiClient = new ExternalApiClient(restTemplate);

    @Test
    public void testFetchSimilarProductIds_Success() {
        // Mock the successful response from the external API
        String[] productIdsArray = {"1", "2", "3"};
        ResponseEntity<String[]> responseEntity = new ResponseEntity<>(productIdsArray, HttpStatus.OK);
        when(restTemplate.getForEntity(anyString(), eq(String[].class), anyString())).thenReturn(responseEntity);

        // Call the method under test
        List<String> similarProductIds = externalApiClient.fetchSimilarProductIds("123");

        // Verify the interactions and assertions
        verify(restTemplate).getForEntity(eq("http://host.docker.internal:3001/product/{productId}/similarids"),
                eq(String[].class), eq("123"));
        assertEquals(Arrays.asList(productIdsArray), similarProductIds);
    }

    @Test
    public void testFetchSimilarProductIds_Failure() {
        // Mock the failure response from the external API
        ResponseEntity<String[]> responseEntity = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        when(restTemplate.getForEntity(anyString(), eq(String[].class), anyString())).thenReturn(responseEntity);

        // Call the method under test
        List<String> similarProductIds = externalApiClient.fetchSimilarProductIds("123");

        // Verify the interactions and assertions
        verify(restTemplate).getForEntity(eq("http://host.docker.internal:3001/product/{productId}/similarids"),
                eq(String[].class), eq("123"));
        assertEquals(Collections.emptyList(), similarProductIds);
    }

    @Test
    public void testFetchProductDetails_Success() {
        // Mock the successful response from the external API
        ProductDetail productDetail1 = new ProductDetail();
        productDetail1.setId("1");
        productDetail1.setName("Product 1");
        ProductDetail productDetail2 = new ProductDetail();
        productDetail2.setId("2");
        productDetail2.setName("Product 2");
        ResponseEntity<ProductDetail> responseEntity1 = new ResponseEntity<>(productDetail1, HttpStatus.OK);
        ResponseEntity<ProductDetail> responseEntity2 = new ResponseEntity<>(productDetail2, HttpStatus.OK);
        when(restTemplate.getForEntity(anyString(), eq(ProductDetail.class), anyString())).thenReturn(responseEntity1, responseEntity2);

        // Call the method under test
        List<ProductDetail> productDetails = externalApiClient.fetchProductDetails(Arrays.asList("1", "2"));

        // Verify the interactions and assertions
        verify(restTemplate).getForEntity(eq("http://host.docker.internal:3001/product/{productId}"),
                eq(ProductDetail.class), eq("1"));
        verify(restTemplate).getForEntity(eq("http://host.docker.internal:3001/product/{productId}"),
                eq(ProductDetail.class), eq("2"));
        assertEquals(Arrays.asList(productDetail1, productDetail2), productDetails);
    }

    @Test
    public void testFetchProductDetails_Failure() {
        // Mock the failure response from the external API
        ResponseEntity<ProductDetail> responseEntity = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        when(restTemplate.getForEntity(anyString(), eq(ProductDetail.class), anyString())).thenReturn(responseEntity);

        // Call the method under test
        List<ProductDetail> productDetails = externalApiClient.fetchProductDetails(Collections.singletonList("1"));

        // Verify the interactions and assertions
        verify(restTemplate).getForEntity(eq("http://host.docker.internal:3001/product/{productId}"),
                eq(ProductDetail.class), eq("1"));
        assertEquals(Collections.emptyList(), productDetails);
    }
}
