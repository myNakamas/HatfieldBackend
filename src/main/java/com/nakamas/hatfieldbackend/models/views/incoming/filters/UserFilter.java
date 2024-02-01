package com.nakamas.hatfieldbackend.models.views.incoming.filters;

import com.nakamas.hatfieldbackend.models.entities.User;
import com.nakamas.hatfieldbackend.models.enums.UserRole;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserFilter implements Specification<User> {
    private Long shopId;
    private Boolean active;
    private Boolean banned;
    private List<UserRole> roles;

    @Override
    public Predicate toPredicate(@NonNull Root<User> user, @NonNull CriteriaQuery<?> query, @NonNull CriteriaBuilder builder) {
        List<Predicate> predicates = new ArrayList<>();
        if (shopId != null)
            predicates.add(builder.equal(user.<Long>get("shop").get("id"), shopId));
        if (active != null)
            predicates.add(builder.equal(user.<Boolean>get("isActive"), active));
        predicates.add(builder.equal(user.<Boolean>get("isBanned"), Objects.requireNonNullElse(banned, false)));
        if (roles != null && !roles.isEmpty())
            predicates.add(user.<UserRole>get("role").in(roles));
        query.orderBy(builder.desc(user.get("id")));
        return builder.and(predicates.toArray(Predicate[]::new));
    }
}
