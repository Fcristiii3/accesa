package org.example.price_comparator.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Setter
@Getter
public class Alert {
    @Id
    private Long id;
    private String email;
    @ManyToOne
    private Product product;
    private float targetPrice;
}
