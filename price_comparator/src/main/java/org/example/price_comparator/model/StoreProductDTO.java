package org.example.price_comparator.model;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class StoreProductDTO {
    private String productName;
    private float price;
    private String currency;
    private List<Discount> discounts;
    private String storeName;
}
