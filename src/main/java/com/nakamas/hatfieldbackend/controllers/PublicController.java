package com.nakamas.hatfieldbackend.controllers;

import com.nakamas.hatfieldbackend.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/public")
public class PublicController {
    private final UserService userService;

    @PutMapping("reset-password")
    private void resetPassword(@RequestParam UUID oneTimeToken, String password) {
        userService.resetPassword(oneTimeToken, password);
    }
}
