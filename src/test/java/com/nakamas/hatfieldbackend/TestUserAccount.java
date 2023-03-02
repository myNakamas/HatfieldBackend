package com.nakamas.hatfieldbackend;

import com.nakamas.hatfieldbackend.config.exception.CustomException;
import com.nakamas.hatfieldbackend.models.entities.User;
import com.nakamas.hatfieldbackend.models.entities.shop.Shop;
import com.nakamas.hatfieldbackend.models.enums.UserRole;
import com.nakamas.hatfieldbackend.models.views.incoming.CreateUser;
import com.nakamas.hatfieldbackend.repositories.ShopRepository;
import com.nakamas.hatfieldbackend.repositories.UserRepository;
import com.nakamas.hatfieldbackend.services.UserService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
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

    private User registeredUser;

    @BeforeEach
    void setUp() {
        Shop shop = shopRepository.save(getTestShop());
        registeredUser = userService.createUser(getTestUser(shop));
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
        assertEquals(userService.loadUserByUsername(correctUsername), registeredUser);
    }

    @Test
    void change_password_should_fail(){
        assertThrows(CustomException.class,
                () -> userService.changePassword(registeredUser, "incorrectPassword", "newPassword"));
    }

    @Test
    void change_password_should_succeed(){
        userService.changePassword(registeredUser, correctPassword, "newPassword");
        UserDetails temp = userService.loadUserByUsername(correctUsername);
        assertTrue(passwordEncoder.matches("newPassword", temp.getPassword()));
    }

    @Test
    void create_user_existing_username_should_fail(){
        assertThrows(CustomException.class,
                () -> createSecondUser(correctUsername, "exmaple@email.com"));
    }

    @Test
    void create_user_existing_email_should_fail(){
        assertThrows(CustomException.class,
                () ->  createSecondUser("new username", registeredUser.getEmail()));
    }

    @Test
    void create_user_should_succeed(){
        assertDoesNotThrow(() -> createSecondUser("new username", "newEmail@gmail.com"));
    }

    @Test
    void create_client_existing_username_should_fail(){
        assertThrows(CustomException.class,
                () -> createClient(correctUsername, "exmaple@email.com"));
    }

    @Test
    void create_client_existing_email_should_fail(){
        assertThrows(CustomException.class,
                () -> createClient("new username", registeredUser.getEmail()));
    }

    @Test
    void create_client_should_succeed(){
        assertDoesNotThrow(() -> createClient("new username", "newEmail@gmail.com"));
    }

    @Test
    @Transactional
    void update_user_as_admin_existing_username_should_fail(){
        User user = createSecondUser("new username", "exmaple@email.com");
        assertThrows(CustomException.class,
                () -> userService.updateUser(new CreateUser(user.getId(), correctUsername, "new user",
                        correctPassword, UserRole.ENGINEER,"exmaple@email.com", null, registeredUser.getShop().getId())));
    }

    @Test
    @Transactional
    void update_user_as_admin_existing_email_should_fail(){
        User user = createSecondUser("new username", "exmaple@email.com");
        assertThrows(CustomException.class,
                () -> userService.updateUser(new CreateUser(user.getId(), "new username", "new user",
                        correctPassword, UserRole.ENGINEER, registeredUser.getEmail(), null, registeredUser.getShop().getId())));
    }

    @Test
    @Transactional
    void update_user_as_admin_should_succeed(){
        User user = createSecondUser("new username", "exmaple@email.com");
        assertDoesNotThrow(() ->userService.updateUser(new CreateUser(user.getId(), "new new username", "new user",
                correctPassword, UserRole.ENGINEER,"newNewEmail@gmail.com", null, registeredUser.getShop().getId())));
    }

    @Test
    @Transactional
    void update_user_existing_username_should_fail(){
        User user = createSecondUser("new username", "exmaple@email.com");
        assertThrows(CustomException.class,
                () -> userService.updateUser(user, new CreateUser(user.getId(), correctUsername, "new user",
                        correctPassword, UserRole.ENGINEER,"exmaple@email.com", null, registeredUser.getShop().getId())));
    }

    @Test
    @Transactional
    void update_user_existing_email_should_fail(){
        User user = createSecondUser("new username", "exmaple@email.com");
        assertThrows(CustomException.class,
                () -> userService.updateUser(user, new CreateUser(user.getId(), "new username", "new user",
                        correctPassword, UserRole.ENGINEER, registeredUser.getEmail(), null, registeredUser.getShop().getId())));
    }

    @Test
    @Transactional
    void update_user_should_succeed(){
        User user = createSecondUser("new username", "exmaple@email.com");
        assertDoesNotThrow(() ->userService.updateUser(user, new CreateUser(user.getId(), "new new username", "new user",
                correctPassword, UserRole.ENGINEER,"newNewEmail@gmail.com", null, registeredUser.getShop().getId())));
    }

    @Test
    void get_workers_should_fail(){
        createClient("new username", "newEmail@gmail.com");
        assertEquals(0, userService.getAllWorkers("new username").size());
    }

    @Test
    void get_workers_should_succeed(){
        createClient("new username", "newEmail@gmail.com");
        createSecondUser("new username 2","newEmail2@gmail.com");
        assertEquals(1, userService.getAllWorkers("username").size());
    }

    private User createSecondUser(String username, String email){
        return userService.createUser(new CreateUser(null, username, "new user",
                correctPassword, UserRole.ENGINEER, email, null, registeredUser.getShop().getId()));
    }
    private User createClient(String username, String email){
        return userService.createClient(new CreateUser(null, username, "new user",
                correctPassword, UserRole.ENGINEER, email, null, registeredUser.getShop().getId()));
    }
}
