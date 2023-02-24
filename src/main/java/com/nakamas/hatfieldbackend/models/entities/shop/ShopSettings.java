package com.nakamas.hatfieldbackend.models.entities.shop;

import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table
@Entity
public class ShopSettings extends AbstractPersistable<Long> {
    private String primaryColor;
    private String secondaryLightColor;
    private String secondaryColor;
    private String secondaryDarkColor;
    private String textColor;

    private String gmail;
    private String gmailPassword;
    private String smsApiKey;
    @Lob
    private byte[] logo;
    @Lob
    private byte[] backgroundImage;

}
