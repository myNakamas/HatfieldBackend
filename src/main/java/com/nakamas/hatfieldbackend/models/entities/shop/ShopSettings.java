package com.nakamas.hatfieldbackend.models.entities.shop;

import com.nakamas.hatfieldbackend.models.views.outgoing.shop.ShopSettingsView;
import jakarta.persistence.Column;
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
    private String secondaryColor;

    @Column(columnDefinition = "boolean default false")
    private boolean emailEnabled;
    private String gmail;
    private String gmailPassword;

    @Column(columnDefinition = "boolean default false")
    private boolean printEnabled;
    private String printerIp;
    private String printerModel;

    @Column(columnDefinition = "boolean default false")
    private boolean smsEnabled;
    private String smsApiKey;

    @Lob
    private byte[] logo;
    @Lob
    private byte[] backgroundImage;

    public ShopSettings(ShopSettingsView view) {
        if (view.primaryColor() != null) this.primaryColor = view.primaryColor();
        if (view.secondaryColor() != null) this.secondaryColor = view.secondaryColor();
        if (view.gmail() != null) this.gmail = view.gmail();
        if (view.gmailPassword() != null) this.gmailPassword = view.gmailPassword();
        if (view.printerIp() != null) this.printerIp = view.printerIp();
        if (view.printerModel() != null) this.printerModel = view.printerModel();
        if (view.smsApiKey() != null) this.smsApiKey = view.smsApiKey();
    }

    public void update(ShopSettingsView view) {
        if (view.primaryColor() != null) this.primaryColor = view.primaryColor();
        if (view.secondaryColor() != null) this.secondaryColor = view.secondaryColor();
        if (view.emailNotificationsEnabled() != null) this.emailEnabled = view.emailNotificationsEnabled();
        if (view.gmail() != null) this.gmail = view.gmail();
        if (view.gmailPassword() != null) this.gmailPassword = view.gmailPassword();
        if (view.printEnabled() != null) this.printEnabled = view.printEnabled();
        if (view.printerIp() != null) this.printerIp = view.printerIp();
        if (view.printerModel() != null) this.printerModel = view.printerModel();
        if (view.smsNotificationsEnabled() != null) this.smsEnabled = view.smsNotificationsEnabled();
        if (view.smsApiKey() != null) this.smsApiKey = view.smsApiKey();
    }
}
