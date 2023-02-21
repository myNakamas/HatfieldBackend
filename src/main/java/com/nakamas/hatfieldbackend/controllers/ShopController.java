package com.nakamas.hatfieldbackend.controllers;

import com.nakamas.hatfieldbackend.models.entities.User;
import com.nakamas.hatfieldbackend.models.views.outgoing.shop.ShopSettingsView;
import com.nakamas.hatfieldbackend.services.SettingsService;
import com.nakamas.hatfieldbackend.services.ShopService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@RequestMapping("api/shop")
public class ShopController {

    private final ShopService shopService;
    private final SettingsService settingsService;

    @GetMapping("/settings")
    public ShopSettingsView shopSettingsView(@Autowired Authentication authentication){
        User user = (User) authentication.getPrincipal();
        return settingsService.getShopSettings(user.getShop().getId());
    }

//    @PostMapping("create")

}
