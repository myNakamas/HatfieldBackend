package com.nakamas.hatfieldbackend.controllers;

import com.nakamas.hatfieldbackend.models.entities.Photo;
import com.nakamas.hatfieldbackend.services.PhotoService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/image")
@RequiredArgsConstructor
public class ResourcesController {
    private final PhotoService photoService;

    @GetMapping(path = "/{id}", produces = {MediaType.IMAGE_JPEG_VALUE})
    public void getImageById(@PathVariable Long id, HttpServletResponse response) {
        Photo byId = photoService.getById(id);
        photoService.writeToResponse(response, byId);
    }

//    @DeleteMapping(path = "/{id}")
//    public void updateUserImage(@AuthenticationPrincipal User user, @RequestBody MultipartFile image) {
//        userService.updateUserImage(user, image);
//    }
}
