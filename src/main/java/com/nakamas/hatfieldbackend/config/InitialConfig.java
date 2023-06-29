package com.nakamas.hatfieldbackend.config;

import com.nakamas.hatfieldbackend.models.entities.shop.Shop;
import com.nakamas.hatfieldbackend.models.entities.shop.ShopSettings;
import com.nakamas.hatfieldbackend.models.enums.UserRole;
import com.nakamas.hatfieldbackend.models.views.incoming.CreateUser;
import com.nakamas.hatfieldbackend.repositories.*;
import com.nakamas.hatfieldbackend.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class InitialConfig implements ApplicationRunner {
    private final UserService userService;
    private final UserRepository userRepository;
    private final ShopRepository shopRepository;

    private static CreateUser defaultUser(Long shopId) {
        return new CreateUser(null, "admin", "Admin FullName", "admin", UserRole.ADMIN, "admin@email.com", List.of(), shopId);
    }

    private static ShopSettings defaultShopSettings() {
        return new ShopSettings("#eec550",  "#203e5f", "gmail", "password", true, "192.168.0.51", "QL-580N", "smsApiKey", null, null);
    }

    public void run(ApplicationArguments args) {
        if(userRepository.count() == 0) {
            Shop initialShop = new Shop("Hatfield", "London, Street 023", "fakePhoneNum", "gakeEmail@email.com", defaultShopSettings());
            Shop save = shopRepository.save(initialShop);
            userService.createUser(defaultUser(save.getId()));
        }
    }
}
