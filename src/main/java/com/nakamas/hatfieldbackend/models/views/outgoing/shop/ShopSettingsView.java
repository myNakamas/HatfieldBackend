package com.nakamas.hatfieldbackend.models.views.outgoing.shop;

import com.nakamas.hatfieldbackend.models.entities.shop.ShopSettings;

public record ShopSettingsView(
        String primaryColor,
        String secondaryColor,
        Boolean emailNotificationsEnabled,
        String gmail,
        String gmailPassword,
        Boolean printEnabled,
        String printerIp,
        String printerModel,
        Boolean smsNotificationsEnabled,
        String smsApiKey) {

    public ShopSettingsView(ShopSettings settings) {
        this(settings.getPrimaryColor(), settings.getSecondaryColor(), settings.isEmailEnabled(), settings.getGmail(), settings.getGmailPassword(), settings.isPrintEnabled(), settings.getPrinterIp(), settings.getPrinterModel(), settings.isSmsEnabled(), settings.getSmsApiKey());
    }
}
