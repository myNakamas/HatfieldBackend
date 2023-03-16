package com.nakamas.hatfieldbackend.controllers;

import com.nakamas.hatfieldbackend.services.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/pdf")
public class DocumentController {
    private final DocumentService documentService;

    @PostMapping("generate")
    private void resetPassword() {
        documentService.fillDocument();
    }
}
