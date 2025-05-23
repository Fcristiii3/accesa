package org.example.price_comparator.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class Discount {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int discount_id;
    private Date from_date;
    private Date to_date;
    private float percentage_of_discount;

    @ManyToOne
    @JoinColumn(name = "store_product_id")
    private StoreProduct storeProduct;


}
