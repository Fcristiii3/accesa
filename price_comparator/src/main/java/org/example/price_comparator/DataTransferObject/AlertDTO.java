package org.example.price_comparator.DataTransferObject;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.price_comparator.model.Product;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AlertDTO {
    private String email;
    private int product;
    private float targetPrice;
}
