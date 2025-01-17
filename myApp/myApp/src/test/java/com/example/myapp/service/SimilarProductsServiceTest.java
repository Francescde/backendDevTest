package com.example.myapp.service;

import com.example.myapp.client.MocksApiClient;
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
import static org.mockito.Mockito.*;

public class SimilarProductsServiceTest {

    @Mock
    private MocksApiClient mocksApiClient;

    private SimilarProductsService similarProductsService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        similarProductsService = new SimilarProductsService(mocksApiClient);
    }

    @Test
    public void testGetSimilarProducts_ExternalApiReturnsValidIds_ReturnsProductDetails() {
        // Arrange
        String productId = "123";
        List<String> similarProductIds = new ArrayList<>();
        similarProductIds.add("456");
        similarProductIds.add("789");
        when(mocksApiClient.fetchSimilarProductIds(productId)).thenReturn(similarProductIds);

        ProductDetail productDetail1 = new ProductDetail();
        productDetail1.setName("Product 1");
        productDetail1.setId("456");
        ProductDetail productDetail2 = new ProductDetail();
        productDetail1.setName("Product 2");
        productDetail1.setId("789");
        when(mocksApiClient.getProductDetail("456")).thenReturn(productDetail1);
        when(mocksApiClient.getProductDetail("789")).thenReturn(productDetail2);

        // Act
        List<ProductDetail> result = similarProductsService.getSimilarProducts(productId);

        // Assert
        assertEquals(2, result.size());
        assertEquals(productDetail1, result.get(0));
        assertEquals(productDetail2, result.get(1));

        // Verify that the external API methods were called
        verify(mocksApiClient).fetchSimilarProductIds(productId);
        verify(mocksApiClient).getProductDetail("456");
        verify(mocksApiClient).getProductDetail("789");
    }

    @Test
    public void testGetSimilarProducts_ExternalApiReturnsEmptyIds_ReturnsEmptyList() {
        // Arrange
        String productId = "123";
        List<String> similarProductIds = new ArrayList<>();
        when(mocksApiClient.fetchSimilarProductIds(productId)).thenReturn(similarProductIds);

        // Act
        List<ProductDetail> result = similarProductsService.getSimilarProducts(productId);

        // Assert
        assertEquals(0, result.size());

        // Verify that the external API methods were called
        verify(mocksApiClient).fetchSimilarProductIds(productId);
        // No calls to getProductDetail() expected
        verifyNoMoreInteractions(mocksApiClient);
    }

    @Test
    public void testGetSimilarProducts_ExternalApiThrowsException_ThrowsSameException() {
        // Arrange
        String productId = "123";
        String errorMessage = "Similar products not found for productId=123\"";
        when(mocksApiClient.fetchSimilarProductIds(productId)).thenThrow(new RuntimeException(errorMessage));

        // Act & Assert
        RuntimeException exception =
                assertThrows(RuntimeException.class, () -> similarProductsService.getSimilarProducts(productId));
        assertEquals(errorMessage, exception.getMessage());

        // Verify that the external API methods were called
        verify(mocksApiClient).fetchSimilarProductIds(productId);
        // No calls to getProductDetail() expected
        verifyNoMoreInteractions(mocksApiClient);
    }
}
