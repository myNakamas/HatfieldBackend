package com.nakamas.hatfieldbackend.controllers;

import com.nakamas.hatfieldbackend.models.entities.User;
import com.nakamas.hatfieldbackend.models.views.incoming.ChangePasswordView;
import com.nakamas.hatfieldbackend.models.views.incoming.CreateUser;
import com.nakamas.hatfieldbackend.models.views.incoming.filters.UserFilter;
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
    @PutMapping("admin/updateBan")
    public void updateUserBan(@RequestParam UUID id, @RequestParam Boolean status) {
        userService.updateUserBan(id, status);
    }
    @PutMapping("admin/updateActivity")
    public void updateUserActivity(@RequestParam UUID id, @RequestParam Boolean status) {
        userService.updateUserActivity(id, status);
    }

    @PostMapping("create/client")
    public UserProfile createClient(@RequestBody @Valid CreateUser user) {
        return new UserProfile(userService.createClient(user));
    }
    //worker i nagore toest admin
    @GetMapping("worker/all")
    public List<UserProfile> getAll(UserFilter filter) {
        return userService.getAll(filter).stream().map(UserProfile::new).toList();
    }
    @GetMapping("worker/all/workers")
    public List<UserProfile> getAllWorkers(UserFilter filter) {
        return userService.getAllWorkers(filter).stream().map(UserProfile::new).toList();
    }
    @GetMapping("worker/all/clients")
    public List<UserProfile> getAllClients(UserFilter filter) {
        return userService.getAllClients(filter).stream().map(UserProfile::new).toList();
    }
    //do tuk :D
    @GetMapping("profile")
    public UserProfile getLoggedUser(@AuthenticationPrincipal User user) {
        User fromDb = userService.getUser(user.getId());
        return new UserProfile(fromDb);
    }
    @PutMapping("profile/edit")
    public UserProfile editLoggedUser(@AuthenticationPrincipal User user, @RequestBody CreateUser update) {
        return new UserProfile(userService.updateUser(user, update));
    }

    @PutMapping("profile/editActivity")
    public void editLoggedUserActivity(@AuthenticationPrincipal User user, @RequestParam Boolean status) {
        userService.updateUserActivity(user, status);
    }

    @PutMapping("profile/edit/password")
    public void editPassword(@AuthenticationPrincipal User user, @RequestBody ChangePasswordView changePassword) {
        userService.changePassword(user, changePassword.oldPassword(), changePassword.newPassword());
    }
    @GetMapping(path = "profile/image", produces = {MediaType.IMAGE_JPEG_VALUE})
    public void getUserImage(@RequestParam UUID id, @Autowired HttpServletResponse response) {
        userService.getUserImage(id, response);
    }
    @PostMapping(path = "profile/edit/image", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public void updateUserImage(@AuthenticationPrincipal User user, @RequestBody MultipartFile image) {
        userService.updateUserImage(user, image);
    }
}
