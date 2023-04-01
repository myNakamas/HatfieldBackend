package com.nakamas.hatfieldbackend;

import com.nakamas.hatfieldbackend.models.entities.User;
import com.nakamas.hatfieldbackend.models.entities.shop.Category;
import com.nakamas.hatfieldbackend.models.entities.shop.Shop;
import com.nakamas.hatfieldbackend.models.entities.shop.ShopSettings;
import com.nakamas.hatfieldbackend.models.enums.ItemType;
import com.nakamas.hatfieldbackend.models.enums.TicketStatus;
import com.nakamas.hatfieldbackend.models.enums.UserRole;
import com.nakamas.hatfieldbackend.models.views.incoming.CreateInventoryItem;
import com.nakamas.hatfieldbackend.models.views.incoming.CreateTicket;
import com.nakamas.hatfieldbackend.models.views.incoming.CreateUser;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public final class TestData {
    public static final String correctPassword = "CorrectPassword";
    public static final String correctUsername = "CorrectUsername";
    private static final Random random = new Random();

    public static ShopSettings getTestShopSettings() {
        return new ShopSettings("#eec550", "#1a2634", "gmail", "password", "smsApiKey", null, null);
    }

    public static Shop getTestShop() {
        return new Shop("Hatfield", List.of(), "London, Street 023", "fakePhoneNum", "gakeEmail@email.com", "64243213001", "1245245", getTestShopSettings(), List.of(), List.of());
    }

    public static CreateUser getTestUser(Shop shop) {
        return new CreateUser(null, correctUsername, "Adam John", correctPassword, UserRole.ADMIN, "email@email.com", List.of("+359898575932"), shop.getId());
    }

    public static CreateUser getTestUser(String username, String email, UserRole role, Shop shop) {
        return new CreateUser(null, username, "Adam John", correctPassword, role, email, List.of("+359898575932"), shop.getId());
    }

    public static Category getCategory() {
        return new Category("CategoryName", ItemType.DEVICE, new ArrayList<>());
    }

    public static CreateInventoryItem getTestInventoryItem(Shop shop, Category category) {
        return new CreateInventoryItem(null, "Brand" + random.nextInt(), null, "Model" + random.nextInt(), 10, shop.getId(), category.getId(), new HashMap<>());
    }

    public static CreateTicket getTestTicket(User client) {
        return new CreateTicket("TestDeviceModel" + random.nextInt(10), "TestDeviceBrand" + random.nextInt(10), "Lab" + random.nextInt(), "", "The screen is broken", "", random.nextInt() + "", "83753373", "none", LocalDateTime.now().plusDays(5), "", TicketStatus.PENDING, BigDecimal.valueOf(random.nextDouble()), BigDecimal.ZERO, client.getId(), random.nextInt(30));
    }
}
