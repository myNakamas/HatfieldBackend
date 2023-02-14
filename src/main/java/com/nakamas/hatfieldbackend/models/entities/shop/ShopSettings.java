package com.nakamas.hatfieldbackend.models.entities.shop;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Getter
@Setter
@NoArgsConstructor
@Table
@Entity
public class ShopSettings extends AbstractPersistable<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String primaryColor;
    private String secondaryColor;

    private String gmail;
    private String gmailPassword;
    private String smsApiKey;
    @Lob
    private byte[] logo;

}
