package org.example.price_comparator.service;

import lombok.RequiredArgsConstructor;
import org.example.price_comparator.model.Discount;
import org.example.price_comparator.model.Product;
import org.example.price_comparator.model.StoreProduct;
import org.example.price_comparator.repository.ProductRepository;
import org.example.price_comparator.repository.StoreProductRepository;
import org.example.price_comparator.utils.Notification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final StoreProductRepository storeProductRepository;

    private Product findBestDiscount(Product product) {
        List<Product> products;
        List<StoreProduct> storeProducts = storeProductRepository.findAllByProduct(product);
        float minPrice = Float.MAX_VALUE;
        Product bestDiscount = null;
        for (StoreProduct sp : storeProducts) {
            float price = sp.getPrice();
            Optional<Discount> discount = sp.getCurrentDiscount();
            if(discount.isPresent()) {
            if(price*discount.get().getPercentage_of_discount()<minPrice){
                minPrice = price*discount.get().getPercentage_of_discount();
                bestDiscount=sp.getProduct();
            }
            }
            else if(price<minPrice){
                minPrice = price;
                bestDiscount = sp.getProduct();
            }
        }
        return bestDiscount;
    }

    public Notification<List<Product>> dailyBasketOptimisation(List<Product> products){
        List<Product> optimisedProducts = new ArrayList<>();
        for(Product product : products) {
            optimisedProducts.add(findBestDiscount(product));
        }
        Notification<List<Product>> notification = new Notification<>();
        notification.setResult(optimisedProducts);
        notification.setSuccessMessage("Basket Optimisation");
        return notification;
    }
}
