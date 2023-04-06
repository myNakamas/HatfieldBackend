package com.nakamas.hatfieldbackend.models.entities.shop;

import com.nakamas.hatfieldbackend.models.entities.ticket.Brand;
import com.nakamas.hatfieldbackend.models.entities.ticket.Model;
import com.nakamas.hatfieldbackend.models.views.incoming.CreateInventoryItem;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
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

    //private String name;??????
    private Integer count;
    @ManyToOne
    private Shop shop;
    private Boolean shoppingListNeeded;
    @Column(name = "category_id")
    private Long categoryId;
    @ElementCollection
    Map<String, String> otherProperties;

    public String getPropertyValue(String key) {
        return otherProperties.get(key);
    }

    public InventoryItem(CreateInventoryItem item, Brand brand, Model model, Shop shop, Category category) {
        this.model = model;
        this.brand = brand;
        this.shop = shop;
        this.shoppingListNeeded = true;
        this.count = item.count();
        this.categoryId = category.getId();
        this.otherProperties = item.properties();
    }

    @Override
    @NonNull
    public String toString() {
        return "model[%s] brand[%s]".formatted(model.getModel(), brand.getBrand());
    }
}