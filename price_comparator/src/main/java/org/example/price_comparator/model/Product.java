package org.example.price_comparator.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.price_comparator.constant.MeasuringUnit;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int productId;

    private String name;
    private String category;
    private String brand;
    private MeasuringUnit unit;
    private float packageQuantity;

    @OneToMany(mappedBy = "product")
    private List<StoreProduct> storeOffers;


    public float getPackageQuantityNormalised() {
        if(unit.equals(MeasuringUnit.ML))return packageQuantity/1000f;
        else if(unit.equals(MeasuringUnit.G))return packageQuantity/1000f;
        return packageQuantity;
    }

}
