package com.nakamas.hatfieldbackend.models.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Table()
@Entity
public class Photo {
    @Id
    @GeneratedValue
    private Long id;
    @Lob
    private byte[] data;
    private boolean secure;

    public Photo(byte[] data, boolean secure) {
        this.data = data;
        this.secure = secure;
    }
}
