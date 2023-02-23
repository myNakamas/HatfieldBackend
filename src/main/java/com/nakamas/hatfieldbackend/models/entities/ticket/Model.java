package com.nakamas.hatfieldbackend.models.entities.ticket;

import com.nakamas.hatfieldbackend.models.entities.shop.InventoryItem;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.AbstractPersistable;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Table
@Entity
public class Model extends AbstractPersistable<Long> {//    todo: add shopId column to the table
    private String model;
    @OneToMany
    private List<InventoryItem> items;
}
