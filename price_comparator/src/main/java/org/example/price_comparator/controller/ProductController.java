package org.example.price_comparator.controller;

import lombok.RequiredArgsConstructor;
import org.example.price_comparator.model.BasketDTO;
import org.example.price_comparator.model.Product;
import org.example.price_comparator.repository.ProductRepository;
import org.example.price_comparator.service.ProductService;
import org.example.price_comparator.utils.Notification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.example.price_comparator.constant.UrlMapping.PRODUCT;

@RequiredArgsConstructor
@RestController
@RequestMapping(PRODUCT)
public class ProductController {

    private final ProductService productService;

    @PostMapping("/bestPrice")
    public ResponseEntity<Notification<List<BasketDTO>>> bestPrice(@RequestBody List<Product> products) {
        Notification<List<BasketDTO>> result = productService.bestDiscounts(products);
        return ResponseEntity.ok(result);
    }



}
