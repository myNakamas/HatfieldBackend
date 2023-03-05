package com.nakamas.hatfieldbackend;

import com.nakamas.hatfieldbackend.models.entities.shop.InventoryItem;
import com.nakamas.hatfieldbackend.models.entities.shop.Shop;
import com.nakamas.hatfieldbackend.models.entities.ticket.Brand;
import com.nakamas.hatfieldbackend.models.entities.ticket.Model;
import com.nakamas.hatfieldbackend.models.enums.ItemType;
import com.nakamas.hatfieldbackend.models.views.incoming.CreateInventoryItem;
import com.nakamas.hatfieldbackend.repositories.BrandRepository;
import com.nakamas.hatfieldbackend.repositories.InventoryItemRepository;
import com.nakamas.hatfieldbackend.repositories.ModelRepository;
import com.nakamas.hatfieldbackend.repositories.ShopRepository;
import com.nakamas.hatfieldbackend.services.InventoryItemService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY, connection = EmbeddedDatabaseConnection.H2)
public class TestShopInventory {
    @Autowired
    private InventoryItemService inventoryItemService;
    @Autowired
    private ShopRepository shopRepository;
    @Autowired
    private InventoryItemRepository inventoryItemRepository;
    @Autowired
    private ModelRepository modelRepository;
    @Autowired
    private BrandRepository brandRepository;

    private Shop shop;

    @BeforeEach
    void setUp() {
        Shop testShop = TestData.getTestShop();
        shop = shopRepository.save(testShop);
    }

    @AfterEach
    void tearDown() {
        inventoryItemRepository.deleteAll();
        modelRepository.deleteAll();
        brandRepository.deleteAll();
    }

    @Test
    void add_item_with_new_values() {
        CreateInventoryItem inventoryItem = TestData.getInventoryItem(shop);
        InventoryItem save = inventoryItemService.createInventoryItem(inventoryItem);

        Assertions.assertEquals(inventoryItem.brand(), save.getBrand().getBrand());
        Assertions.assertEquals(inventoryItem.model(), save.getModel().getModel());
    }

    @Test
    void add_item_with_existing_values() {
        Brand newBrand = brandRepository.save(new Brand("newBrand"));
        Model newModel = modelRepository.save(new Model("newModel"));
        CreateInventoryItem inventoryItem = new CreateInventoryItem(newBrand.getId(), null, newModel.getId(), null, 10, shop.getId(), ItemType.DEVICE);
        InventoryItem save = inventoryItemService.createInventoryItem(inventoryItem);

        Assertions.assertEquals(inventoryItem.brandId(), save.getBrand().getId());
        Assertions.assertEquals(inventoryItem.modelId(), save.getModel().getId());
    }
}
