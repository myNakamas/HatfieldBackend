package com.nakamas.hatfieldbackend.models.views.incoming;

import org.springframework.data.domain.Sort;

public record SortRequest(String id,Boolean desc) {
    public Sort getSort() {
        return desc ? Sort.by(id).descending() : Sort.by(id).ascending();
    }
}
