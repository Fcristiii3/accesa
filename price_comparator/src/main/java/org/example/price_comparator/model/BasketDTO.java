package org.example.price_comparator.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BasketDTO{

    private String product;
    private String store;
    float basePrice;
    float discount;

}
