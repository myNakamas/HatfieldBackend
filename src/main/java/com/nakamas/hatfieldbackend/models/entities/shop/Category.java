package com.nakamas.hatfieldbackend.models.entities.shop;

import com.nakamas.hatfieldbackend.models.enums.ItemType;
import com.nakamas.hatfieldbackend.models.views.outgoing.shop.CategoryColumnView;
import com.nakamas.hatfieldbackend.models.views.outgoing.shop.CategoryView;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.AbstractPersistable;

import java.util.ArrayList;
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

    @OneToMany(cascade = {CascadeType.DETACH, CascadeType.REMOVE, CascadeType.REFRESH})
    @JoinColumn(name = "category_id")
    private List<InventoryItem> items;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "category_id")
    private List<CategoryColumn> fields = new ArrayList<>();

    public Category(CategoryView categoryView) {
        this.name = categoryView.name();
        this.type = categoryView.itemType();
        setFields(categoryView.columns());
    }

    public Category(String categoryName, ItemType type, List<CategoryColumn> columns) {
        this.name = categoryName;
        this.type = type;
        this.fields = columns;
    }

    public void update(CategoryView categoryView) {
        if (categoryView.name() != null) this.name = categoryView.name();
        if (categoryView.itemType() != null) this.type = categoryView.itemType();
        if (categoryView.columns() != null) setFields(categoryView.columns());
    }

    public void setFields(List<CategoryColumnView> columnViews) {
        this.fields.clear();
        this.fields.addAll(columnViews.stream().map(CategoryColumn::new).toList());
    }
}
