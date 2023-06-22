package com.nakamas.hatfieldbackend.models.entities.shop;

import com.nakamas.hatfieldbackend.config.exception.CustomException;
import com.nakamas.hatfieldbackend.models.entities.ticket.Brand;
import com.nakamas.hatfieldbackend.models.entities.ticket.Model;
import com.nakamas.hatfieldbackend.models.views.incoming.CreateInventoryItem;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import org.springframework.data.jpa.domain.AbstractPersistable;
import org.springframework.lang.Nullable;

import java.math.BigDecimal;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@Table
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class InventoryItem extends AbstractPersistable<Long> {
    private String name;
    @ManyToOne
    private Model model;
    @ManyToOne
    private Brand brand;
    private Integer count;
    private BigDecimal purchasePrice;
    private BigDecimal sellPrice;
    @ManyToOne
    private Shop shop;
    @Column(name = "category_id")
    private Long categoryId;
    @ElementCollection
    private Map<String, String> otherProperties;
    @OneToOne(cascade = CascadeType.ALL)
    private RequiredItem requiredItem;

    public String getPropertyValue(String key) {
        return otherProperties.get(key);
    }

    public InventoryItem(CreateInventoryItem item, Brand brand, Model model, Shop shop, @Nullable Category category) {
        this.name = item.name();
        this.model = model;
        this.brand = brand;
        this.shop = shop;
        this.purchasePrice = item.purchasePrice() != null ? item.purchasePrice() : BigDecimal.ZERO;
        this.sellPrice = item.sellPrice() != null ? item.sellPrice() : BigDecimal.ZERO;
        this.count = item.count();
        this.requiredItem = new RequiredItem(count);
        if (category != null)
            this.categoryId = category.getId();
        this.otherProperties = item.properties();
    }

    @Override
    @NonNull
    public String toString() {
        return "name[%s] model[%s] brand[%s] count[%s]".formatted(name, model.getModel(), brand.getBrand(), count);
    }

    public void update(CreateInventoryItem item, Brand brand, Model model, @Nullable Shop shop, @Nullable Category category) {
        if (item.name() != null) this.name = item.name();
        if (model != null) this.model = model;
        if (brand != null) this.brand = brand;
        if (shop != null) this.shop = shop;
        if (item.purchasePrice() != null) this.purchasePrice = item.purchasePrice();
        else this.purchasePrice = BigDecimal.ZERO;
        if (item.sellPrice() != null) this.sellPrice = item.sellPrice();
        else this.sellPrice = BigDecimal.ZERO;
        if (item.count() != null) this.count = item.count();
        if (category != null) this.categoryId = category.getId();
        if (item.properties() != null) this.otherProperties = item.properties();
    }

    public String getModelString() {
        return model != null ? model.getModel() : "";
    }

    public String getBrandString() {
        return brand != null ? brand.getBrand() : "";
    }

    public int getMissingCount() {
        if (!this.requiredItem.getNeeded()) return 0;
        int neededCount = this.requiredItem.getRequiredAmount() - this.getCount() - this.requiredItem.getDefectiveCount();
        return Math.max(0, neededCount);
    }

    public void addCount(int count) {
        this.count += count;
    }

    public void removeCount(int count) {
        if (this.count - count < 0)
            throw new CustomException("Not enough items to complete action. Current number of items in shop: " + this.count);
        this.count -= count;
    }
}