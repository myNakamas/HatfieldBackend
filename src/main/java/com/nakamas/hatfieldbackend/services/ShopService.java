package com.nakamas.hatfieldbackend.services;

import com.nakamas.hatfieldbackend.config.exception.CustomException;
import com.nakamas.hatfieldbackend.models.entities.shop.Shop;
import com.nakamas.hatfieldbackend.models.views.incoming.CreateShop;
import com.nakamas.hatfieldbackend.models.views.outgoing.shop.ShopSettingsView;
import com.nakamas.hatfieldbackend.models.views.outgoing.shop.ShopView;
import com.nakamas.hatfieldbackend.repositories.ShopRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ShopService {
    private final ShopRepository shopRepository;

    public List<Shop> getAllShops() {
        return shopRepository.findAll();
    }

    public ShopSettingsView getShopSettings(Long shopId) {
        Shop shop = shopRepository.findById(shopId).orElseThrow();
        return new ShopSettingsView(shop.getSettings());
    }

    public ShopView getShopById(Long id){
        return new ShopView(shopRepository.findById(id).orElseThrow(() -> new CustomException("")));
    }
    public Shop create(CreateShop create) {
        return shopRepository.save(new Shop(create));
    }

    public Shop update(CreateShop updateView) {
        Optional<Shop> byId = shopRepository.findById(updateView.id());
        Shop shop = byId.orElseThrow(() -> new CustomException("Could not find shop with id '%s'".formatted(updateView.id())));
        shop.update(updateView);
        return shopRepository.save(shop);
    }
}
