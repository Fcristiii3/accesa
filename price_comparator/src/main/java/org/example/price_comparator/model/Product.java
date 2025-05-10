package org.example.price_comparator.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class Product {
    @Id
    private int productId;

    private String name;
    private String category;
    private String brand;
    private String unit;
    private float packageQuantity;

    @OneToMany(mappedBy = "product")
    private List<StoreProduct> storeOffers;
}
