package com.nakamas.hatfieldbackend.models.views.outgoing.shop;

import com.nakamas.hatfieldbackend.models.entities.shop.ShopSettings;

public record ShopSettingsView(
        Long id,
        String primaryColor,
        String secondaryLightColor,
        String secondaryColor,
        String secondaryDarkColor,
        String textColor,

        String gmail,
        String gmailPassword,
        String smsApiKey) {

    public ShopSettingsView(ShopSettings settings){
        this(settings.getId(),settings.getPrimaryColor(),settings.getSecondaryLightColor(), settings.getSecondaryColor(),settings.getSecondaryDarkColor(), settings.getTextColor(),settings.getGmail(),"//Private//",settings.getSmsApiKey());
    }
}
