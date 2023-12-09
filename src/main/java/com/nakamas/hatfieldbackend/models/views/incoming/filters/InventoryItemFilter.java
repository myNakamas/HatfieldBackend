package com.nakamas.hatfieldbackend.models.views.incoming.filters;

import com.nakamas.hatfieldbackend.models.entities.shop.InventoryItem;
import jakarta.persistence.criteria.*;
import lombok.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private Boolean onlyNonEmpty;
    private Boolean inShoppingList;

    @Override
    public Predicate toPredicate(@NonNull Root<InventoryItem> item, @NonNull CriteriaQuery<?> query, @NonNull CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();
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
        if (onlyNonEmpty != null && onlyNonEmpty) {
            predicates.add(criteriaBuilder.gt(item.get("count"), 0));
        }
        if (searchBy != null && !searchBy.isBlank()) {
            searchBy = searchBy.trim();
            MapJoin<InventoryItem, String, String> otherProperties = item.joinMap("otherProperties", JoinType.LEFT);
            Predicate searchMatchesProperty = getOtherPropertyEntrySearch(criteriaBuilder, otherProperties);
            predicates.add(criteriaBuilder.or(criteriaBuilder.like(criteriaBuilder.lower(item.get("name")), "%" + searchBy.toLowerCase() + "%"), searchMatchesProperty));
        }
        query.orderBy(criteriaBuilder.desc(item.get("id")));
        return predicates.isEmpty() ? criteriaBuilder.conjunction() : criteriaBuilder.and(predicates.toArray(Predicate[]::new));
    }

    private Predicate getOtherPropertyEntrySearch(CriteriaBuilder criteriaBuilder, MapJoin<InventoryItem, String, String> otherProperties) {
        Map<String, String> searchEntries = extractKeyValues(searchBy);
        if (searchEntries.isEmpty()) return criteriaBuilder.disjunction();
        List<Predicate> parametersMatch = new ArrayList<>();
        for (Map.Entry<String, String> searchEntry : searchEntries.entrySet()) {
            Predicate keyMatches = criteriaBuilder.like(criteriaBuilder.lower(otherProperties.key()), searchEntry.getKey().toLowerCase());
            Predicate valueMatches = criteriaBuilder.like(criteriaBuilder.lower(otherProperties.value()), "%" + searchEntry.getValue().toLowerCase() + "%");
            parametersMatch.add(criteriaBuilder.and(keyMatches, valueMatches));
        }
        return criteriaBuilder.or(parametersMatch.toArray(Predicate[]::new));
    }

    public static Map<String, String> extractKeyValues(String text) {
        Map<String, String> keyValuePairs = new HashMap<>();

        String[] pairs = text.split(" ");
        for (String pair : pairs) {
            String[] keyValue = pair.split(":");
            if (keyValue.length == 2) {
                String key = keyValue[0];
                String value = keyValue[1];
                keyValuePairs.put(key, value);
            }
        }

        return keyValuePairs;
    }
}
