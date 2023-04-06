package com.nakamas.hatfieldbackend.controllers;

import com.nakamas.hatfieldbackend.models.views.incoming.PageRequestView;
import com.nakamas.hatfieldbackend.models.views.incoming.filters.LogFilter;
import com.nakamas.hatfieldbackend.models.views.outgoing.LogView;
import com.nakamas.hatfieldbackend.models.views.outgoing.PageView;
import com.nakamas.hatfieldbackend.services.LoggerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/logs")
@RequiredArgsConstructor
public class LogController {
    private final LoggerService loggerService;

    @GetMapping("all")
    public PageView<LogView> allLogs(LogFilter filter, PageRequestView pageRequestView){
        return loggerService.getLogs(filter,pageRequestView.getPageRequest());
    }
}
