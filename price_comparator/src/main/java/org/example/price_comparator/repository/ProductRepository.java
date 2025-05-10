package org.example.price_comparator.repository;

import org.example.price_comparator.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, String> {
}
