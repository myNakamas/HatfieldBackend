package com.nakamas.hatfieldbackend;


import com.nakamas.hatfieldbackend.models.entities.User;
import com.nakamas.hatfieldbackend.models.entities.shop.Shop;
import com.nakamas.hatfieldbackend.repositories.ShopRepository;
import com.nakamas.hatfieldbackend.repositories.UserRepository;
import com.nakamas.hatfieldbackend.services.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static com.nakamas.hatfieldbackend.TestData.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc()
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY, connection = EmbeddedDatabaseConnection.H2)
class AuthTest {

    @Autowired
    private UserService userService;
    @Autowired
    private ShopRepository shopRepository;
    @Autowired
    private UserRepository userRepo;
    @Autowired
    private WebApplicationContext context;

    User correctUser;
    private MockMvc mvc;


    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
        Shop shop = shopRepository.save(getTestShop());
        correctUser = userService.createUser(getTestUser(shop));
    }

    @AfterEach
    void tearDown() {
        userRepo.deleteAll();
        logout();
    }

    @Test
    void noAccess() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                        .post("/api/user/admin/create").secure(true).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void login() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                        .post("/api/login").secure(true).contentType(MediaType.APPLICATION_JSON)
                        .param("username", correctUser.getUsername())
                        .param("password", correctPassword))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void falseLogin() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                        .post("/api/login").secure(true).contentType(MediaType.APPLICATION_JSON)
                        .param("username", correctUser.getUsername())
                        .param("password", "incorrect"))
                .andExpect(status().isUnauthorized());
    }

    private void logout() {
        SecurityContextHolder.getContext().setAuthentication(null);
    }
}