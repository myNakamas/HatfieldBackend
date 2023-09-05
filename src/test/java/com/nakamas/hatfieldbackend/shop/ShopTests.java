package com.nakamas.hatfieldbackend.shop;

import com.nakamas.hatfieldbackend.TestData;
import com.nakamas.hatfieldbackend.config.exception.CustomException;
import com.nakamas.hatfieldbackend.models.entities.shop.Shop;
import com.nakamas.hatfieldbackend.models.entities.shop.ShopSettings;
import com.nakamas.hatfieldbackend.models.views.incoming.CreateShop;
import com.nakamas.hatfieldbackend.models.views.outgoing.shop.ShopSettingsView;
import com.nakamas.hatfieldbackend.models.views.outgoing.shop.ShopView;
import com.nakamas.hatfieldbackend.repositories.ShopRepository;
import com.nakamas.hatfieldbackend.services.ShopService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY, connection = EmbeddedDatabaseConnection.H2)
public class ShopTests {

    @Autowired
    private ShopService shopService;

    @Autowired
    private ShopRepository shopRepository;

    @AfterEach
    void tearDown() {
        shopRepository.deleteAll();
    }

    @Test
    public void testGetAllShops() {
        Shop shop1 = TestData.getTestShop();
        Shop shop2 = TestData.getTestShop();
        shop2.setShopName("Second shop name");
        List<Shop> shops = Arrays.asList(shop1, shop2);
        shopRepository.saveAll(shops);
        List<Shop> result = shopService.getAllShops();
        assertEquals(2, result.size());
    }

    @Test
    public void testGetShopSettings() {
        Shop shop = TestData.getTestShop();
        Shop save = shopRepository.save(shop);
        ShopSettingsView result = shopService.getShopSettings(save.getId());
        assertEquals(save.getSettings().getPrimaryColor(), result.primaryColor());
    }

    @Test
    public void testGetShopById() {
        Shop shop = TestData.getTestShop();
        Shop save = shopRepository.save(shop);
        ShopView result = shopService.getShopById(save.getId());
        assertEquals(save.getId(), result.id());
    }

    @Test
    public void testCreate() {
        ShopSettings settings = TestData.getTestShopSettings();
        CreateShop create = new CreateShop(null, "New Shop", "Address line", "0893728371", "shop.email@email.com", "654326543", "234542", new ShopSettingsView(settings));
        Shop shop = new Shop(create);
        Shop result = shopService.create(create);
        assertEquals(shop.getShopName(), result.getShopName());
    }

    @Test
    public void testUpdate() {
        Shop save = shopRepository.save(TestData.getTestShop());
        CreateShop update = new CreateShop(save.getId(), "Other name", "some other address", "+359-898768595", "email.new@gmail.com", "3456", "numReg", new ShopSettingsView(TestData.getTestShopSettings()));
        Shop result = shopService.update(update);
        assertEquals(update.shopName(), result.getShopName());
    }

    @Test()
    public void testUpdateShopNotFound() {
        CreateShop update = new CreateShop(1L, "Other name", "some other address", "+359-898768595", "email.new@gmail.com", "3456", "numReg", new ShopSettingsView(TestData.getTestShopSettings()));
        assertThrows(CustomException.class, () -> shopService.update(update));
    }

    @Test()
    public void testGetShopNotFound() {
        assertThrows(CustomException.class, () -> shopService.getShopSettings(23L));
    }
}
