package com.nakamas.hatfieldbackend.models.entities.shop;

import com.nakamas.hatfieldbackend.models.views.outgoing.shop.CategoryColumnView;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table
@Entity
public class CategoryColumn extends AbstractPersistable<Long> {
    private String name;
    @Column(columnDefinition = "boolean default false")
    private Boolean showOnDocument = false;
    @Column(columnDefinition = "boolean default false")
    private Boolean showNameOnDocument = false;

    public CategoryColumn(String name) {
        this.name = name;
        this.showOnDocument = false;
        this.showNameOnDocument = false;
    }

    public CategoryColumn(CategoryColumnView categoryColumnView) {
        this.name = categoryColumnView.name();
        this.showOnDocument = categoryColumnView.isShowOnDocument();
        this.showNameOnDocument = categoryColumnView.isShowNameOnDocument();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        CategoryColumn that = (CategoryColumn) o;

        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + name.hashCode();
        return result;
    }
}
