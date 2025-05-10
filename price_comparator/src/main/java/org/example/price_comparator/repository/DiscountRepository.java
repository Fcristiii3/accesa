package org.example.price_comparator.repository;

import org.example.price_comparator.model.Discount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiscountRepository extends JpaRepository<Discount, String> {
}
