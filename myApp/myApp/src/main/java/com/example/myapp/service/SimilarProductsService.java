package com.example.myapp.service;

import com.example.myapp.client.ExternalApiClient;
import com.example.myapp.exception.NotFoundException;
import com.example.myapp.model.ProductDetail;
import org.springframework.stereotype.Service;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class SimilarProductsService {
    private final ExternalApiClient externalApiClient;
    private static final Logger logger = LoggerFactory.getLogger(SimilarProductsService.class);

    public SimilarProductsService(ExternalApiClient externalApiClient) {
        this.externalApiClient = externalApiClient;
    }

    public List<ProductDetail> getSimilarProducts(String productId) {
        logger.debug("Fetching similar products for productId={}", productId);

        List<String> similarProductIds = externalApiClient.fetchSimilarProductIds(productId);

        if (similarProductIds.isEmpty()) {
            String errorMessage = "Similar products not found for productId=" + productId;
            logger.warn(errorMessage);
            throw new NotFoundException(errorMessage);
        }

        logger.debug("Fetched similar productIds for productId={}: {}", productId, similarProductIds);

        List<ProductDetail> similarProducts = externalApiClient.fetchProductDetails(similarProductIds);

        if (similarProducts.isEmpty()) {
            String errorMessage = "Similar product details not found for productId=" + productId;
            logger.warn(errorMessage);
            throw new NotFoundException(errorMessage);
        }

        logger.debug("Fetched similar products for productId={}: {}", productId, similarProducts);

        return similarProducts;
    }
}
