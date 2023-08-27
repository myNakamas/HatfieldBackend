package com.nakamas.hatfieldbackend.config;

import com.nakamas.hatfieldbackend.models.entities.shop.DeviceLocation;
import com.nakamas.hatfieldbackend.models.entities.shop.Shop;
import com.nakamas.hatfieldbackend.models.entities.shop.ShopSettings;
import com.nakamas.hatfieldbackend.models.entities.ticket.Brand;
import com.nakamas.hatfieldbackend.models.enums.UserRole;
import com.nakamas.hatfieldbackend.models.views.incoming.CreateUser;
import com.nakamas.hatfieldbackend.repositories.BrandRepository;
import com.nakamas.hatfieldbackend.repositories.DeviceLocationRepository;
import com.nakamas.hatfieldbackend.repositories.ShopRepository;
import com.nakamas.hatfieldbackend.repositories.UserRepository;
import com.nakamas.hatfieldbackend.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class InitialConfig implements ApplicationRunner {
    private static final String[] INITIAL_BRANDS_FOR_DB = {"Apple", "Samsung", "HP", "Dell", "Lenovo", "Xiaomi", "Nokia", "HTC", "OnePlus", "Motorola", "Sony", "DJI", "Huawei", "Honor", "Razer", "Vivo", "Oppo", "Redmi"};
    private static final String[] INITIAL_LOCATIONS_FOR_DB = {"AT_THE_FRONT","IN_THE_LAB"};
    private final UserService userService;
    private final UserRepository userRepository;
    private final BrandRepository brandsRepository;
    private final DeviceLocationRepository deviceLocationRepository;
    private final ShopRepository shopRepository;

    private static CreateUser defaultUser(Long shopId) {
        return new CreateUser(null, "admin", "Admin FullName", "admin", UserRole.ADMIN, "admin@email.com", List.of(), shopId);
    }

    private static ShopSettings defaultShopSettings() {
        return new ShopSettings("#eec550", "#203e5f", false, "gmail", "password", true, "192.168.0.51", "QL-580N", false, "smsApiKey", null, null);
    }

    private void persistInitialBrands() {
        List<Brand> brands = Arrays.stream(INITIAL_BRANDS_FOR_DB).map(Brand::new).toList();
        brandsRepository.saveAll(brands);
    }

    private void persistInitialLocations() {
        List<DeviceLocation> locations = Arrays.stream(INITIAL_LOCATIONS_FOR_DB).map(DeviceLocation::new).toList();
        deviceLocationRepository.saveAll(locations);
    }

    public void run(ApplicationArguments args) {
        if (userRepository.count() == 0) {
            Shop initialShop = new Shop("Hatfield", "London, Street 023", "fakePhoneNum", "gakeEmail@email.com", defaultShopSettings());
            Shop save = shopRepository.save(initialShop);
            userService.createUser(defaultUser(save.getId()));
            persistInitialBrands();
            persistInitialLocations();
        }
    }
}
