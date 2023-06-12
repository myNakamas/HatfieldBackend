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
    private String secondaryColor;
    private String gmail;
    private String gmailPassword;
    private boolean print;
    private String printerIp;
    private String printerModel;
    private String smsApiKey;
    @Lob
    private byte[] logo;
    @Lob
    private byte[] backgroundImage;

    public ShopSettings(ShopSettingsView view) {
        this.primaryColor = view.primaryColor();
        this.secondaryColor = view.secondaryColor();
        this.gmail = view.gmail();
        this.gmailPassword = view.gmailPassword();
        this.print = view.print();
        this.printerIp = view.printerIp();
        this.printerModel = view.printerModel();
        this.smsApiKey = view.smsApiKey();
    }

    public void update(ShopSettingsView view) {
        if (view.primaryColor() != null) this.primaryColor = view.primaryColor();
        if (view.secondaryColor() != null) this.secondaryColor = view.secondaryColor();
        if (view.gmail() != null) this.gmail = view.gmail();
        if (view.gmailPassword() != null) this.gmailPassword = view.gmailPassword();
        this.print = view.print();
        if (view.printerIp() != null) this.printerIp = view.printerIp();
        if (view.printerModel() != null) this.printerModel = view.printerModel();
        if (view.smsApiKey() != null) this.smsApiKey = view.smsApiKey();
    }
}
