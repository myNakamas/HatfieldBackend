package com.nakamas.hatfieldbackend.models.views.outgoing.shop;

import com.nakamas.hatfieldbackend.models.entities.shop.ShopSettings;

public record ShopSettingsView(
        Long id,
        String primaryColor,
        String secondaryColor,
        String gmail,
        String gmailPassword,
        String smsApiKey) {

    public ShopSettingsView(ShopSettings settings){
        this(settings.getId(),settings.getPrimaryColor(),settings.getSecondaryColor(),settings.getGmail(),"//Private//",settings.getSmsApiKey());
    }
}
