package com.nakamas.hatfieldbackend.controllers;

import com.nakamas.hatfieldbackend.models.entities.User;
import com.nakamas.hatfieldbackend.models.views.incoming.ChangePasswordView;
import com.nakamas.hatfieldbackend.models.views.incoming.CreateUser;
import com.nakamas.hatfieldbackend.models.views.outgoing.user.UserProfile;
import com.nakamas.hatfieldbackend.services.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Slf4j
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
        User fromDb = userService.getUser(user.getId());
        return userService.getUserProfile(fromDb);
    }

    @PutMapping("profile/edit")
    public UserProfile editLoggedUser(@AuthenticationPrincipal User user, @RequestBody CreateUser update) {
        return new UserProfile(userService.updateUser(user, update));
    }

    @PutMapping("profile/edit/password")
    public void editPassword(@AuthenticationPrincipal User user, @RequestBody ChangePasswordView changePassword) {
        userService.changePassword(user, changePassword.oldPassword(), changePassword.newPassword());
    }
    @GetMapping(path = "profile/image", produces = {MediaType.IMAGE_JPEG_VALUE})
    public void getUserImage(@RequestParam UUID id, @Autowired HttpServletResponse response) {
        userService.getUserImage(id,response);
    }
    @PostMapping(path = "profile/edit/image", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public void updateUserImage(@AuthenticationPrincipal User user, @RequestBody MultipartFile image) {
        userService.updateUserImage(user, image);
    }

}
