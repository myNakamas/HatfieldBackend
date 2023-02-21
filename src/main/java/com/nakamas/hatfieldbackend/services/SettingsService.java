package com.nakamas.hatfieldbackend.services;

import com.nakamas.hatfieldbackend.models.entities.shop.Shop;
import com.nakamas.hatfieldbackend.models.views.outgoing.shop.ShopSettingsView;
import com.nakamas.hatfieldbackend.repositories.SettingsRepository;
import com.nakamas.hatfieldbackend.repositories.ShopRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SettingsService {
    private final ShopRepository shopRepository;
    private final SettingsRepository settingsRepository;

    public ShopSettingsView getShopSettings(Long shopId){
        Shop shop = shopRepository.findById(shopId).orElseThrow();
        return new ShopSettingsView(shop.getSettings());
    }
}
