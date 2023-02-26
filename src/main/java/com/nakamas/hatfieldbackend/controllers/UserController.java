package com.nakamas.hatfieldbackend.controllers;

import com.nakamas.hatfieldbackend.models.entities.User;
import com.nakamas.hatfieldbackend.models.views.incoming.CreateUser;
import com.nakamas.hatfieldbackend.models.views.outgoing.user.UserProfile;
import com.nakamas.hatfieldbackend.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("api/user")
public class UserController {

    private final UserService userService;

    @PostMapping("admin/create")
    public UserProfile createUser(@RequestBody @Valid CreateUser user) {
        return new UserProfile(userService.createUser(user));
    }

    @PutMapping("admin/update")
    public UserProfile updateUser(@RequestBody @Valid CreateUser user) {
        return new UserProfile(userService.updateUser(user));
    }

    @PostMapping("create/client")
    public UserProfile createClient(@RequestBody @Valid CreateUser user) {
        return new UserProfile(userService.createClient(user));
    }

    @GetMapping("all/workers")//todo: transform into a filter if needed
    public List<UserProfile> getAllWorkers(String searchBy) {
        return userService.getAllWorkers(searchBy);
    }

    @GetMapping("profile")
    public UserProfile getLoggedUser(@AuthenticationPrincipal User user) {
        return new UserProfile(user);
    }

    @PutMapping("profile/edit")
    public UserProfile editLoggedUser(@AuthenticationPrincipal User user, @RequestBody CreateUser update) {
        return new UserProfile(userService.updateUser(user, update));
    }
}
