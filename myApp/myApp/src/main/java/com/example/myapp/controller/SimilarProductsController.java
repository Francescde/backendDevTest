package com.example.myapp.controller;

import com.example.myapp.model.ProductDetail;
import com.example.myapp.service.SimilarProductsService;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/product")
public class SimilarProductsController {
    private static final Logger logger = LoggerFactory.getLogger(SimilarProductsController.class);
    private final SimilarProductsService similarProductsService;

    public SimilarProductsController(SimilarProductsService similarProductsService) {
        this.similarProductsService = similarProductsService;
    }

    @GetMapping("/{productId}/similar")
    public ResponseEntity<List<ProductDetail>> getSimilarProducts(@PathVariable String productId) {
        logger.info("Received request to fetch similar products for productId={}", productId);

        List<ProductDetail> similarProducts = similarProductsService.getSimilarProducts(productId);
        logger.info("Fetched similar products for productId={}: {}", productId, similarProducts);

        return ResponseEntity.ok(similarProducts);
    }
}
