package com.nakamas.hatfieldbackend;

import com.nakamas.hatfieldbackend.models.entities.shop.Shop;
import com.nakamas.hatfieldbackend.models.entities.shop.ShopSettings;
import com.nakamas.hatfieldbackend.models.enums.UserRole;
import com.nakamas.hatfieldbackend.models.views.incoming.CreateUser;

import java.util.List;

public final class TestData {
    public static final String correctPassword = "CorrectPassword";
    public static final String correctUsername = "CorrectUsername";

    static ShopSettings getTestShopSettings() {
        return new ShopSettings("#eec550", "#f9e3a3", "#203e5f", "#1a2634", "#fff", "gmail", "password", "smsApiKey", null, null);

    }

    static Shop getTestShop() {
        return new Shop("Hatfield", List.of(), "London, Street 023", "fakePhoneNum", "gakeEmail@email.com", "64243213001", "1245245", getTestShopSettings(), List.of(), List.of());
    }

    static CreateUser getTestUser(Shop shop) {
        return new CreateUser(null, correctUsername, "Adam John", correctPassword, UserRole.ADMIN, "email@email.com", List.of("+359898575932"), shop.getId());
    }
}
