package com.nakamas.hatfieldbackend.config;

import com.nakamas.hatfieldbackend.models.entities.shop.ShopSettings;
import com.nakamas.hatfieldbackend.models.enums.UserRole;
import com.nakamas.hatfieldbackend.models.views.incoming.CreateUser;
import com.nakamas.hatfieldbackend.repositories.BrandRepository;
import com.nakamas.hatfieldbackend.repositories.CategoryRepository;
import com.nakamas.hatfieldbackend.repositories.ModelRepository;
import com.nakamas.hatfieldbackend.repositories.ShopRepository;
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
    private final ShopRepository shopRepository;
    private final ModelRepository modelRepository;
    private final BrandRepository brandRepository;
    private final CategoryRepository categoryRepository;

    private static CreateUser defaultUser(Long shopId) {
        return new CreateUser(null, "admin", "Admin FullName", "admin", UserRole.ADMIN, "admin@email.com", List.of(), shopId);
    }

    private static ShopSettings defaultShopSettings() {
        return new ShopSettings("#eec550",  "#203e5f", "gmail", "password", "smsApiKey", null, null);
    }

    public void run(ApplicationArguments args) {
//        Shop initialShop = new Shop("Hatfield", List.of(), "London, Street 023", "fakePhoneNum", "gakeEmail@email.com", "64243213001", "1245245", defaultShopSettings(), List.of(), List.of());
//        Shop save = shopRepository.save(initialShop);
//        userService.createUser(defaultUser(save.getId()));
//        modelRepository.save(new Model("new Model"));
//        brandRepository.save(new Brand("new Brand"));
//        categoryRepository.save(new Category("Category name", ItemType.PART, List.of("Name", "Wattage")));
    }
}
