package org.example.price_comparator.DataTransferObject;

import lombok.*;
import org.example.price_comparator.model.Discount;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class StoreProductDTO {
    private String productID;
    private float price;
    private String currency;
    private List<DiscountDTO> discounts;
    private String storeName;
}
