package com.nakamas.hatfieldbackend.models.entities.shop;

import com.nakamas.hatfieldbackend.models.entities.ticket.Brand;
import com.nakamas.hatfieldbackend.models.entities.ticket.Model;
import com.nakamas.hatfieldbackend.models.enums.ItemType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
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
    private Model model;
    private Brand brand;
    private Integer count;
    @Enumerated
    private ItemType type;
    @ElementCollection
    Map<String, String> otherProperties;
}
