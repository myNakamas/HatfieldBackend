package com.nakamas.hatfieldbackend.controllers;

import com.nakamas.hatfieldbackend.config.exception.CustomException;
import com.nakamas.hatfieldbackend.models.views.outgoing.ResponseMessage;
import com.nakamas.hatfieldbackend.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api/public")
public class PublicController {
    private final UserService userService;
    private final Map<String, Integer> requestMap = new HashMap<>();

    @PostMapping("forgot-password")
    public ResponseMessage editPassword(@RequestParam String username, HttpServletRequest request) {
        limitUserRequestsByIp(request);
        return userService.forgotPassword(username);
    }

    private void limitUserRequestsByIp(HttpServletRequest request) {
        int MAX_REQUESTS_PER_HOUR = 2;
        int requestCount = requestMap.computeIfAbsent(request.getRemoteAddr(), (key) -> 0);
        if (requestCount >= MAX_REQUESTS_PER_HOUR) {
            throw new CustomException("You have exceeded the maximum number of requests per hour.");
        }
        requestMap.put(request.getRemoteAddr(), requestCount + 1);
    }
}
