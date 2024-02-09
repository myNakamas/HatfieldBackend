package com.nakamas.hatfieldbackend;

import com.nakamas.hatfieldbackend.models.entities.User;
import com.nakamas.hatfieldbackend.models.entities.shop.Category;
import com.nakamas.hatfieldbackend.models.entities.shop.InventoryItem;
import com.nakamas.hatfieldbackend.models.entities.shop.Shop;
import com.nakamas.hatfieldbackend.models.entities.ticket.Ticket;
import com.nakamas.hatfieldbackend.models.enums.TicketStatus;
import com.nakamas.hatfieldbackend.models.enums.UserRole;
import com.nakamas.hatfieldbackend.models.views.incoming.CreateTicket;
import com.nakamas.hatfieldbackend.models.views.incoming.PageRequestView;
import com.nakamas.hatfieldbackend.models.views.incoming.filters.TicketFilter;
import com.nakamas.hatfieldbackend.models.views.outgoing.PageView;
import com.nakamas.hatfieldbackend.models.views.outgoing.ticket.TicketView;
import com.nakamas.hatfieldbackend.repositories.*;
import com.nakamas.hatfieldbackend.services.*;
import com.nakamas.hatfieldbackend.services.communication.sms.api.SmsClient;
import com.nakamas.hatfieldbackend.services.communication.sms.models.SmsApiResponse;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

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
    @MockBean
    private EmailService emailService;
    @MockBean
    private SmsClient smsClient;
    @MockBean
    private SmsService smsService;

    private Shop shop;
    private User user;
    private User client;
    private Ticket ticket;
    private List<InventoryItem> items;

    Logger log = LoggerFactory.getLogger(TicketTests.class);

    @BeforeEach
    void setUp() {
        log.info("### Test setup initiated ###");
        doNothing().when(emailService).sendMail(any(), any(), any());
        when(smsClient.sendMessage(any(),any())).thenReturn(new SmsApiResponse("1234","accepted", LocalDateTime.now()));
        when(smsService.sendSms(any(),any(),any())).thenReturn(true);

        Shop testShop = TestData.getTestShop();
        shop = shopRepository.save(testShop);
        user = userService.createUser(TestData.getTestUser(shop));
        client = userService.createUser(TestData.getTestUser("client", "client@email.com", UserRole.CLIENT, shop));
        ticket = ticketService.createTicket(TestData.getTestTicket(client), user);
        Category category = categoryRepository.save(TestData.getCategory());
        for (int i = 0; i < 3; i++)
            inventoryItemService.createInventoryItem(TestData.getTestInventoryItem(shop, category));
        items = inventoryItemRepository.findAll();
        log.info("### Test setup completed ###");
}

    @AfterEach
    void tearDown() {
        log.info("### Test teardown initiated ###");
        ticketRepository.deleteAll();
        userRepository.deleteAll();
        inventoryItemRepository.deleteAll();
        categoryRepository.deleteAll();
        modelRepository.deleteAll();
        brandRepository.deleteAll();
        shopRepository.deleteAll();
        log.info("### Test teardown completed ###");
    }

    @Test
    @Transactional
    void create_ticket() {
        CreateTicket createTicket = TestData.getTestTicket(client);
        Ticket ticket = ticketService.createTicket(createTicket, user);
        Assertions.assertEquals(user.getShop().getId(), ticket.getShop().getId());
        Assertions.assertEquals(createTicket.deviceLocation(), ticket.getDeviceLocation().getLocation());
        Assertions.assertEquals(createTicket.deviceBrand(), ticket.getDeviceBrandString());
    }
    @Test
    @Transactional
    void search_ticket() {
        CreateTicket createTicket = TestData.getTestTicket(client);
        TicketFilter ticketFilter = new TicketFilter();
        ticketFilter.setSearchBy(createTicket.problemExplanation());
        PageView<TicketView> all = ticketService.findAll(ticketFilter, new PageRequestView(10, 1));
        Assertions.assertEquals(1, all.getTotalCount());
    }


    @Test
    @Transactional
    void update_ticket_should_succeed() {
        CreateTicket createTicket = TestData.getTestTicket(client);
        Ticket result = ticketService.update(createTicket, ticket.getId());
        Assertions.assertEquals(ticket.getId(), result.getId());
        Assertions.assertEquals(user.getShop().getId(), result.getShop().getId());
        Assertions.assertEquals(createTicket.deviceLocation(), result.getDeviceLocation().getLocation());
        Assertions.assertEquals(createTicket.deviceBrand(), result.getDeviceBrandString());
    }

    @Test
    @Transactional
    void filter_tickets_by_createdBy() {
        CreateTicket createTicket = TestData.getTestTicket(client);
        User user2 = userService.createUser(TestData.getTestUser("salesman", "salesman@email.com", UserRole.SALESMAN, shop));

        ticketService.createTicket(createTicket, user);
        ticketService.createTicket(createTicket, user2);
        TicketFilter ticketFilter = new TicketFilter();
        ticketFilter.setCreatedById(user.getId());
        PageView<TicketView> filtered = ticketService.findAll(ticketFilter, new PageRequestView(10, 1));
        PageView<TicketView> all = ticketService.findAll(new TicketFilter(), new PageRequestView(10, 1));

        Assertions.assertEquals(2, filtered.getTotalCount());
        Assertions.assertEquals(3, all.getTotalCount());
    }

    @Test
    @Transactional
    void add_item_from_inventory() {
        CreateTicket createTicket = TestData.getTestTicket(client);
        Ticket ticket = ticketService.createTicket(createTicket, user);
        ticket = ticketService.usePartFromInventory(ticket.getId(), items.get(0).getId(), 1);

        Assertions.assertEquals(1, ticket.getUsedParts().size());
        Assertions.assertEquals(1, usedPartRepository.count());
    }

    @Test
    @Transactional
    void start_ticket_repair_should_succeed() {
        ticketService.startRepair(user, ticket.getId());
        Assertions.assertEquals(ticketService.getTicket(ticket.getId()).getStatus(), TicketStatus.STARTED);
    }

    @Test
    @Transactional
    void complete_ticket_repair_should_succeed() {
        ticketService.completeRepair(user, ticket.getId(), true);
        Assertions.assertEquals(ticketService.getTicket(ticket.getId()).getStatus(), TicketStatus.FINISHED);
    }

}
