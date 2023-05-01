package com.nakamas.hatfieldbackend.services;

import com.nakamas.hatfieldbackend.config.exception.CustomException;
import com.nakamas.hatfieldbackend.models.entities.Photo;
import com.nakamas.hatfieldbackend.repositories.PhotoRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

@Service
@Slf4j
@RequiredArgsConstructor
public class PhotoService {
    private final PhotoRepository photoRepository;

    public void writeToResponse(HttpServletResponse response, Photo photo ){
        try (InputStream image = new ByteArrayInputStream(photo.getData())) {
            image.transferTo(response.getOutputStream());
        } catch (IOException e) {
            log.info(e.getMessage());
            throw new CustomException("Could not load profile image");
        }
    }

    public Photo getById(Long photoId) {
        return photoRepository.findById(photoId).orElseThrow(()-> new CustomException("No such image exists"));
    }
}
