package com.nakamas.hatfieldbackend.models.entities.shop;

import com.nakamas.hatfieldbackend.models.enums.ItemType;
import com.nakamas.hatfieldbackend.models.views.outgoing.shop.CategoryView;
import jakarta.persistence.*;
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

    @OneToMany(mappedBy = "category",cascade = {CascadeType.DETACH})
    private List<InventoryItem> items;

    @ElementCollection
    private List<String> fields;

    public Category(CategoryView categoryView) {
        this.name = categoryView.name();
        this.type = categoryView.itemType();
        this.fields = categoryView.columns();
    }

    public Category(String categoryName, ItemType type,List<String> columns) {
        this.name = categoryName;
        this.type = type;
        this.fields = columns;
    }

    public void update(CategoryView categoryView) {
        if (categoryView.name() != null) this.name = categoryView.name();
        if (categoryView.itemType() != null) this.type = categoryView.itemType();
        if (categoryView.columns() != null) this.fields = categoryView.columns();
    }
}
