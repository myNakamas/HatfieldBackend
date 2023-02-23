package com.nakamas.hatfieldbackend.config;

import com.nakamas.hatfieldbackend.models.entities.User;
import com.nakamas.hatfieldbackend.models.entities.shop.Shop;
import com.nakamas.hatfieldbackend.models.entities.shop.ShopSettings;
import com.nakamas.hatfieldbackend.models.enums.UserRole;
import com.nakamas.hatfieldbackend.models.views.incoming.CreateUser;
import com.nakamas.hatfieldbackend.repositories.ShopRepository;
import com.nakamas.hatfieldbackend.repositories.UserRepository;
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

    private static CreateUser defaultUser() {
        return new CreateUser("admin", "Admin FullName", "admin", UserRole.ADMIN, "admin@email.com", List.of(), null);
    }

    private static ShopSettings defaultShopSettings() {
        return new ShopSettings("#aaa", "#baa", "gmail", "password", "smsApiKey", null, null);
    }

    public void run(ApplicationArguments args) {
        User user = userService.createUser(defaultUser());
        Shop initialShop = new Shop("Hatfield", List.of(), "London, Street 023", "fakePhoneNum", "gakeEmail@email.com", "64243213001", "1245245", defaultShopSettings(), List.of(), List.of());
        Shop save = shopRepository.save(initialShop);
        user.setShop(save);
        userRepository.save(user);
    }
}
