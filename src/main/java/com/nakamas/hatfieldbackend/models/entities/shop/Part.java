package com.nakamas.hatfieldbackend.models.entities.shop;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Table
@Entity
public class Part extends InventoryItem {
    private String version;
    private Boolean isOriginal;
    private Boolean isNew;
    private Boolean isFaulty;
    private Boolean isBrokenDuringWork;
//    private Double wattage;
//    private Double voltage;
//    private Double current;
//    private Double length;
//    private String connector;
//    private String identifier;
    @Column(columnDefinition = "text")
    private String notes;

    @OneToMany(mappedBy = "part")
    private List<UsedPart> usedParts;

}
