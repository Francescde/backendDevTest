package com.example.myapp.service;

import com.example.myapp.client.ExternalApiClient;
import com.example.myapp.exception.NotFoundException;
import com.example.myapp.model.ProductDetail;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class SimilarProductsServiceTest {

    @Mock
    private ExternalApiClient externalApiClient;

    private SimilarProductsService similarProductsService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        similarProductsService = new SimilarProductsService(externalApiClient);
    }

    @Test
    public void testGetSimilarProducts_ExternalApiReturnsValidIds_ReturnsProductDetails() {
        // Arrange
        String productId = "123";
        List<String> similarProductIds = new ArrayList<>();
        similarProductIds.add("456");
        similarProductIds.add("789");
        when(externalApiClient.fetchSimilarProductIds(productId)).thenReturn(similarProductIds);

        ProductDetail productDetail1 = new ProductDetail();
        productDetail1.setName("Product 1");
        productDetail1.setId("456");
        ProductDetail productDetail2 = new ProductDetail();
        productDetail1.setName("Product 2");
        productDetail1.setId("789");
        when(externalApiClient.getProductDetail("456")).thenReturn(productDetail1);
        when(externalApiClient.getProductDetail("789")).thenReturn(productDetail2);

        // Act
        List<ProductDetail> result = similarProductsService.getSimilarProducts(productId);

        // Assert
        assertEquals(2, result.size());
        assertEquals(productDetail1, result.get(0));
        assertEquals(productDetail2, result.get(1));

        // Verify that the external API methods were called
        verify(externalApiClient).fetchSimilarProductIds(productId);
        verify(externalApiClient).getProductDetail("456");
        verify(externalApiClient).getProductDetail("789");
    }

    @Test
    public void testGetSimilarProducts_ExternalApiReturnsEmptyIds_ReturnsEmptyList() {
        // Arrange
        String productId = "123";
        List<String> similarProductIds = new ArrayList<>();
        when(externalApiClient.fetchSimilarProductIds(productId)).thenReturn(similarProductIds);

        // Act
        List<ProductDetail> result = similarProductsService.getSimilarProducts(productId);

        // Assert
        assertEquals(0, result.size());

        // Verify that the external API methods were called
        verify(externalApiClient).fetchSimilarProductIds(productId);
        // No calls to getProductDetail() expected
        verifyNoMoreInteractions(externalApiClient);
    }

    @Test
    public void testGetSimilarProducts_ExternalApiThrowsException_ThrowsNotFoundException() {
        // Arrange
        String productId = "123";
        when(externalApiClient.fetchSimilarProductIds(productId)).thenThrow(new RuntimeException());

        // Act & Assert
        NotFoundException exception =
                assertThrows(NotFoundException.class, () -> similarProductsService.getSimilarProducts(productId));
        assertEquals("Similar products not found for productId=123", exception.getMessage());

        // Verify that the external API methods were called
        verify(externalApiClient).fetchSimilarProductIds(productId);
        // No calls to getProductDetail() expected
        verifyNoMoreInteractions(externalApiClient);
    }
}
