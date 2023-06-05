package com.example.myapp.client;

import com.example.myapp.model.ProductDetail;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
@EnableCaching
public class ExternalApiClient {
    private final RestTemplate restTemplate;
    private static final String BASE_URL = "http://host.docker.internal:3001";
    private static final Logger logger = LoggerFactory.getLogger(ExternalApiClient.class);

    public ExternalApiClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    @Cacheable(key = "#productId", unless = "#result == null")
    public List<String> fetchSimilarProductIds(String productId) {
        String url = BASE_URL + "/product/{productId}/similarids";
        ResponseEntity<String[]> response = restTemplate.getForEntity(url, String[].class, productId);
        logger.debug("Fetched similar product IDs for productId={}: {}", productId,
                Arrays.toString(response.getBody()));
        return Arrays.asList(response.getBody());
    }
    @Cacheable(key = "#productId", unless = "#result == null")
    public CompletableFuture<ProductDetail> getFutureProductDetail(String productId) {
        return CompletableFuture.supplyAsync(() -> {
            ResponseEntity<ProductDetail> response = getProductDetail(productId);
            logger.debug("Fetched product detail for productId={}: {}", productId, response.getBody());
            return response.getBody();
        });
    }

    private ResponseEntity<ProductDetail> getProductDetail(String productId) {
        String url = BASE_URL + "/product/{productId}";
        ResponseEntity<ProductDetail> response = restTemplate.getForEntity(url, ProductDetail.class, productId);
        return response;
    }
}