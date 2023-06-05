package com.example.myapp.client;

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

import java.util.Arrays;
import java.util.List;

@Component
@EnableCaching()
@CacheConfig(cacheNames = "similarProductsCache")
public class ExternalApiClient {
    private static final Logger logger = LoggerFactory.getLogger(ExternalApiClient.class);
    private final Environment environment;
    private final RestTemplate restTemplate;

    public ExternalApiClient(RestTemplate restTemplate, Environment environment) {
        this.restTemplate = restTemplate;
        this.environment = environment;
    }

    @Cacheable(key = "#productId", unless = "#result == null")
    public List<String> fetchSimilarProductIds(String productId) {
        String url =  environment.getProperty("externalApi.similarids.url");
        ResponseEntity<String[]> response = restTemplate.getForEntity(url.replace("{productId}",productId),
                String[].class, productId);
        logger.debug("Fetched similar product IDs for productId={}: {}", productId,
                Arrays.toString(response.getBody()!=null?response.getBody():null));
        return response.getBody()!=null?List.of(response.getBody()):null;
    }

    @Cacheable(key = "#productId", unless = "#result == null")
    public ProductDetail getProductDetail(String productId) {
        String url =  environment.getProperty("externalApi.productdetail.url");
        ResponseEntity<ProductDetail> response = restTemplate.getForEntity(url.replace("{productId}",productId),
                ProductDetail.class, productId);
        return response.getBody();
    }
}