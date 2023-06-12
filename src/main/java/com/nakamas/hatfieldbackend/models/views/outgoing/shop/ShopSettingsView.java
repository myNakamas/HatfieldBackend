package com.nakamas.hatfieldbackend.models.views.outgoing.shop;

import com.nakamas.hatfieldbackend.models.entities.shop.ShopSettings;

public record ShopSettingsView(
        String primaryColor,
        String secondaryColor,
        String gmail,
        String gmailPassword,
        boolean print,
        String printerIp,
        String printerModel,
        String smsApiKey) {

    public ShopSettingsView(ShopSettings settings){
        this(settings.getPrimaryColor(), settings.getSecondaryColor(), settings.getGmail(), settings.getGmailPassword(), settings.isPrint(), settings.getPrinterIp(), settings.getPrinterModel(), settings.getSmsApiKey());
    }
}
