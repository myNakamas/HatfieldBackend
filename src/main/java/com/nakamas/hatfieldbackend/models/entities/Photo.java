package com.nakamas.hatfieldbackend.models.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.AbstractPersistable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table()
@Entity
public class Photo extends AbstractPersistable<Long> {
    private String fileName;
    private String path;
    @Transient
    private byte[] data;
    private boolean secure;

    public Photo(byte[] data, boolean secure) {
        this.data = data;
        this.secure = secure;
    }

    public Photo(String fileName, byte[] data, boolean secure) {
        this.fileName = fileName;
        this.data = data;
        this.secure = secure;
    }

    public Path getPath() throws IOException {
        if (path == null || path.isBlank()) throw new IOException("Photo is missing");
        return Path.of(path);
    }

    public void setPath(Path path) {
        this.path = path.toString();
    }
    public byte[] getBytes() throws IOException {
        return Files.readAllBytes(getPath());
    }
}
