package com.example.myapp.service;

import com.example.myapp.client.ExternalApiClient;
import com.example.myapp.exception.NotFoundException;
import com.example.myapp.model.ProductDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

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
        List<String> similarProductIds;
        try {
            similarProductIds = externalApiClient.fetchSimilarProductIds(productId);
        } catch (Exception e) {
            logger.error("Error occurred while waiting for request to complete", e);
            String errorMessage = "Similar products not found for productId=" + productId;
            throw new NotFoundException(errorMessage);
            // Since this is an external error, we don't want to propagate it further
            // Instead, we can choose to perform alternative actions or provide a fallback response

            // Option 1: Provide a default response or fallback data
            // ...

            // Option 2: Retry the request after a delay
            // ...

            // Option 3: Notify the user about the error without exposing sensitive information
            // ...

            // Option 4: Log the error and continue processing, if applicable
            // ...

            // Add a comment to explain the handling of the external error and provide alternatives
            // We encountered an error while calling the external API. Since this is an external error,
            // we don't want to expose it directly to the client. Instead, we log the error for debugging
            // purposes and choose an appropriate alternative action based on the specific use case.
            // The alternatives mentioned above are some possible options, but the choice depends on the
            // application requirements and the impact of the error on the overall flow.
        }
        if (similarProductIds.isEmpty()) {
            String errorMessage = "Similar products not found for productId=" + productId;
            logger.warn(errorMessage);
            return new ArrayList<>();
        }

        logger.debug("Fetched similar productIds for productId={}: {}", productId, similarProductIds);

        List<ProductDetail> similarProducts = fetchProductDetails(similarProductIds);

        if (similarProducts.isEmpty()) {
            String errorMessage =
                    "Any product detail found for productId=" + productId + " when were " + similarProducts.size() +
                    " expected";
            logger.warn(errorMessage);
            return new ArrayList<>();
        }

        logger.debug("Fetched similar products for productId={}: {}", productId, similarProducts);

        return similarProducts;
    }


    private List<ProductDetail> fetchProductDetails(List<String> productIds) {
        List<CompletableFuture<ProductDetail>> futures = new ArrayList<>();

        for (String productId : productIds) {
            try {
                CompletableFuture<ProductDetail> future = CompletableFuture.supplyAsync(() -> {
                    ProductDetail responseBody = externalApiClient.getProductDetail(productId);
                    logger.debug("Fetched product detail for productId={}: {}", productId, responseBody);
                    return responseBody;
                });
                futures.add(future);
            } catch (Exception e) {
                logger.error("Error occurred while waiting for requests to complete", e);
            }
        }

        CompletableFuture<Void> allFutures = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));

        try {
            allFutures.get(); // Wait for all the requests to complete
        } catch (Exception e) {
            logger.error("Error occurred while waiting for requests to complete", e);
            // Since this is an external error, we don't want to propagate it further
            // Instead, we can choose to perform alternative actions or provide a fallback response

            // Option 1: Provide a default response or fallback data
            // ...

            // Option 2: Retry the request after a delay
            // ...

            // Option 3: Notify the user about the error without exposing sensitive information
            // ...

            // Option 4: Log the error and continue processing, if applicable
            // ...

            // Add a comment to explain the handling of the external error and provide alternatives
            // We encountered an error while calling the external API. Since this is an external error,
            // we don't want to expose it directly to the client. Instead, we log the error for debugging
            // purposes and choose an appropriate alternative action based on the specific use case.
            // The alternatives mentioned above are some possible options, but the choice depends on the
            // application requirements and the impact of the error on the overall flow.
        }

        List<ProductDetail> productDetails = new ArrayList<>();

        for (CompletableFuture<ProductDetail> future : futures) {
            try {
                ProductDetail productDetail = future.get(); // Get the result of each request
                if (productDetail != null) {
                    productDetails.add(productDetail);
                }
            } catch ( Exception e) {
                logger.error("Error occurred while getting the result of a request", e);
                // Since this is an external error, we don't want to propagate it further
                // Instead, we can choose to perform alternative actions or provide a fallback response

                // Option 1: Provide a default response or fallback data
                // ...

                // Option 2: Retry the request after a delay
                // ...

                // Option 3: Notify the user about the error without exposing sensitive information
                // ...

                // Option 4: Log the error and continue processing, if applicable
                // ...

                // Add a comment to explain the handling of the external error and provide alternatives
                // We encountered an error while calling the external API. Since this is an external error,
                // we don't want to expose it directly to the client. Instead, we log the error for debugging
                // purposes and choose an appropriate alternative action based on the specific use case.
                // The alternatives mentioned above are some possible options, but the choice depends on the
                // application requirements and the impact of the error on the overall flow.
            }
        }

        return productDetails;
    }
}
