package com.nakamas.hatfieldbackend.models.views.outgoing.shop;

import com.nakamas.hatfieldbackend.models.entities.shop.Shop;

public record ShopView(Long id, String shopName, String address, String phone, String email, String vatNumber,
                       String regNumber, ShopSettingsView shopSettingsView) {
    public ShopView(Shop shop) {
        this(shop.getId(), shop.getShopName(), shop.getAddress(), shop.getPhone(), shop.getEmail(), shop.getVatNumber(), shop.getRegNumber(), new ShopSettingsView(shop.getSettings()));
    }
}
