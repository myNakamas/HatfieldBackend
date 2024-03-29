package com.nakamas.hatfieldbackend.models.views.incoming;

import org.springframework.data.domain.PageRequest;

public record PageRequestView(Integer pageSize, Integer page) {

    public PageRequest getPageRequest() {
        if (page == null || pageSize == null) return PageRequest.of(0, 10);
        return PageRequest.of(page-1, pageSize);
    }
}
