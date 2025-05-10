package org.example.price_comparator.repository;

import org.example.price_comparator.model.Store;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreRepository extends JpaRepository<Store, String> {
}