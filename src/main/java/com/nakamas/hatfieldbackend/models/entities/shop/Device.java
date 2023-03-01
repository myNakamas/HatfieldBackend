package com.nakamas.hatfieldbackend.models.entities.shop;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Device extends InventoryItem {
    private String serialNumber;
    private String condition;
}
