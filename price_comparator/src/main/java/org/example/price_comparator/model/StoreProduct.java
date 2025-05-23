package org.example.price_comparator.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class StoreProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private float price;
    //each store can have their own price -> the best price of a product will be price*discount
    private String currency;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "store_id")
    private Store store;

    @OneToMany(mappedBy = "storeProduct")
    private List<Discount> discounts;


    public Optional<Discount> getCurrentDiscount() {
        Date now = new Date();
        for (Discount discount : discounts) {
            if(now.before(discount.getTo_date()) && now.after(discount.getFrom_date())){
                return Optional.of(discount);
            }
        }
        return Optional.empty();
    }
    public boolean hasActiveDiscount() {
        if(getCurrentDiscount().isPresent())return true;
        return false;
    }

    public float getPrice(){
        Optional<Discount> discount = getCurrentDiscount();
        if(discount.isPresent()) {
            return price - price * discount.get().getPercentage_of_discount()/100f;
        }
        else return price;
    }

    public float getPricePerKGorL(){
        return getPrice()/product.getPackageQuantityNormalised();
    }


}

