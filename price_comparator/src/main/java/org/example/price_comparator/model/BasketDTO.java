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

    private String product; // id
    //private String productName;
    private String store; //store id would be more suitable, but we have only one instance of each market
    float basePrice;
    float discount;

}
