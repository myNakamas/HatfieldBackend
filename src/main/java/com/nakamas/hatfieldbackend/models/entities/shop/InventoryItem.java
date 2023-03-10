package com.nakamas.hatfieldbackend.models.entities.shop;

import com.nakamas.hatfieldbackend.models.entities.ticket.Brand;
import com.nakamas.hatfieldbackend.models.entities.ticket.Model;
import com.nakamas.hatfieldbackend.models.views.incoming.CreateInventoryItem;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.AbstractPersistable;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@Table
@Entity
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
public class InventoryItem extends AbstractPersistable<Long> {
    @ManyToOne
    private Model model;
    @ManyToOne
    private Brand brand;
    private Integer count;
    @ManyToOne
    private Shop shop;
    //todo: dali se interesuva toq item da go slaga v shopping list boolean
    @ManyToOne
    private Category category;
    @ElementCollection
    Map<String, String> otherProperties;

    public InventoryItem(CreateInventoryItem item, Brand brand, Model model, Shop shop, Category category) {
        this.model = model;
        this.brand = brand;
        this.shop = shop;
        this.count = item.count();
        this.category = category;
        this.otherProperties = item.properties();
    }
}