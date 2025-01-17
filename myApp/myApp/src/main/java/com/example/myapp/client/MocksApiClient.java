package com.example.myapp.client;

import com.example.myapp.exception.InternalServerErrorException;
import com.example.myapp.model.ProductDetail;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpStatusCodeException;

import java.util.Arrays;
import java.util.List;

@Component
@EnableCaching()
@CacheConfig
public class MocksApiClient {
    private static final Logger logger = LoggerFactory.getLogger(MocksApiClient.class);
    private final Environment environment;
    private final RestTemplate restTemplate;

    public MocksApiClient(RestTemplate restTemplate, Environment environment) {
        this.restTemplate = restTemplate;
        this.environment = environment;
    }

    @Cacheable(cacheNames = "similarProductIdsCache", key = "#productId", unless = "#result == null")
    public List<String> fetchSimilarProductIds(String productId) {
        String url = environment.getProperty("externalApi.similarids.url");
        try {
            ResponseEntity<String[]> response = restTemplate.getForEntity(
                    url.replace("{productId}", productId),
                    String[].class,
                    productId
            );
            logger.debug("Fetched similar product IDs for productId={}: {}", productId,
                    Arrays.toString(response.getBody() != null ? response.getBody() : null));
            return response.getBody() != null ? List.of(response.getBody()) : null;
        } catch (HttpStatusCodeException e) {
            logger.error("Error occurred while fetching similar product IDs for productId={}", productId, e);
            throw e; // Propagate the exception
        } catch (Exception e) {
            logger.error("Unexpected error occurred while fetching similar product IDs for productId={}", productId, e);
            throw new InternalServerErrorException("Unexpected error occurred"); // Or any appropriate exception
        }
    }

    @Cacheable(cacheNames = "productDetailCache", key = "#productId", unless = "#result == null")
    public ProductDetail getProductDetail(String productId) {
        String url = environment.getProperty("externalApi.productdetail.url");
        try {
            ResponseEntity<ProductDetail> response = restTemplate.getForEntity(
                    url.replace("{productId}", productId),
                    ProductDetail.class,
                    productId
            );
            return response.getBody();
        } catch (HttpStatusCodeException e) {
            logger.error("Error occurred while fetching product detail for productId={}", productId, e);
            throw e; // Propagate the exception
        } catch (Exception e) {
            logger.error("Unexpected error occurred while fetching product detail for productId={}", productId, e);
            throw new InternalServerErrorException("Unexpected error occurred"); // Or any appropriate exception
        }
    }
}