package com.nakamas.hatfieldbackend.controllers;

import com.nakamas.hatfieldbackend.models.views.incoming.CreateUser;
import com.nakamas.hatfieldbackend.models.views.outgoing.user.UserProfile;
import com.nakamas.hatfieldbackend.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@RequestMapping("api/user")
public class UserController {

    private final UserService userService;

    @PostMapping("create")
    public UserProfile createUser(@RequestBody CreateUser user){
        return new UserProfile(userService.createUser(user));
    }
}
