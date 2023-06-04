package com.example.myapp.controller;
import com.example.myapp.model.ProductDetail;
import com.example.myapp.service.SimilarProductsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class SimilarProductsControllerTest {
    @Mock
    private SimilarProductsService similarProductsService;

    private SimilarProductsController similarProductsController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        similarProductsController = new SimilarProductsController(similarProductsService);
    }

    @Test
    void testGetSimilarProducts() throws ExecutionException, InterruptedException {
        String productId = "123";
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

        // Mock the similarProductsService
        when(similarProductsService.getSimilarProducts(productId)).thenReturn(expectedSimilarProducts);

        // Call the getSimilarProducts method
        ResponseEntity<List<ProductDetail>> responseEntity = similarProductsController.getSimilarProducts(productId);

        // Verify the similarProductsService method call
        verify(similarProductsService, times(1)).getSimilarProducts(productId);

        // Verify the response
        assertEquals(expectedSimilarProducts, responseEntity.getBody());
        assertEquals(200, responseEntity.getStatusCodeValue());
    }
}
