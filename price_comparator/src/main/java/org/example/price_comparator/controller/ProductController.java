package org.example.price_comparator.controller;

import lombok.RequiredArgsConstructor;
import org.example.price_comparator.DataTransferObject.AlertDTO;
import org.example.price_comparator.DataTransferObject.BasketDTO;
import org.example.price_comparator.DataTransferObject.StoreProductDTO;
import org.example.price_comparator.service.ProductService;
import org.example.price_comparator.utils.Notification;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<Notification<List<BasketDTO>>> bestPrice(@RequestBody List<String> productIDs) {
        Notification<List<BasketDTO>> result = productService.bestDiscounts(productIDs);
        if(result.hasErrors() && result.getResult()==null)return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
        else if(result.hasErrors())return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        return ResponseEntity.ok(result);
    }
    @PostMapping("/optimiseBasket")
    public ResponseEntity<Notification<Map<String,List<BasketDTO>>>> optimiseBasket(@RequestBody List<String> productIDs) {
        Notification<Map<String, List<BasketDTO>>> result = productService.optimiseBasket(productIDs);
        if(result.hasErrors() && result.getResult()==null)return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
        else if(result.hasErrors())return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        return ResponseEntity.ok(result);
    }
    @GetMapping("/newDiscounts")
    public ResponseEntity<Notification<List<BasketDTO>>> getNewDiscounts() {
        Notification<List<BasketDTO>> result = productService.newDiscounts();
        if(result.hasErrors() && result.getResult()==null)return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
        else if(result.hasErrors())return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        return ResponseEntity.ok(result);
    }
    @GetMapping("/getPriceHistory")
    public ResponseEntity<Notification<StoreProductDTO>> getPriceHistory(@RequestParam String productId, @RequestParam String shopId) {
        Notification<StoreProductDTO> result = productService.dynamicHistory(productId,shopId);
        if(result.hasErrors() && result.getResult()==null)return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
        else if(result.hasErrors())return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        return ResponseEntity.ok(result);
    }
    //create op for alerts. the checking is made automatically
    @PostMapping("/setAlert")
    public ResponseEntity<Notification<AlertDTO>> setAlert(
            @RequestParam String email,
            @RequestParam String productId,
            @RequestParam float targetPrice
    ) {
        Notification<AlertDTO> result = productService.setAlert(email, productId, targetPrice);
        if(result.hasErrors() && result.getResult()==null)return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
        else if(result.hasErrors())return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/findSubstitutes")
    public ResponseEntity<Notification<List<StoreProductDTO>>> findSubstitutes(@RequestParam String productID){
        Notification<List<StoreProductDTO>> result = productService.findSubstitutes(productID);
        if(result.hasErrors() && result.getResult()==null)return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
        else if(result.hasErrors())return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        return ResponseEntity.ok(result);
    }

}
