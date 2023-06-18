package com.nakamas.hatfieldbackend.models.views.incoming;

import com.nakamas.hatfieldbackend.models.views.outgoing.shop.ShopSettingsView;

public record CreateShop(Long id, String shopName, String address, String phone, String email, String vatNumber,
                         String regNumber, ShopSettingsView shopSettingsView, ShopPageTemplatesView templates) {
    public CreateShop(Long id, String shopName, String address, String phone, String email, String vatNumber,
                      String regNumber, ShopSettingsView shopSettingsView) {
        this(id, shopName, address, phone, email, vatNumber,
                regNumber, shopSettingsView, new ShopPageTemplatesView());
    }
}
