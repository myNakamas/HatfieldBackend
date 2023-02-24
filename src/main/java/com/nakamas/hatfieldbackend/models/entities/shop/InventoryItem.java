package com.nakamas.hatfieldbackend.models.entities.shop;

import com.nakamas.hatfieldbackend.models.entities.ticket.Brand;
import com.nakamas.hatfieldbackend.models.entities.ticket.Model;
import com.nakamas.hatfieldbackend.models.enums.ItemType;
import com.nakamas.hatfieldbackend.models.views.incoming.CreateInventoryItem;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Getter
@Setter
@NoArgsConstructor
@Table
@Entity
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
public class InventoryItem extends AbstractPersistable<Long> {
    @ManyToOne(cascade = CascadeType.ALL)
    private Model model;
    @ManyToOne(cascade = CascadeType.ALL)
    private Brand brand;
    private Integer count;
    @ManyToOne
    private Shop shop;
    @Enumerated
    private ItemType type;
//    @ElementCollection
//    Map<String, String> otherProperties;

    public InventoryItem(CreateInventoryItem item, Brand brand, Model model, Shop shop){
        this.model = model;
        this.brand = brand;
        this.shop = shop;
        this.count = item.count();
        this.type = item.type();
    }
}