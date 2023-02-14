package com.nakamas.hatfieldbackend.models.entities.shop;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Table
@Entity
public class Device extends InventoryItem {
    private String serialNumber;
    private String condition;
}
