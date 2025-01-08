package com.nakamas.hatfieldbackend.services;

import com.nakamas.hatfieldbackend.config.exception.CustomException;
import com.nakamas.hatfieldbackend.models.entities.Log;
import com.nakamas.hatfieldbackend.models.entities.Photo;
import com.nakamas.hatfieldbackend.models.entities.shop.Shop;
import com.nakamas.hatfieldbackend.models.enums.LogType;
import com.nakamas.hatfieldbackend.models.views.incoming.CreateShop;
import com.nakamas.hatfieldbackend.models.views.outgoing.shop.ShopSettingsView;
import com.nakamas.hatfieldbackend.models.views.outgoing.shop.ShopView;
import com.nakamas.hatfieldbackend.models.views.outgoing.shop.WorkerShopView;
import com.nakamas.hatfieldbackend.repositories.ShopRepository;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ShopService {
    private final ShopRepository shopRepository;

    private final LoggerService loggerService;
    private final PhotoService photoService;

    public List<Shop> getAllShops() {
        return shopRepository.findAll();
    }

    public ShopSettingsView getShopSettings(Long shopId) {
        Shop shop = getShop(shopId);
        return new ShopSettingsView(shop.getSettings());
    }

    private Shop getShop(Long shopId) {
        return shopRepository.findById(shopId).orElseThrow(() -> new CustomException("Cannot find shop"));
    }

    public ShopView getShopById(Long id) {
        return new ShopView(shopRepository.findById(id).orElseThrow(() -> new CustomException("")));
    }

    public Shop create(CreateShop create) {
        Shop newShop = shopRepository.save(new Shop(create));
        loggerService.createLog(new Log(LogType.CREATED_SHOP), newShop.getShopName());
        return newShop;
    }

    public Shop update(CreateShop updateView) {
        Shop shop = getShop(updateView.id());
        String updateInfo = loggerService.shopUpdateCheck(shop, updateView);
        shop.update(updateView);
        loggerService.createLog(new Log(LogType.UPDATED_SHOP), shop.getShopName(), updateInfo);
        return shopRepository.save(shop);
    }

    public List<WorkerShopView> workerShops() {
        List<Shop> allAdminShops = shopRepository.findAll();
        List<WorkerShopView> allWorkerShops = new ArrayList<>();
        for (Shop shop : allAdminShops) {
            allWorkerShops.add(new WorkerShopView(shop.getId(), shop.getShopName()));
        }
        return allWorkerShops;
    }

    public void fillShopImageToResponse(Long shopId, HttpServletResponse response) {
        Shop shop = getShop(shopId);
        if(shop.getImage() == null ) return;
        photoService.writeToResponse(response, shop.getImage());
    }

    @Transactional
    public void updateShopImage(Long shopId, MultipartFile image) {
        Shop shop = getShop(shopId);
        Photo photo = photoService.saveShopImage(shopId, image);
        shop.setImage(photo);
        loggerService.createLog(new Log(LogType.UPDATED_SHOP), shop.getShopName(), "The shop photo was changed.");
        shopRepository.save(shop);
    }
}
