package com.nakamas.hatfieldbackend.models.entities.shop;

import com.nakamas.hatfieldbackend.models.enums.ItemType;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Getter
@Setter
@NoArgsConstructor
@Table
@Entity
public class Category extends AbstractPersistable<Long> {


    private String name;
    @Enumerated
    private ItemType type;

    //todo: tuk moje da slojim additional poletata
}
