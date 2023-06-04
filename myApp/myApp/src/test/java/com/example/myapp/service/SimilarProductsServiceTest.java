package com.example.myapp.service;

import com.example.myapp.client.ExternalApiClient;
import com.example.myapp.exception.NotFoundException;
import com.example.myapp.model.ProductDetail;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SimilarProductsServiceTest {
    @Mock
    private ExternalApiClient externalApiClient;

    private SimilarProductsService similarProductsService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        similarProductsService = new SimilarProductsService(externalApiClient);
    }

    @Test
    void testGetSimilarProducts_Successful() {
        String productId = "123";
        List<String> similarProductIds = Arrays.asList("456", "789");

        // Mock the externalApiClient
        when(externalApiClient.fetchSimilarProductIds(productId)).thenReturn(similarProductIds);

        ProductDetail product1 = new ProductDetail();
        product1.setId("456");
        product1.setName("Product 1");
        product1.setPrice(10.0);
        product1.setAvailability(true);

        ProductDetail product2 = new ProductDetail();
        product2.setId("789");
        product2.setName("Product 2");
        product2.setPrice(20.0);
        product2.setAvailability(false);

        List<ProductDetail> expectedSimilarProducts = Arrays.asList(product1, product2);

        // Mock the externalApiClient
        when(externalApiClient.fetchProductDetails(similarProductIds)).thenReturn(expectedSimilarProducts);

        // Call the getSimilarProducts method
        List<ProductDetail> actualSimilarProducts = similarProductsService.getSimilarProducts(productId);

        // Verify the calls to externalApiClient
        verify(externalApiClient, times(1)).fetchSimilarProductIds(productId);
        verify(externalApiClient, times(1)).fetchProductDetails(similarProductIds);

        // Verify the result
        assertEquals(expectedSimilarProducts, actualSimilarProducts);
    }

    @Test
    void testGetSimilarProducts_NoSimilarProductIds() {
        String productId = "123";
        List<String> similarProductIds = Collections.emptyList();

        // Mock the externalApiClient
        when(externalApiClient.fetchSimilarProductIds(productId)).thenReturn(similarProductIds);

        // Call the getSimilarProducts method and assert that it throws NotFoundException
        assertThrows(NotFoundException.class, () -> similarProductsService.getSimilarProducts(productId));

        // Verify the calls to externalApiClient
        verify(externalApiClient, times(1)).fetchSimilarProductIds(productId);
        verifyNoMoreInteractions(externalApiClient);
    }

    @Test
    void testGetSimilarProducts_NoSimilarProducts() {
        String productId = "123";
        List<String> similarProductIds = Arrays.asList("456", "789");

        // Mock the externalApiClient
        when(externalApiClient.fetchSimilarProductIds(productId)).thenReturn(similarProductIds);
        when(externalApiClient.fetchProductDetails(similarProductIds)).thenReturn(Collections.emptyList());

        // Call the getSimilarProducts method and assert that it throws NotFoundException
        assertThrows(NotFoundException.class, () -> similarProductsService.getSimilarProducts(productId));

        // Verify the calls to externalApiClient
        verify(externalApiClient, times(1)).fetchSimilarProductIds(productId);
        verify(externalApiClient, times(1)).fetchProductDetails(similarProductIds);
    }
}

