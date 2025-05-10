package org.example.price_comparator.repository;

import org.example.price_comparator.model.Product;
import org.example.price_comparator.model.StoreProduct;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StoreProductRepository extends JpaRepository<StoreProduct, String> {
    List<StoreProduct> findAllByProduct(Product product);
}
