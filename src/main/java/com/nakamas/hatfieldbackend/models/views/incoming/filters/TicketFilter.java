package com.nakamas.hatfieldbackend.models.views.incoming.filters;

import com.nakamas.hatfieldbackend.config.exception.CustomException;
import com.nakamas.hatfieldbackend.models.entities.ticket.Ticket;
import com.nakamas.hatfieldbackend.models.enums.TicketStatus;
import jakarta.persistence.criteria.*;
import lombok.*;
import org.hibernate.query.SemanticException;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TicketFilter implements Specification<Ticket> {
    private String searchBy;
    private Long modelId;
    private Long brandId;
    private Long deviceLocation;
    private Long shopId;
    private UUID clientId;
    private UUID createdById;
    private LocalDate createdBefore;
    private LocalDate createdAfter;
    private LocalDate deadlineBefore;
    private LocalDate deadlineAfter;
    private List<TicketStatus> ticketStatuses;

    private String sortField;
    private Sort.Direction sortDirection;

    @Override
    public Predicate toPredicate(@NonNull Root<Ticket> ticket, @NonNull CriteriaQuery<?> query, @NonNull CriteriaBuilder builder) {
        List<Predicate> predicates = new ArrayList<>();
        if (modelId != null)
            predicates.add(builder.equal(ticket.get("deviceModel").get("id"), modelId));
        if (brandId != null)
            predicates.add(builder.equal(ticket.get("deviceBrand").get("id"), brandId));
        if (deviceLocation != null)
            predicates.add(builder.equal(ticket.get("deviceLocation").get("id"), deviceLocation));
        if (shopId != null)
            predicates.add(builder.equal(ticket.get("shop").get("id"), shopId));
        if (clientId != null)
            predicates.add(builder.equal(ticket.get("client").get("id"), clientId));
        if (createdById != null)
            predicates.add(builder.equal(ticket.get("createdBy").get("id"), createdById));
        if (createdBefore != null)
            predicates.add(builder.lessThanOrEqualTo(ticket.get("timestamp"), createdBefore.plusDays(1L).atStartOfDay().atZone(ZoneId.systemDefault())));
        if (createdAfter != null)
            predicates.add(builder.greaterThanOrEqualTo(ticket.get("timestamp"), createdAfter.atStartOfDay().atZone(ZoneId.systemDefault())));
        if (deadlineBefore != null)
            predicates.add(builder.lessThanOrEqualTo(ticket.get("deadline"), deadlineBefore.plusDays(1L).atStartOfDay().atZone(ZoneId.systemDefault())));
        if (deadlineAfter != null)
            predicates.add(builder.greaterThanOrEqualTo(ticket.get("deadline"), deadlineAfter.atStartOfDay().atZone(ZoneId.systemDefault())));
        if (ticketStatuses != null && !ticketStatuses.isEmpty())
            predicates.add((ticket.get("status").in(ticketStatuses)));
        if (searchBy != null && !searchBy.isBlank()) {
            Expression<String> concat = builder.concat(ticket.get("serialNumberOrImei"), ticket.get("deviceProblemExplanation"));
            predicates.add(builder.like(builder.lower(concat), "%" + searchBy.toLowerCase() + "%"));
        }
        try {
            Order sort = (sortField != null && !sortField.isBlank() && sortDirection != null) ?
                    getOrder(ticket.get(sortField), builder) : builder.desc(ticket.get("id"));
            query.orderBy(sort);
            return builder.and(predicates.toArray(Predicate[]::new));
        } catch (SemanticException e) {
            throw new CustomException("Could not find field " + sortField + " in Ticket");
        }

    }

    private Order getOrder(Path<Ticket> path, CriteriaBuilder builder) {
        return sortDirection.isAscending() ? builder.asc(path) : builder.desc(path);
    }
}
