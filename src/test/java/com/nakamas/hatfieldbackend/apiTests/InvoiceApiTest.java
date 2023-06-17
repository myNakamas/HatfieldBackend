package com.nakamas.hatfieldbackend.apiTests;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.nakamas.hatfieldbackend.models.entities.User;
import com.nakamas.hatfieldbackend.models.entities.shop.Shop;
import com.nakamas.hatfieldbackend.models.entities.ticket.Invoice;
import com.nakamas.hatfieldbackend.models.entities.ticket.Ticket;
import com.nakamas.hatfieldbackend.models.enums.UserRole;
import com.nakamas.hatfieldbackend.models.views.incoming.CreateInvoice;
import com.nakamas.hatfieldbackend.repositories.InvoiceRepository;
import com.nakamas.hatfieldbackend.repositories.ShopRepository;
import com.nakamas.hatfieldbackend.repositories.TicketRepository;
import com.nakamas.hatfieldbackend.repositories.UserRepository;
import com.nakamas.hatfieldbackend.services.UserService;
import com.nakamas.hatfieldbackend.util.JwtUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Optional;

import static com.nakamas.hatfieldbackend.TestData.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc()
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY, connection = EmbeddedDatabaseConnection.H2)
class InvoiceApiTest {
    User correctUser;
    User client;
    Shop shop;
    @Autowired
    private TicketRepository ticketRepository;
    @Autowired
    private InvoiceRepository invoiceRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private ShopRepository shopRepository;
    @Autowired
    private UserRepository userRepo;
    @Autowired
    private WebApplicationContext context;
    @Autowired
    private ResourceLoader resourceLoader;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private JwtUtil jwtUtil;
    private MockMvc mvc;


    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
        shop = shopRepository.save(getTestShop());
        correctUser = userService.createUser(getTestUser("worker", "worker@email.com", UserRole.ENGINEER, shop));
        client = userService.createClient(getTestUser("client", "client@email.com", UserRole.CLIENT, shop));
    }

    @AfterEach
    void tearDown() {
        invoiceRepository.deleteAll();
        ticketRepository.deleteAll();
        userRepo.deleteAll();
    }

    @Test
    void createInvoiceTest() throws Exception {
        Resource resource = resourceLoader.getResource("classpath:json/invoiceBodies.json");
        Ticket save = ticketRepository.save(new Ticket(getTestTicket(client), correctUser));
        CreateInvoice[] createInvoices = objectMapper.readValue(resource.getInputStream(), CreateInvoice[].class);
        CreateInvoice testInvoice = createInvoices[0];
        testInvoice.setTicketId(save.getId());
        testInvoice.setClientId(client.getId());
        mvc.perform(MockMvcRequestBuilders
                        .post("/api/invoice/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createInvoices[0]))
                        .header("Authorization", "Bearer " + jwtUtil.encode(correctUser)))
                .andExpect(status().is2xxSuccessful()).andReturn();

        Optional<Invoice> one = invoiceRepository.findAll().stream().findFirst();
        Assertions.assertTrue(one.isPresent());
    }
}