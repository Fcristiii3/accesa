package org.example.price_comparator.controller;

import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.example.price_comparator.model.AlertDTO;
import org.example.price_comparator.model.BasketDTO;
import org.example.price_comparator.model.Product;
import org.example.price_comparator.model.StoreProductDTO;
import org.example.price_comparator.repository.ProductRepository;
import org.example.price_comparator.service.ProductService;
import org.example.price_comparator.utils.Notification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static org.example.price_comparator.constant.UrlMapping.PRODUCT;

@RequiredArgsConstructor
@RestController
@RequestMapping(PRODUCT)
public class ProductController {

    private final ProductService productService;

    @PostMapping("/bestPrice")
    public ResponseEntity<Notification<List<BasketDTO>>> bestPrice(@RequestBody List<String> productNames) {
        Notification<List<BasketDTO>> result = productService.bestDiscounts(productNames);
        return ResponseEntity.ok(result);
    }
    @PostMapping("/optimiseBasket")
    public ResponseEntity<Notification<Map<String,List<BasketDTO>>>> optimiseBasket(@RequestBody List<Product> products) {
        Notification<Map<String, List<BasketDTO>>> result = productService.optimiseBasket(products);
        return ResponseEntity.ok(result);
    }
    @GetMapping("/newDiscounts")
    public ResponseEntity<Notification<List<BasketDTO>>> getNewDiscounts() {
        Notification<List<BasketDTO>> result = productService.newDiscounts();
        return ResponseEntity.ok(result);
    }
    @GetMapping("/getPriceHistory")
    public ResponseEntity<Notification<StoreProductDTO>> getPriceHistory(@RequestParam int productId) {
        Notification<StoreProductDTO> result = productService.dynamicHistory(productId);
        return ResponseEntity.ok(result);
    }
    //create op for alerts. the checking is made automatically
    @PostMapping("/setAlert")
    public ResponseEntity<Notification<AlertDTO>> setAlert(
            @RequestParam String email,
            @RequestParam int productId,
            @RequestParam float targetPrice
    ) {
        Product product = new Product();
        product.setProductId(String.valueOf(productId));
        Notification<AlertDTO> result = productService.setAlert(email, product, targetPrice);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/findSubstitutes")
    public ResponseEntity<Notification<List<StoreProductDTO>>> findSubstitutes(@RequestParam int productID){
        Notification<List<StoreProductDTO>> result = productService.findSubstitutes(productID);
        return ResponseEntity.ok(result);
    }

}
