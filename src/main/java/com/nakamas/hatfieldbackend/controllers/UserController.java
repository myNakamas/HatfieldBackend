package com.nakamas.hatfieldbackend.controllers;

import com.nakamas.hatfieldbackend.models.entities.User;
import com.nakamas.hatfieldbackend.models.views.incoming.CreateUser;
import com.nakamas.hatfieldbackend.models.views.outgoing.user.UserProfile;
import com.nakamas.hatfieldbackend.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("api/user")
public class UserController {

    private final UserService userService;

    @PostMapping("create")
    public UserProfile createUser(@RequestBody CreateUser user) {
        return new UserProfile(userService.createUser(user));
    }

    @GetMapping("profile")
    public UserProfile getLoggedUser(@Autowired Authentication authentication) {
        return new UserProfile((User) authentication.getPrincipal());
    }
}
