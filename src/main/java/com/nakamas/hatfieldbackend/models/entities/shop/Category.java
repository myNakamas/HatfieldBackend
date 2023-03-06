package com.nakamas.hatfieldbackend.models.entities.shop;

import com.nakamas.hatfieldbackend.models.enums.ItemType;
import com.nakamas.hatfieldbackend.models.views.outgoing.shop.CategoryView;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.AbstractPersistable;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table
@Entity
public class Category extends AbstractPersistable<Long> {
    private String name;
    @Enumerated
    private ItemType type;
    @ElementCollection
    private List<String> fields;

    public Category(CategoryView categoryView) {
        this.name = categoryView.name();
        this.type = categoryView.itemType();
        this.fields = categoryView.columns();
    }

    public void update(CategoryView categoryView) {
        if (categoryView.name() != null) this.name = categoryView.name();
        if (categoryView.itemType() != null) this.type = categoryView.itemType();
        if (categoryView.columns() != null) this.fields = categoryView.columns();
    }
}
