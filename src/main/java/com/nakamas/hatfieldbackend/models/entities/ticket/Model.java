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

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Table
@Entity
public class Model extends AbstractPersistable<Long> {
    private String model;
    @OneToMany(mappedBy = "model")
    private List<InventoryItem> items;
    @OneToMany(mappedBy = "deviceModel")
    private List<Invoice> invoices;
    private Long brandId;

    public Model(String model, Long brandId) {
        this.model = model;
        this.brandId = brandId;
    }

    @Override
    @NonNull
    public String toString() {
        return model;
    }
}
