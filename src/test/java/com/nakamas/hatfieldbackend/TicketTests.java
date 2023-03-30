package com.nakamas.hatfieldbackend;

import com.nakamas.hatfieldbackend.models.entities.User;
import com.nakamas.hatfieldbackend.models.entities.shop.Category;
import com.nakamas.hatfieldbackend.models.entities.shop.InventoryItem;
import com.nakamas.hatfieldbackend.models.entities.shop.Shop;
import com.nakamas.hatfieldbackend.models.entities.ticket.Ticket;
import com.nakamas.hatfieldbackend.models.enums.UserRole;
import com.nakamas.hatfieldbackend.models.views.incoming.CreateTicket;
import com.nakamas.hatfieldbackend.models.views.incoming.PageRequestView;
import com.nakamas.hatfieldbackend.models.views.incoming.filters.TicketFilter;
import com.nakamas.hatfieldbackend.models.views.outgoing.PageView;
import com.nakamas.hatfieldbackend.models.views.outgoing.ticket.TicketView;
import com.nakamas.hatfieldbackend.repositories.*;
import com.nakamas.hatfieldbackend.services.InventoryItemService;
import com.nakamas.hatfieldbackend.services.TicketService;
import com.nakamas.hatfieldbackend.services.UserService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY, connection = EmbeddedDatabaseConnection.H2)
public class TicketTests {
    @Autowired
    private InventoryItemService inventoryItemService;
    @Autowired
    private ShopRepository shopRepository;
    @Autowired
    private InventoryItemRepository inventoryItemRepository;
    @Autowired
    private ModelRepository modelRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private BrandRepository brandRepository;
    @Autowired
    private TicketRepository ticketRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private TicketService ticketService;
    @Autowired
    private UsedPartRepository usedPartRepository;

    private Shop shop;
    private User user;
    private User client;
    private List<InventoryItem> items;

    @BeforeEach
    void setUp() {
        Shop testShop = TestData.getTestShop();
        shop = shopRepository.save(testShop);
        user = userService.createUser(TestData.getTestUser(shop));
        client = userService.createUser(TestData.getTestUser("client", "client@email.com", UserRole.CLIENT, shop));
        Category category = categoryRepository.save(TestData.getCategory());
        for (int i = 0; i < 3; i++)
            inventoryItemService.createInventoryItem(TestData.getTestInventoryItem(shop, category));
        items = inventoryItemRepository.findAll();
    }

    @AfterEach
    void tearDown() {
        ticketRepository.deleteAll();
        userRepository.deleteAll();
        inventoryItemRepository.deleteAll();
        categoryRepository.deleteAll();
        modelRepository.deleteAll();
        brandRepository.deleteAll();
        shopRepository.deleteAll();
    }

    @Test
    void create_ticket() {
        CreateTicket createTicket = TestData.getTestTicket(client);
        Ticket ticket = ticketService.createTicket(createTicket, user);

        Assertions.assertEquals(ticket.getShop().getId(), user.getShop().getId());
    }

    @Test
    void filter_tickets_by_createdBy() {
        CreateTicket createTicket = TestData.getTestTicket(client);
        User user2 = userService.createUser(TestData.getTestUser("salesman", "salesman@email.com", UserRole.SALESMAN, shop));

        Ticket ticket = ticketService.createTicket(createTicket, user);
        ticketService.createTicket(createTicket, user2);
        TicketFilter ticketFilter = new TicketFilter();
        ticketFilter.setCreatedById(user.getId());
        PageView<TicketView> filtered = ticketService.findAll(ticketFilter, new PageRequestView());
        PageView<TicketView> all = ticketService.findAll(new TicketFilter(), new PageRequestView());

        Assertions.assertEquals(1, filtered.getTotalCount());
        Assertions.assertEquals(ticket.getId(), filtered.getContent().get(0).id());
        Assertions.assertEquals(2, all.getTotalCount());
    }

    @Test
    @Transactional
    void add_item_from_inventory() {
        CreateTicket createTicket = TestData.getTestTicket(client);
        Ticket ticket = ticketService.createTicket(createTicket, user);
        ticket = ticketService.usePartFromInventory(ticket.getId(), items.get(0).getId(), user, 1);

        Assertions.assertEquals(1, ticket.getUsedParts().size());
        Assertions.assertEquals(1, usedPartRepository.count());
    }
}