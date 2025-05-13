package org.example.price_comparator.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class StoreProductDTO {
    private String productName;
    private int price;
    private String currency;
    private List<Discount> discounts;
}
