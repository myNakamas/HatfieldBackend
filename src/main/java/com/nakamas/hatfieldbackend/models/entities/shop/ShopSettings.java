package com.nakamas.hatfieldbackend.models.entities.shop;

import com.nakamas.hatfieldbackend.models.views.outgoing.shop.ShopSettingsView;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Lob;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class ShopSettings {
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

    public ShopSettings(ShopSettingsView view) {
        this.primaryColor = view.primaryColor();
        this.secondaryLightColor = view.secondaryLightColor();
        this.secondaryColor = view.secondaryColor();
        this.secondaryDarkColor = view.secondaryDarkColor();
        this.textColor = view.textColor();
        this.gmail = view.gmail();
        this.gmailPassword = view.gmailPassword();
        this.smsApiKey = view.smsApiKey();
    }

    public void update(ShopSettingsView view) {
        if (view.primaryColor() != null) this.primaryColor = view.primaryColor();
        if (view.secondaryLightColor() != null) this.secondaryLightColor = view.secondaryLightColor();
        if (view.secondaryColor() != null) this.secondaryColor = view.secondaryColor();
        if (view.secondaryDarkColor() != null) this.secondaryDarkColor = view.secondaryDarkColor();
        if (view.textColor() != null) this.textColor = view.textColor();
        if (view.gmail() != null) this.gmail = view.gmail();
        if (view.gmailPassword() != null) this.gmailPassword = view.gmailPassword();
        if (view.smsApiKey() != null) this.smsApiKey = view.smsApiKey();
    }
}
