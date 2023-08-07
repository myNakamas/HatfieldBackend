package com.nakamas.hatfieldbackend.services;

import com.nakamas.hatfieldbackend.TestData;
import com.nakamas.hatfieldbackend.models.entities.User;
import com.nakamas.hatfieldbackend.models.entities.shop.Shop;
import com.nakamas.hatfieldbackend.models.entities.ticket.Ticket;
import com.nakamas.hatfieldbackend.models.enums.UserRole;
import com.nakamas.hatfieldbackend.repositories.ShopRepository;
import com.nakamas.hatfieldbackend.services.communication.sms.api.SmsClient;
import com.nakamas.hatfieldbackend.services.communication.sms.models.SmsApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY, connection = EmbeddedDatabaseConnection.H2)
class SmsServiceTest {
    @Autowired
    private SmsService smsService;
    @Autowired
    private ShopRepository shopRepository;
    @Autowired
    private TicketService ticketService;
    @Autowired
    private UserService userService;
    @MockBean
    private SmsClient smsClient;
    private User user;
    private Ticket ticket;

    @BeforeEach
    void setUp() {
        Shop testShop = TestData.getTestShop();
        testShop.getSettings().setSmsEnabled(true);
        Shop shop = shopRepository.save(testShop);
        user = userService.createUser(TestData.getTestUser("usernameTest", "username@test.com", UserRole.ENGINEER, shop));
        ticket = ticketService.createTicket(TestData.getTestTicket(user), user);
        when(smsClient.sendMessage(any(),any())).thenReturn(new SmsApiResponse("1234","accepted", LocalDateTime.now()));
    }
    @Test
    void sendSms() {
        user.setSmsPermission(true);
        smsService.sendSms(user,"ticketCompleted.txt", ticketService.getTicketContext(ticket));
        verify(smsClient,times(1)).sendMessage(any(),any());
    }
}