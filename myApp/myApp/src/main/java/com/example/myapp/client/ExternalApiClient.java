package com.example.myapp.client;

import com.example.myapp.MyAppApplication;
import com.example.myapp.model.ProductDetail;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Component
public class ExternalApiClient {
    private final RestTemplate restTemplate;
    private static final String BASE_URL = "http://host.docker.internal:3001";
    private static final Logger logger = LoggerFactory.getLogger(ExternalApiClient.class);

    public ExternalApiClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<String> fetchSimilarProductIds(String productId) {
        try {
            String url = BASE_URL + "/product/{productId}/similarids";
            ResponseEntity<String[]> response = restTemplate.getForEntity(url, String[].class, productId);
            logger.debug("Fetched similar product IDs for productId={}: {}", productId,
                    Arrays.toString(response.getBody()));
            return Arrays.asList(response.getBody());
        } catch (Exception e) {
            logger.error("Error occurred while waiting for requests to complete", e);
            return new ArrayList<>();
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

    public List<ProductDetail> fetchProductDetails(List<String> productIds) {
        List<CompletableFuture<ProductDetail>> futures = new ArrayList<>();

        for (String productId : productIds) {
            CompletableFuture<ProductDetail> future = CompletableFuture.supplyAsync(() -> {
                String url = BASE_URL + "/product/{productId}";
                ResponseEntity<ProductDetail> response = restTemplate.getForEntity(url, ProductDetail.class, productId);
                logger.debug("Fetched product detail for productId={}: {}", productId, response.getBody());
                return response.getBody();
            });

            futures.add(future);
        }

        CompletableFuture<Void> allFutures = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));

        try {
            allFutures.get(); // Wait for all the requests to complete
        } catch (InterruptedException | ExecutionException e) {
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
                if(productDetail!=null) {
                    productDetails.add(productDetail);
                }
            } catch (InterruptedException | ExecutionException e) {
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