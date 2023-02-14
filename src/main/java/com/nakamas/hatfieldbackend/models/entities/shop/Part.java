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
    private Integer wattage;//todo: think of how to display the wattage ( integer overload!  maybe transfer to double?)
    private Integer voltage;
    private Integer current;
    private Integer length;
    private String connector;
    private String identifier;
    @Column(columnDefinition = "text")
    private String notes;

    @OneToMany
    @JoinColumn(name = "part_id")
    private List<UsedPart> usedParts;

}
