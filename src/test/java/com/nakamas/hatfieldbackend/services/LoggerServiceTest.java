package com.nakamas.hatfieldbackend.services;

import com.nakamas.hatfieldbackend.models.entities.shop.InventoryItem;
import com.nakamas.hatfieldbackend.models.entities.shop.Shop;
import com.nakamas.hatfieldbackend.models.entities.ticket.Brand;
import com.nakamas.hatfieldbackend.models.entities.ticket.Model;
import com.nakamas.hatfieldbackend.models.views.incoming.CreateInventoryItem;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.HashMap;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY, connection = EmbeddedDatabaseConnection.H2)
class LoggerServiceTest {
    @Autowired
    LoggerService loggerService;
    private final Shop shop = new Shop();

    private final CreateInventoryItem view = new CreateInventoryItem(1L, "item", "Imei53212342", BigDecimal.ONE, BigDecimal.TEN, "Model", null, "Brand", null, 3, shop.getId(), null, new HashMap<>());
    private final CreateInventoryItem viewNullPrices = new CreateInventoryItem(1L, "item", "Imei53212342", null, null, "Model", null, "Brand", null, 3, shop.getId(), null, new HashMap<>());
    private final InventoryItem item = new InventoryItem(view, new Brand("Brand"), new Model("Model", 0L), shop, null);


    @Test
    void itemUpdateCheckSameValues_NoChange() {
        String logResult = loggerService.itemUpdateCheck(item, view);
        Assertions.assertEquals(" ", logResult);
    }

    @Test
    void itemUpdateCheckNullPrices_NoChange() {
        item.setPurchasePrice(null);
        item.setSellPrice(null);

        String logResult = loggerService.itemUpdateCheck(item, viewNullPrices);
        Assertions.assertEquals(" ", logResult);
    }

    @Test
    void itemUpdateCheckUpdateWithNullPrices_ShouldNotTrigger() {
        String logResult = loggerService.itemUpdateCheck(item, viewNullPrices);
        Assertions.assertEquals(" ", logResult);
    }

    @Test
    void itemUpdateCheckUpdateWithNonNullPrices_ShouldTrigger() {
        String expectedResult = " Purchase price updated from £%.2f to £%.2f;Sell price updated from £%.2f to £%.2f;".formatted(0f, view.purchasePrice(), 0f, view.sellPrice());
        item.setPurchasePrice(null);
        item.setSellPrice(null);
        String logResult = loggerService.itemUpdateCheck(item, view);
        Assertions.assertEquals(expectedResult, logResult);
    }
}