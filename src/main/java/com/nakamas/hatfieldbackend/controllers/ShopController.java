package com.nakamas.hatfieldbackend.controllers;

import com.nakamas.hatfieldbackend.models.entities.User;
import com.nakamas.hatfieldbackend.models.views.incoming.CreateShop;
import com.nakamas.hatfieldbackend.models.views.outgoing.shop.ShopSettingsView;
import com.nakamas.hatfieldbackend.models.views.outgoing.shop.ShopView;
import com.nakamas.hatfieldbackend.models.views.outgoing.shop.WorkerShopView;
import com.nakamas.hatfieldbackend.services.ShopService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/shop")
public class ShopController {

    private final ShopService shopService;

    @GetMapping("settings")
    public ShopSettingsView shopSettingsView(@AuthenticationPrincipal User user) {
        return shopService.getShopSettings(user.getShop().getId());
    }

    @GetMapping("/myShop")
    public ShopView getShopById(@AuthenticationPrincipal User user) {
        ShopView shopView = new ShopView(user.getShop());
        shopView.templates().fillTemplates(user.getShop());
        return shopView;
    }

    @GetMapping(path = "/image", produces = { MediaType.IMAGE_JPEG_VALUE })
    public void getShopImage(@RequestParam Long shopId, @Autowired HttpServletResponse response) {
        shopService.fillShopImageToResponse(shopId, response);
    }

    @PutMapping("/admin/image")
    public void updateShopImage(@RequestParam Long shopId, @RequestBody MultipartFile image) {
        shopService.updateShopImage(shopId, image);
    }

    @GetMapping("admin/all")
    public List<ShopView> getAllShops() {
        return shopService.getAllShops().stream().map(ShopView::new).collect(Collectors.toList());
    }

    @GetMapping("worker/all")
    public List<WorkerShopView> getAllWorkerShops() {
        return shopService.workerShops();
    }

    @GetMapping("admin/byId")
    public ShopView getShopsById(@RequestParam Long shopId) {
        return shopService.getShopById(shopId);
    }

    @PostMapping("admin/create")
    public ShopView createNewShop(@RequestBody CreateShop createView) {
        return new ShopView(shopService.create(createView));
    }

    @PutMapping("admin/update")
    public ShopView updateShop(@RequestBody CreateShop updateView) {
        return new ShopView(shopService.update(updateView));
    }
}
