package com.nakamas.hatfieldbackend.models.views.incoming.filters;

import com.nakamas.hatfieldbackend.models.entities.ticket.Invoice;
import com.nakamas.hatfieldbackend.models.enums.InvoiceType;
import jakarta.persistence.criteria.*;
import lombok.*;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceFilter implements Specification<Invoice> {
    private String searchBy;
    private String model;
    private String brand;
    private Long shopId;
    private UUID clientId;
    private UUID createdById;
    private LocalDate createdBefore;
    private LocalDate createdAfter;
    private InvoiceType type;
    private Boolean valid;

    @Override
    public Predicate toPredicate(@NonNull Root<Invoice> invoice, @NonNull CriteriaQuery<?> query, @NonNull CriteriaBuilder builder) {
        List<Predicate> predicates = new ArrayList<>();
        if (model != null && !model.isBlank())
            predicates.add(builder.like(invoice.get("deviceModel"), model));
        predicates.add(builder.equal(invoice.get("valid"), Objects.requireNonNullElse(valid, true)));
        if (brand != null && !brand.isBlank())
            predicates.add(builder.like(invoice.get("deviceBrand"), brand));
        if (createdById != null)
            predicates.add(builder.equal(invoice.get("createdBy").get("id"), createdById));
        if (shopId != null)
            predicates.add(builder.equal(invoice.get("createdBy").get("shop").get("id"), shopId));
        if (clientId != null)
            predicates.add(builder.equal(invoice.get("client").get("id"), clientId));
        if (createdBefore != null)
            predicates.add(builder.lessThanOrEqualTo(invoice.get("timestamp"), createdBefore.plusDays(1L).atStartOfDay().atZone(ZoneId.systemDefault())));
        if (createdAfter != null)
            predicates.add(builder.greaterThanOrEqualTo(invoice.get("timestamp"), createdAfter.atStartOfDay().atZone(ZoneId.systemDefault())));
        if (type != null)
            predicates.add(builder.equal(invoice.get("type"), type));
        if (searchBy != null && !searchBy.isBlank()) {
            Expression<String> concat = builder.lower(builder.concat(invoice.get("serialNumber"), invoice.get("notes")));
            predicates.add(builder.like(concat, "%" + searchBy.toLowerCase() + "%"));
        }

        query.orderBy(builder.desc(invoice.get("id")));
        return builder.and(predicates.toArray(Predicate[]::new));
    }
}
