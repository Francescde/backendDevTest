package com.example.myapp.service;
import com.example.myapp.client.ExternalApiClient;
import com.example.myapp.exception.NotFoundException;
import com.example.myapp.model.ProductDetail;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

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
    public void testGetSimilarProducts_Successful() throws ExecutionException, InterruptedException {
        String productId = "123";
        List<String> similarProductIds = Arrays.asList("456", "789");

        when(externalApiClient.fetchSimilarProductIds(eq(productId))).thenReturn(similarProductIds);

        ProductDetail productDetail1 = new ProductDetail();
        productDetail1.setId("456");
        productDetail1.setId("Product 456");
        ProductDetail productDetail2 = new ProductDetail();
        productDetail1.setId("789");
        productDetail1.setId("Product 789");

        CompletableFuture<ProductDetail> future1 = CompletableFuture.completedFuture(productDetail1);
        CompletableFuture<ProductDetail> future2 = CompletableFuture.completedFuture(productDetail2);

        when(externalApiClient.getFutureProductDetail(eq("456"))).thenReturn(future1);
        when(externalApiClient.getFutureProductDetail(eq("789"))).thenReturn(future2);

        List<ProductDetail> expectedProductDetails = Arrays.asList(productDetail1, productDetail2);
        List<ProductDetail> similarProducts = similarProductsService.getSimilarProducts(productId);

        assertEquals(expectedProductDetails, similarProducts);
    }

    @Test
    public void testGetSimilarProducts_EmptySimilarProductIds() {
        String productId = "123";
        List<String> similarProductIds = new ArrayList<>();

        when(externalApiClient.fetchSimilarProductIds(eq(productId))).thenReturn(similarProductIds);

        assertThrows(NotFoundException.class, () -> similarProductsService.getSimilarProducts(productId));
    }

    @Test
    public void testGetSimilarProducts_ExceptionDuringFetchSimilarProductIds() {
        String productId = "123";

        when(externalApiClient.fetchSimilarProductIds(eq(productId))).thenThrow(new RuntimeException("Internal Server Error"));

        assertThrows(NotFoundException.class, () -> similarProductsService.getSimilarProducts(productId));
    }

    @Test
    public void testGetSimilarProducts_ExceptionDuringGetFutureProductDetail() throws ExecutionException, InterruptedException {
        String productId = "123";
        List<String> similarProductIds = Arrays.asList("456", "789");

        when(externalApiClient.fetchSimilarProductIds(eq(productId))).thenReturn(similarProductIds);

        ProductDetail productDetail1 = new ProductDetail();
        productDetail1.setId("456");
        productDetail1.setId("Product 456");
        CompletableFuture<ProductDetail> future1 = CompletableFuture.completedFuture(productDetail1);

        when(externalApiClient.getFutureProductDetail(eq("456"))).thenReturn(future1);
        when(externalApiClient.getFutureProductDetail(eq("789"))).thenThrow(new RuntimeException("Internal Server Error"));

        List<ProductDetail> expectedProductDetails = Arrays.asList(productDetail1);

        List<ProductDetail> similarProducts = similarProductsService.getSimilarProducts(productId);

        assertEquals(expectedProductDetails, similarProducts);
    }
}

