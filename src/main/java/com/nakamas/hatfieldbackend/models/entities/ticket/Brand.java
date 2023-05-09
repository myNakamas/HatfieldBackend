package com.nakamas.hatfieldbackend.models.entities.ticket;

import com.nakamas.hatfieldbackend.models.entities.shop.InventoryItem;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import org.springframework.data.jpa.domain.AbstractPersistable;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Table
@Entity
public class Brand extends AbstractPersistable<Long> {
    private String brand;
    @OneToMany(mappedBy = "brand")
    private List<InventoryItem> items;
    @OneToMany()
    private List<Model> models = new ArrayList<>();

    public Brand(String brand) {
        this.brand = brand;
    }

    @Override
    @NonNull
    public String toString() {
        return brand;
    }
}
