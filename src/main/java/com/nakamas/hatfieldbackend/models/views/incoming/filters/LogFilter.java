package com.nakamas.hatfieldbackend.models.views.incoming.filters;

import com.nakamas.hatfieldbackend.models.entities.Log;
import com.nakamas.hatfieldbackend.models.enums.LogType;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.*;
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
public class LogFilter implements Specification<Log> {
    private Long shopId;
    private UUID userId;
    private Long ticketId;
    private LocalDate from;
    private LocalDate to;
    private LogType type;

    @Override
    public Predicate toPredicate(@NonNull Root<Log> log, @NonNull CriteriaQuery<?> query, @NonNull CriteriaBuilder builder) {
        List<Predicate> predicates = new ArrayList<>();
        if (shopId != null)
            predicates.add(builder.equal(log.get("shopId"), shopId));
        if (userId != null)
            predicates.add(builder.equal(log.get("userId"), userId));
        if (ticketId != null)
            predicates.add(builder.equal(log.get("ticketId"), ticketId));
        if (type != null)
            predicates.add(builder.equal(log.get("logType"), type));
        if (to != null)
            predicates.add(builder.lessThanOrEqualTo(log.get("timestamp"), to.plusDays(1L).atStartOfDay().atZone(ZoneId.systemDefault())));
        if (from != null)
            predicates.add(builder.greaterThanOrEqualTo(log.get("timestamp"), from.atStartOfDay().atZone(ZoneId.systemDefault())));

        query.orderBy(builder.desc(log.get("timestamp")));
        return builder.and(predicates.toArray(Predicate[]::new));
    }
}
