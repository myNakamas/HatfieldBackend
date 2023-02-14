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
public class Part extends InventoryItem {
    private String version;
    private Boolean isOriginal;
    private Boolean isNew;
    private Integer wattage;//todo: think of how to display the wattage ( integer overload! )
    private Integer voltage;
    private Integer current;
    private Integer length;
    private String connector;
    private String identifier;
    private String notes;

}
