package com.example.myapp.service;

import com.example.myapp.client.ExternalApiClient;
import com.example.myapp.exception.NotFoundException;
import com.example.myapp.model.ProductDetail;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class SimilarProductsServiceTest {

    private SimilarProductsService similarProductsService;

    @Mock
    private ExternalApiClient externalApiClient;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        similarProductsService = new SimilarProductsService(externalApiClient);
    }

    @Test
    public void testGetSimilarProducts() {
        // Mock the externalApiClient.fetchSimilarProductIds() response
        String productId = "123";
        List<String> similarProductIds = Arrays.asList("456", "789");
        when(externalApiClient.fetchSimilarProductIds(eq(productId))).thenReturn(similarProductIds);

        // Mock the externalApiClient.getProductDetail() response
        ProductDetail productDetail1 = new ProductDetail();
        productDetail1.setId("456");
        productDetail1.setName("Product 1");

        ProductDetail productDetail2 = new ProductDetail();
        productDetail2.setId("789");
        productDetail2.setName("Product 2");

        when(externalApiClient.getProductDetail(eq("456"))).thenReturn(productDetail1);
        when(externalApiClient.getProductDetail(eq("789"))).thenReturn(productDetail2);

        // Call the method under test
        List<ProductDetail> similarProducts = similarProductsService.getSimilarProducts(productId);

        // Verify the externalApiClient.fetchSimilarProductIds() was called with the correct argument
        verify(externalApiClient).fetchSimilarProductIds(productId);

        // Verify the externalApiClient.getProductDetail() was called with the correct arguments
        verify(externalApiClient).getProductDetail("456");
        verify(externalApiClient).getProductDetail("789");

        // Verify the returned list is correct
        assertEquals(2, similarProducts.size());
        assertTrue(similarProducts.contains(productDetail1));
        assertTrue(similarProducts.contains(productDetail2));
    }

    @Test
    public void testGetSimilarProducts_NoSimilarProductIds() {
        // Mock the externalApiClient.fetchSimilarProductIds() response
        String productId = "123";
        List<String> similarProductIds = new ArrayList<>();
        when(externalApiClient.fetchSimilarProductIds(eq(productId))).thenReturn(similarProductIds);

        // Call the method under test and verify the NotFoundException is thrown
        assertThrows(NotFoundException.class, () -> similarProductsService.getSimilarProducts(productId));

        // Verify the externalApiClient.fetchSimilarProductIds() was called with the correct argument
        verify(externalApiClient).fetchSimilarProductIds(productId);
    }

    @Test
    public void testGetSimilarProducts_ProductDetailsNotFound() {
        // Mock the externalApiClient.fetchSimilarProductIds() response
        String productId = "123";
        List<String> similarProductIds = Arrays.asList("456", "789");
        when(externalApiClient.fetchSimilarProductIds(eq(productId))).thenReturn(similarProductIds);

        // Mock the externalApiClient.getProductDetail() response with null values
        when(externalApiClient.getProductDetail(eq("456"))).thenReturn(null);
        when(externalApiClient.getProductDetail(eq("789"))).thenReturn(null);

        // Call the method under test and verify the NotFoundException is thrown
        assertThrows(NotFoundException.class, () -> similarProductsService.getSimilarProducts(productId));

        // Verify the externalApiClient.fetchSimilarProductIds() was called with the correct argument
        verify(externalApiClient).fetchSimilarProductIds(productId);

        // Verify the externalApiClient.getProductDetail() was called with the correct arguments
        verify(externalApiClient).getProductDetail("456");
        verify(externalApiClient).getProductDetail("789");
    }
}
