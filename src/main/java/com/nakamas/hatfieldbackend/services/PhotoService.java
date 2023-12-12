package com.nakamas.hatfieldbackend.services;

import com.nakamas.hatfieldbackend.config.exception.CustomException;
import com.nakamas.hatfieldbackend.models.entities.Photo;
import com.nakamas.hatfieldbackend.repositories.PhotoRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@Slf4j
@RequiredArgsConstructor
public class PhotoService {
    private final PhotoRepository photoRepository;
    @Value(value = "${output-dir}")
    private String outputDir;

    public String getChatImagesPath() {
        return Path.of(outputDir, "images", "chats").toString();
    }

    public String getPfpImagesPath() {
        return Path.of(outputDir, "images", "pfp").toString();
    }

    public void writeToResponse(HttpServletResponse response, Photo photo) {
        try {
            byte[] bytes = photo.getBytes();
            if (bytes == null) throw new CustomException("Image is missing");
            InputStream image = new ByteArrayInputStream(bytes);
            image.transferTo(response.getOutputStream());
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new CustomException("Could not load profile image");
        }
    }

    public Photo getById(Long photoId) {
        return photoRepository.findById(photoId).orElseThrow(() -> new CustomException("No such image exists"));
    }

    public Photo saveProfileImage(String username, MultipartFile file) {
        return savePhoto(Paths.get(getPfpImagesPath(), username), file, file.getOriginalFilename());
    }

    public Photo saveChatImage(String fullName, Long ticketId, MultipartFile file) {
        String ticketName = "ticket_%s".formatted(ticketId);
        String fileName = "%s_%s".formatted(fullName, file.getOriginalFilename());

        return savePhoto(Paths.get(getChatImagesPath(), ticketName), file, fileName);
    }

    private Photo savePhoto(Path path, MultipartFile file, String fileName) {
        Photo save;
        try {
            Photo newPhoto = new Photo(file.getOriginalFilename(), file.getBytes(), false);
            Path outputPath = saveToDirectory(file, path, fileName);
            newPhoto.setPath(outputPath);
            save = photoRepository.save(newPhoto);

        } catch (IOException e) {
            log.error(e.getMessage());
            throw new CustomException("Failed to save image");
        }
        return save;
    }

    private static Path saveToDirectory(MultipartFile file, Path categorizedDirectory, String fileName) throws IOException {
        if (!Files.exists(categorizedDirectory)) {
            Files.createDirectories(categorizedDirectory);
        }
        Path imagePath = Paths.get(categorizedDirectory.toString(), fileName);

        // Save the image to the file system
        file.transferTo(imagePath.toFile());
        return imagePath.toAbsolutePath();
    }
}
