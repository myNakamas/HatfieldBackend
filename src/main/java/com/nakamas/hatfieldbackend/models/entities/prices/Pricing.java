package com.nakamas.hatfieldbackend.models.entities.prices;

import com.nakamas.hatfieldbackend.models.entities.ticket.Brand;
import com.nakamas.hatfieldbackend.models.entities.ticket.Model;
import com.nakamas.hatfieldbackend.models.views.incoming.PricingView;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "pricings")
@NoArgsConstructor
public class Pricing {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String deviceType;
    private Long brandId;
    private Long modelId;
    private String issue;
    private BigDecimal price;
    private BigDecimal originalPrice;

    public Pricing(PricingView view, Brand brand, Model modelId) {
        this.deviceType = view.deviceType();
        this.brandId = brand.getId();
        this.modelId = modelId.getId();
        this.issue = view.issue();
        this.price = view.price();
        this.originalPrice = view.originalPrice();
    }
}
