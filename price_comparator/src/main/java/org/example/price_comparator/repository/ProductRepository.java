package org.example.price_comparator.repository;

import org.example.price_comparator.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, String> {
    Product findByName(String name);
    Product findBycsvID(String csvID);
}
