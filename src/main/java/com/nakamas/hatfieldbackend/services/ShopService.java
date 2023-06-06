package com.nakamas.hatfieldbackend.services;

import com.nakamas.hatfieldbackend.config.exception.CustomException;
import com.nakamas.hatfieldbackend.models.entities.Log;
import com.nakamas.hatfieldbackend.models.entities.shop.Shop;
import com.nakamas.hatfieldbackend.models.enums.LogType;
import com.nakamas.hatfieldbackend.models.views.incoming.CreateShop;
import com.nakamas.hatfieldbackend.models.views.outgoing.shop.ShopSettingsView;
import com.nakamas.hatfieldbackend.models.views.outgoing.shop.ShopView;
import com.nakamas.hatfieldbackend.models.views.outgoing.shop.WorkerShopView;
import com.nakamas.hatfieldbackend.repositories.ShopRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ShopService {
    private final ShopRepository shopRepository;

    private final LoggerService loggerService;

    public List<Shop> getAllShops() {
        return shopRepository.findAll();
    }

    public ShopSettingsView getShopSettings(Long shopId) {
        Shop shop = shopRepository.findById(shopId).orElseThrow(() -> new CustomException("Cannot find settings"));
        return new ShopSettingsView(shop.getSettings());
    }

    public ShopView getShopById(Long id){
        return new ShopView(shopRepository.findById(id).orElseThrow(() -> new CustomException("")));
    }
    public Shop create(CreateShop create) {
        Shop newShop = shopRepository.save(new Shop(create));
        loggerService.shopActions(new Log(LogType.CREATED_SHOP), newShop);
        return newShop;
    }

    public Shop update(CreateShop updateView) {
        Optional<Shop> byId = shopRepository.findById(updateView.id());
        Shop shop = byId.orElseThrow(() -> new CustomException("Could not find shop with id '%s'".formatted(updateView.id())));
        shop.update(updateView);
        loggerService.shopActions(new Log(LogType.UPDATED_SHOP), shop);
        return shopRepository.save(shop);
    }

    public List<WorkerShopView> workerShops(){
        List<Shop> allAdminShops = shopRepository.findAll();
        List<WorkerShopView> allWorkerShops = new ArrayList<>();
        for (Shop shop: allAdminShops) {
            allWorkerShops.add(new WorkerShopView(shop.getId(), shop.getShopName()));
        }
        return allWorkerShops;
    }


}
