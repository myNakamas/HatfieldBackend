package com.nakamas.hatfieldbackend.models.views.incoming;

import com.nakamas.hatfieldbackend.models.views.outgoing.shop.ShopSettingsView;

public record CreateShop(Long id, String shopName, String address, String phone, String email, String vatNumber,
                         String regNumber, ShopSettingsView shopSettingsView) {
}
