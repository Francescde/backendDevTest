package com.example.myapp.service;

import com.example.myapp.client.MocksApiClient;
import com.example.myapp.exception.NotFoundException;
import com.example.myapp.model.ProductDetail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class SimilarProductsService {
    private final MocksApiClient mocksApiClient;
    private static final Logger logger = LoggerFactory.getLogger(SimilarProductsService.class);

    public SimilarProductsService(MocksApiClient mocksApiClient) {
        this.mocksApiClient = mocksApiClient;
    }

    public List<ProductDetail> getSimilarProducts(String productId) {
        logger.debug("Fetching similar products for productId={}", productId);

        List<String> similarProductIds;
        try {
            similarProductIds = mocksApiClient.fetchSimilarProductIds(productId);
        } catch (Exception e) {
            throw new NotFoundException("Similar products not found for productId="+productId);
        }

        if (similarProductIds == null) {
            handleExternalError("Similar products null for productId=" + productId);
        } else if (similarProductIds.isEmpty()) {
            logger.warn("Similar products not found for productId={}", productId);
            return new ArrayList<>();
        }

        logger.debug("Fetched similar productIds for productId={}: {}", productId, similarProductIds);

        List<ProductDetail> similarProducts = fetchProductDetails(similarProductIds);

        if (similarProducts.isEmpty()) {
            logger.warn("No product detail found for productId={}, expected {}",
                    productId, similarProductIds.size());
            return new ArrayList<>();
        }

        logger.debug("Fetched similar products for productId={}: {}", productId, similarProducts);

        return similarProducts;
    }

    private void handleExternalError(String errorMessage) {
        logger.error("Error occurred while waiting for request to complete");
        throw new NotFoundException(errorMessage);
        // Handle the external error here (e.g., provide fallback response, retry, log, etc.)
    }

    private List<ProductDetail> fetchProductDetails(List<String> productIds) {
        List<CompletableFuture<ProductDetail>> futures = productIds.stream()
                .map(productId -> CompletableFuture.supplyAsync(() -> {
                    ProductDetail responseBody = mocksApiClient.getProductDetail(productId);
                    logger.debug("Fetched product detail for productId={}: {}", productId, responseBody);
                    return responseBody;
                }))
                .collect(Collectors.toList());

        CompletableFuture<Void> allFutures = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));

        try {
            allFutures.get(); // Wait for all the requests to complete
        } catch (Exception e) {
            handleExternalError("Error occurred while waiting for requests to complete");
        }

        List<ProductDetail> productDetails = futures.stream()
                .filter(CompletableFuture::isDone)
                .filter(future -> !future.isCompletedExceptionally())
                .map(future -> {
                    try {
                        return future.get(); // Get the result of each request
                    } catch (Exception e) {
                        logger.error("Error occurred while getting the result of a request", e);
                        return null;
                    }
                })
                .filter(result -> result != null)
                .collect(Collectors.toList());

        return productDetails;
    }
}
