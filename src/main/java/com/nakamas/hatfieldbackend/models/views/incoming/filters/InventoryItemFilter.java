package com.nakamas.hatfieldbackend.models.views.incoming.filters;

import com.nakamas.hatfieldbackend.models.entities.shop.InventoryItem;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InventoryItemFilter implements Specification<InventoryItem> {
    private String searchBy;
    private Long modelId;
    private Long brandId;
    private Long shopId;
    private Long categoryId;
    private Integer minCount;
    private Boolean isNeeded;
    private Boolean inShoppingList;

    @Override
    public Predicate toPredicate(@NonNull Root<InventoryItem> item, @NonNull CriteriaQuery<?> query, @NonNull CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(criteriaBuilder.conjunction());
        if (modelId != null)
            predicates.add(criteriaBuilder.equal(item.get("model").get("id"), modelId));
        if (brandId != null)
            predicates.add(criteriaBuilder.equal(item.get("brand").get("id"), brandId));
        if (shopId != null)
            predicates.add(criteriaBuilder.equal(item.get("shop").get("id"), shopId));
        if (categoryId != null)
            predicates.add(criteriaBuilder.equal(item.get("categoryId"), categoryId));
        if (isNeeded != null)
            predicates.add(criteriaBuilder.equal(item.get("requiredItem").get("needed"), isNeeded));
        if (minCount != null)
            predicates.add(criteriaBuilder.ge(item.get("count"), minCount));
        if (inShoppingList != null && inShoppingList) {
            predicates.add(criteriaBuilder.gt(item.get("requiredItem").get("requiredAmount"), item.get("count")));
        }
        if (searchBy != null && !searchBy.isBlank()) {
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(item.get("name")), "%" + searchBy.toLowerCase() + "%"));
        }

        query.orderBy(criteriaBuilder.desc(item.get("id")));
        return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
    }
}
