package com.nakamas.hatfieldbackend.services;

import com.nakamas.hatfieldbackend.models.entities.User;
import com.nakamas.hatfieldbackend.models.entities.shop.Category;
import com.nakamas.hatfieldbackend.models.entities.shop.InventoryItem;
import com.nakamas.hatfieldbackend.models.entities.shop.Shop;
import com.nakamas.hatfieldbackend.models.entities.shop.UsedPart;
import com.nakamas.hatfieldbackend.models.entities.ticket.Ticket;
import com.nakamas.hatfieldbackend.models.enums.ItemType;
import com.nakamas.hatfieldbackend.models.views.incoming.CreateInventoryItem;
import com.nakamas.hatfieldbackend.models.views.incoming.CreateShop;
import com.nakamas.hatfieldbackend.models.views.incoming.CreateTicket;
import com.nakamas.hatfieldbackend.models.views.incoming.PageRequestView;
import com.nakamas.hatfieldbackend.models.views.incoming.filters.InventoryItemFilter;
import com.nakamas.hatfieldbackend.models.views.outgoing.PageView;
import com.nakamas.hatfieldbackend.models.views.outgoing.shop.CategoryView;
import com.nakamas.hatfieldbackend.models.views.outgoing.shop.InventoryItemView;
import com.nakamas.hatfieldbackend.models.views.outgoing.shop.ShopSettingsView;
import com.nakamas.hatfieldbackend.repositories.*;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static com.nakamas.hatfieldbackend.TestData.*;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY, connection = EmbeddedDatabaseConnection.H2)
class InventoryItemTest {

    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private InventoryItemRepository inventoryItemRepository;
    @Autowired
    private ShopRepository shopRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private TicketRepository ticketRepository;
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private InventoryItemService inventoryItemService;
    private Shop shop;
    private User loggedUser;

    @BeforeEach
    void setUp() {
        shop = shopRepository.save(getTestShop());
        loggedUser = userService.createUser(getTestUser(shop));
    }

    @AfterEach
    void tearDown() {
        inventoryItemRepository.deleteAll();
        categoryRepository.deleteAll();
        ticketRepository.deleteAll();
        userRepository.deleteAll();
        shopRepository.deleteAll();
    }

    @Test
    public void deleteCategoryWithItem_shouldNotDeleteItems() {
        Category category = categoryRepository.save(new Category("Category", ItemType.PART, List.of("Field 1", "Field 2")));
        assertNotNull(category.getId());
        InventoryItem item = inventoryItemService.createInventoryItem(getTestInventoryItem(shop, category));
        assertNotNull(item.getId());

        inventoryItemService.deleteCategory(category.getId());
        assertFalse(categoryRepository.existsById(category.getId()));

        entityManager.refresh(item);
        assertNotNull(item);
        assertNull(item.getCategoryId());
    }

    @Test
    public void createInventoryItem_shouldCreateItem() {
        Category category = new Category("Category", ItemType.PART, List.of("Color", "Capacity"));
        categoryRepository.save(category);
        CreateInventoryItem inventoryItem = getTestInventoryItem(shop, category);
        inventoryItem.properties().put("Color", "Blue");
        inventoryItem.properties().put("Capacity", "64 GB");
        InventoryItem result = inventoryItemService.createInventoryItem(inventoryItem);

        assertNotNull(result.getId());
        assertEquals(inventoryItem.brand(), result.getBrand().getBrand());
        assertEquals(inventoryItem.model(), result.getModel().getModel());
        assertEquals(shop.getId(), result.getShop().getId());
        assertEquals(category.getId(), result.getCategoryId());
        assertEquals("Blue", result.getPropertyValue("Color"));
        assertEquals("64 GB", result.getPropertyValue("Capacity"));
    }

    @Test
    public void getAllCategoryViews_shouldReturnAllCategories() {
        Category category1 = new Category("Category 1", ItemType.PART, List.of("Field 1", "Field 2"));
        Category category2 = new Category("Category 2", ItemType.PART, List.of("Field 3", "Field 4"));
        categoryRepository.save(category1);
        categoryRepository.save(category2);

        List<CategoryView> result = inventoryItemService.getAllCategoryViews();

        assertEquals(2, result.size());
        assertEquals("Category 1", result.get(0).name());
        assertEquals(List.of("Field 1", "Field 2"), result.get(0).columns());
        assertEquals("Category 2", result.get(1).name());
        assertEquals(List.of("Field 3", "Field 4"), result.get(1).columns());
    }

    @Test
    public void useItemForTicket_shouldReduceItemCount() {
        Category category = categoryRepository.save(getCategory());
        CreateInventoryItem testInventoryItem = getTestInventoryItem(shop, category);
        InventoryItem item = inventoryItemService.createInventoryItem(testInventoryItem);
        Long itemId = item.getId();
        CreateTicket createTicket = getTestTicket(loggedUser);
        Ticket ticket = ticketRepository.save(new Ticket(createTicket,loggedUser));
        UsedPart result = inventoryItemService.useItemForTicket(itemId, ticket,2);

        assertNotNull(result.getId());
        assertEquals(itemId, result.getItem().getId());
        assertEquals(2, result.getUsedCount());
        assertNotNull(result.getTimestamp());
        assertNotNull(itemId);
        assertEquals(ticket.getId(),result.getTicket().getId());
        InventoryItem updatedItem = inventoryItemRepository.findById(itemId).orElse(null);
        assertNotNull(updatedItem);
        assertEquals(testInventoryItem.count() - 2, updatedItem.getCount().intValue());
    }

    @Test
    public void getShopInventory_shouldReturnItemsForShop() {
        Category category = categoryRepository.save(getCategory());
        CreateShop create1 = new CreateShop(null, "Shop 1", "Address line", "0893728371", "shop.email@email.com", "654326543", "234542", new ShopSettingsView(getTestShopSettings()));
        CreateShop create2 = new CreateShop(null, "Shop 2", "Address line", "0893728371", "shop.email@email.com", "654326543", "234542", new ShopSettingsView(getTestShopSettings()));

        Shop shop1 = shopRepository.save(new Shop(create1));
        Shop shop2 = shopRepository.save(new Shop(create2));
        InventoryItem item1 = inventoryItemService.createInventoryItem(getTestInventoryItem(shop1, category));
        InventoryItem item2 = inventoryItemService.createInventoryItem(getTestInventoryItem(shop1, category));
        InventoryItem item3 = inventoryItemService.createInventoryItem(getTestInventoryItem(shop2, category));
        InventoryItemFilter filter = new InventoryItemFilter();
        filter.setCategoryId(category.getId());
        filter.setShopId(shop1.getId());

        PageView<InventoryItemView> result = inventoryItemService.getShopInventory(shop1.getId(), filter, new PageRequestView());
        assertEquals(2, result.getContent().size());
        assertTrue(result.getContent().stream().anyMatch(item -> item.id().equals(item1.getId())));
        assertTrue(result.getContent().stream().anyMatch(item -> item.id().equals(item2.getId())));
        assertFalse(result.getContent().stream().anyMatch(item -> item.id().equals(item3.getId())));
    }

    @Test
    public void createCategory_shouldCreateCategory() {
        CategoryView categoryView = new CategoryView(null, "Category", ItemType.CABLES, new ArrayList<>());

        CategoryView result = inventoryItemService.createCategory(categoryView);

        assertNotNull(result.id());
        assertEquals(categoryView.name(), result.name());
    }

    @Test
    public void updateCategory_shouldUpdateCategory() {
        Category category = categoryRepository.save(new Category("Category 1", ItemType.PART, List.of("Field 1", "Field 2")));
        CategoryView updatedCategory = new CategoryView(null, "Updated Category", ItemType.CABLES, new ArrayList<>());

        CategoryView result = inventoryItemService.updateCategory(updatedCategory, category.getId());

        assertEquals(category.getId(), result.id());
        assertEquals(updatedCategory.name(), result.name());
        assertEquals(updatedCategory.name(), result.name());
    }

    @Test
    public void changeNeed_shouldChangeNeed() {
        InventoryItem item = inventoryItemRepository.save(new InventoryItem());
        assertNotNull(item.getId());
        Boolean need = true;

        inventoryItemService.changeNeed(item.getId(), need);

        InventoryItem result = inventoryItemRepository.findById(item.getId()).orElse(null);
        assertNotNull(result);
        assertEquals(need, result.getShoppingListNeeded());
    }

    @Test
    public void getItem_shouldReturnItemById() {
        InventoryItem item = inventoryItemRepository.save(new InventoryItem());

        InventoryItem result = inventoryItemService.getItem(item.getId());

        assertNotNull(result);
        assertEquals(item.getId(), result.getId());
    }
}