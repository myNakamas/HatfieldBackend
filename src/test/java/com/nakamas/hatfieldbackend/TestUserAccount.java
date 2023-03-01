package com.nakamas.hatfieldbackend;

import com.nakamas.hatfieldbackend.config.exception.CustomException;
import com.nakamas.hatfieldbackend.models.entities.User;
import com.nakamas.hatfieldbackend.models.entities.shop.Shop;
import com.nakamas.hatfieldbackend.models.enums.UserRole;
import com.nakamas.hatfieldbackend.models.views.incoming.CreateUser;
import com.nakamas.hatfieldbackend.repositories.ShopRepository;
import com.nakamas.hatfieldbackend.repositories.UserRepository;
import com.nakamas.hatfieldbackend.services.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

import static com.nakamas.hatfieldbackend.TestData.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY, connection = EmbeddedDatabaseConnection.H2)
class TestUserAccount {

    @Autowired
    private UserService userService;
    @Autowired
    private ShopRepository shopRepository;
    @Autowired
    private UserRepository userRepo;
    @Autowired
    private PasswordEncoder passwordEncoder;

    User correctUser;

    @BeforeEach
    void setUp() {
        Shop shop = shopRepository.save(getTestShop());
        correctUser = userService.createUser(getTestUser(shop));
    }

    @AfterEach
    void tearDown() {
        userRepo.deleteAll();
    }

    @Test
    void load_user_by_username_should_fail(){
        assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername("fakeName"));
    }

    @Test
    void load_user_by_username_should_succeed(){
        assertEquals(userService.loadUserByUsername(correctUsername), correctUser);
    }

    @Test
    void change_password_should_fail(){
        assertThrows(CustomException.class,
                () -> userService.changePassword(correctUser, "incorrectPassword", "newPassword"));
    }

    @Test
    void change_password_should_succeed(){
        userService.changePassword(correctUser, correctPassword, "newPassword");
        UserDetails temp = userService.loadUserByUsername(correctUsername);
        assertTrue(passwordEncoder.matches("newPassword", temp.getPassword()));
    }

    @Test
    void create_user_existing_username_should_fail(){
        assertThrows(DataIntegrityViolationException.class,
                () -> userService.createUser(new CreateUser(null, correctUsername, "new user",
                        correctPassword, UserRole.ENGINEER,"exmaple@email.com", null, correctUser.getShop().getId())));
    }

    @Test
    void create_user_existing_email_should_fail(){
        assertThrows(DataIntegrityViolationException.class,
                () -> userService.createUser(new CreateUser(null, "new username", "new user",
                        correctPassword, UserRole.ENGINEER,correctUser.getEmail(), null, correctUser.getShop().getId())));
    }

    @Test
    void create_user_should_succeed(){
        assertDoesNotThrow(() ->userService.createUser(new CreateUser(null, "new username", "new user",
                correctPassword, UserRole.ENGINEER,"newEmail@gmail.com", null, correctUser.getShop().getId())));
    }

    @Test
    void create_client_existing_username_should_fail(){
        assertThrows(DataIntegrityViolationException.class,
                () -> userService.createClient(new CreateUser(null, correctUsername, "new user",
                        correctPassword, UserRole.ENGINEER,"exmaple@email.com", null, correctUser.getShop().getId())));
    }

    @Test
    void create_client_existing_email_should_fail(){
        assertThrows(DataIntegrityViolationException.class,
                () -> userService.createClient(new CreateUser(null, "new username", "new user",
                        correctPassword, UserRole.ENGINEER,correctUser.getEmail(), null, correctUser.getShop().getId())));
    }

    @Test
    void create_client_should_succeed(){
        assertDoesNotThrow(() ->userService.createClient(new CreateUser(null, "new username", "new user",
                correctPassword, UserRole.ENGINEER,"newEmail@gmail.com", null, correctUser.getShop().getId())));
    }

    @Test
    void get_workers_should_fail(){
        userService.createClient(new CreateUser(null, "new username", "new user",
                correctPassword, UserRole.ENGINEER,"newEmail@gmail.com", null, correctUser.getShop().getId()));
        assertEquals(0, userService.getAllWorkers("new username").size());
    }

    @Test
    void get_workers_should_succeed(){
        userService.createClient(new CreateUser(null, "new username", "new user",
                correctPassword, UserRole.ENGINEER,"newEmail@gmail.com", null, correctUser.getShop().getId()));
        userService.createUser(new CreateUser(null, "new username 2", "new user",
                correctPassword, UserRole.ENGINEER,"newEmail2@gmail.com", null, correctUser.getShop().getId()));
        assertEquals(1, userService.getAllWorkers("username").size());
    }



}
