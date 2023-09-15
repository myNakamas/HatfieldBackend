package com.nakamas.hatfieldbackend.models.views.outgoing;

import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
public class PageView<T> {
    private final int page;
    private final int pageSize;
    private final long totalCount;
    private final int pageCount;
    private final List<T> content;

    public PageView(Page<T> page) {
        this.page = page.getNumber() + 1;
        this.pageSize = page.getSize();
        this.totalCount = page.getTotalElements();
        this.pageCount = page.getTotalPages();
        this.content = page.getContent();
    }
}
