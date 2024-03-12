package com.nakamas.hatfieldbackend;

import com.nakamas.hatfieldbackend.config.exception.CustomException;
import com.nakamas.hatfieldbackend.models.entities.User;
import com.nakamas.hatfieldbackend.models.entities.shop.Shop;
import com.nakamas.hatfieldbackend.models.enums.UserRole;
import com.nakamas.hatfieldbackend.models.views.incoming.CreateUser;
import com.nakamas.hatfieldbackend.models.views.incoming.filters.UserFilter;
import com.nakamas.hatfieldbackend.repositories.ShopRepository;
import com.nakamas.hatfieldbackend.repositories.UserRepository;
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
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.nakamas.hatfieldbackend.TestData.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY, connection = EmbeddedDatabaseConnection.H2)
class TestUserAccount {

    @Autowired
    private UserService userService;
    @Autowired
    private ShopRepository shopRepository;
    @Autowired
    private UserRepository userRepo;

    private User registeredUser;

    @BeforeEach
    void setUp() {
        Shop shop = shopRepository.save(getTestShop());
        registeredUser = userService.createUser(getTestUser(shop));
    }

    @AfterEach
    void tearDown() {
        userRepo.deleteAll();
        shopRepository.deleteAll();
    }

    @Test
    void load_user_by_username_should_fail() {
        assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername("fakeName"));
    }

    @Test
    void load_user_by_username_should_succeed() {
        assertEquals(userService.loadUserByUsername(correctUsername), registeredUser);
    }

    //    Assert that it login works with either 0 or the country code.
    @Test
    void load_user_by_phones_should_succeed() {
        User existingUser = userService.createUser(new CreateUser(null, "new Username", "new user",
                correctPassword, UserRole.ENGINEER, "newemail@email.com", List.of("+359-898575932"), registeredUser.getShop().getId()));
        String userPhone = existingUser.getPhones().get(0).getPhoneWithCode();
        String phoneNum = userPhone.substring(userPhone.indexOf("-") + 1);
        assertEquals(userService.loadUserByUsername(userPhone), existingUser);
        assertEquals(userService.loadUserByUsername("0" + phoneNum), existingUser);
    }

    @Test
    void change_password_should_fail() {
        assertThrows(CustomException.class,
                () -> userService.changePassword(registeredUser, "incorrectPassword", "newPassword"));
    }

    @Test
    void create_user_existing_username_should_fail() {
        assertThrows(CustomException.class,
                () -> createSecondUser(correctUsername, "exmaple@email.com"));
    }

    @Test
    void create_user_existing_email_should_fail() {
        assertThrows(CustomException.class,
                () -> createSecondUser("new username", registeredUser.getEmail()));
    }

    @Test
    void create_user_existing_phones_should_fail() {
        User existingUser = userService.createUser(new CreateUser(null, "new Username", "new user",
                correctPassword, UserRole.ENGINEER, "newemail@email.com", List.of("+359-898575932"), registeredUser.getShop().getId()));
        List<String> phones = new ArrayList<>(existingUser.getPhonesString());
        assertThrows(CustomException.class,
                () -> userService.createUser(new CreateUser(null, "new Username", "new user",
                        correctPassword, UserRole.ENGINEER, "newemail@email.com", phones, existingUser.getShop().getId())));
    }

    //    Assert that phones will interfere, no matter the country code
    //    It is required so the users can log in with their localized phone number.
    //    For example "08.." instead of "+44..."
    @Test
    void create_user_existing_phones_different_format_should_fail() {
        User existingUser = userService.createUser(new CreateUser(null, "new Username", "new user",
                correctPassword, UserRole.ENGINEER, "newemail@email.com", List.of("+359-898575932"), registeredUser.getShop().getId()));
        List<String> phones = new ArrayList<>(existingUser.getPhonesString().stream().map(s -> 0 + s.substring(s.indexOf("-") + 1)).toList());
        assertThrows(CustomException.class,
                () -> userService.createUser(new CreateUser(null, "new Username", "new user",
                        correctPassword, UserRole.ENGINEER, "newemail@email.com", phones, existingUser.getShop().getId())));
    }

    @Test
    void create_user_should_succeed() {
        assertDoesNotThrow(() -> createSecondUser("new username", "newEmail@gmail.com"));
    }

    @Test
    void create_client_existing_username_should_fail() {
        assertThrows(CustomException.class,
                () -> createSecondUser(correctUsername, "exmaple@email.com"));
    }

    @Test
    void create_client_existing_email_should_fail() {
        assertThrows(CustomException.class,
                () -> createClient("new username", registeredUser.getEmail()));
    }

    @Test
    void create_client_should_succeed() {
        assertDoesNotThrow(() -> createClient("new username", "newEmail@gmail.com"));
    }

    @Test
    @Transactional
    void update_user_as_admin_existing_username_should_fail() {
        User user = createSecondUser("new username", "exmaple@email.com");
        assertThrows(CustomException.class,
                () -> userService.updateUser(new CreateUser(user.getId(), correctUsername, "new user",
                        correctPassword, UserRole.ENGINEER, "exmaple@email.com", null, registeredUser.getShop().getId())));
    }

    @Test
    @Transactional
    void update_user_as_admin_existing_email_should_fail() {
        User user = createSecondUser("new username", "exmaple@email.com");
        assertThrows(CustomException.class,
                () -> userService.updateUser(new CreateUser(user.getId(), "new username", "new user",
                        correctPassword, UserRole.ENGINEER, registeredUser.getEmail(), null, registeredUser.getShop().getId())));
    }

    @Test
    @Transactional
    void update_user_as_admin_should_succeed() {
        User user = createSecondUser("new username", "exmaple@email.com");
        assertDoesNotThrow(() -> userService.updateUser(new CreateUser(user.getId(), "new new username", "new user",
                correctPassword, UserRole.ENGINEER, "newNewEmail@gmail.com", null, registeredUser.getShop().getId())));
    }

    @Test
    @Transactional
    void update_user_existing_username_should_fail() {
        User user = createSecondUser("new username", "exmaple@email.com");
        assertThrows(CustomException.class,
                () -> userService.updateUser(user, new CreateUser(user.getId(), correctUsername, "new user",
                        correctPassword, UserRole.ENGINEER, "exmaple@email.com", null, registeredUser.getShop().getId())));
    }

    @Test
    @Transactional
    void update_user_existing_email_should_fail() {
        User user = createSecondUser("new username", "exmaple@email.com");
        assertThrows(CustomException.class,
                () -> userService.updateUser(user, new CreateUser(user.getId(), "new username", "new user",
                        correctPassword, UserRole.ENGINEER, registeredUser.getEmail(), null, registeredUser.getShop().getId())));
    }

    @Test
    @Transactional
    void update_user_should_succeed() {
        User user = createSecondUser("new username", "exmaple@email.com");
        assertDoesNotThrow(() -> userService.updateUser(user, new CreateUser(user.getId(), "new new username", "new user",
                correctPassword, UserRole.ENGINEER, "newNewEmail@gmail.com", null, registeredUser.getShop().getId())));
    }

    @Test
    void get_workers_should_fail() {
        createClient("new username", "newEmail@gmail.com");
        UserFilter filter = new UserFilter();
        assertEquals(0, userService.getAllWorkers(filter).size());
    }

    @Test
    @Transactional
    void get_clients_with_filter() {
        createClient("new username", "newEmail@gmail.com");
        UserFilter filter = new UserFilter();
        filter.setEmail("newEmail@gmail.com");
        assertEquals(1, userService.getFilteredClients(filter).size());
    }

    @Test
    void get_workers_should_succeed() {
        createClient("new username", "newEmail@gmail.com");
        createSecondUser("new username 2", "newEmail2@gmail.com");
        UserFilter filter = new UserFilter();

        assertEquals(1, userService.getAllWorkers(filter).size());
    }

    @Test
    @Transactional
    void ban_client_and_filter() {
        User client = createClient("new username 2", "newEmail2@gmail.com");

        userService.updateUserBan(client.getId(), true);
        UserFilter filter = new UserFilter();
        filter.setBanned(true);
        List<User> all = userService.getAllClients(filter);

        Assertions.assertTrue(all.stream().anyMatch(user ->
                Objects.equals(user.getId(), client.getId())));
    }

    @Test
    @Transactional
    void user_set_inactive_and_filter() {
        User secondUser = createSecondUser("new username 2", "newEmail2@gmail.com");

        userService.updateUserActivity(secondUser.getId(), false);
        UserFilter filter = new UserFilter();
        filter.setActive(false);
        List<User> all = userService.getAll(filter);

        Assertions.assertTrue(all.contains(secondUser));
    }

//    @Test
//    public void testGetUserImage() throws Exception {
//        byte[] imageData = "test".getBytes();
//
//        registeredUser.setImage(new Photo(imageData, false));
//        userService.updateUserImage(registeredUser, new MockMultipartFile("image", imageData));
//
//        MockHttpServletResponse response = new MockHttpServletResponse();
//        userService.getUserImage(registeredUser.getId(), response);
//        InputStream responseStream = new ByteArrayInputStream(response.getContentAsByteArray());
//        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//        byte[] buffer = new byte[1024];
//        int length;
//        while ((length = responseStream.read(buffer)) > 0) {
//            outputStream.write(buffer, 0, length);
//        }
//        assertArrayEquals(imageData, outputStream.toByteArray());
//    }
//
//    @Test
//    public void testUpdateUserImage() throws Exception {
//        // Create a mock image file
//        byte[] imageData = "test".getBytes();
//        MultipartFile imageFile = mock(MultipartFile.class);
//        when(imageFile.getBytes()).thenReturn(imageData);
//
//        // Call the updateUserImage method and assert that the registeredUser's image was updated
//        userService.updateUserImage(registeredUser, imageFile);
//
//        assertNotNull(registeredUser.getImage());
//        assertArrayEquals(imageData, registeredUser.getImage().getData());
//    }

    private User createSecondUser(String username, String email) {
        return userService.createUser(new CreateUser(null, username, "new user",
                correctPassword, UserRole.ENGINEER, email, new ArrayList<>(), registeredUser.getShop().getId()));
    }

    private User createClient(String username, String email) {
        return userService.createClient(getTestUser(username, email, UserRole.CLIENT, registeredUser.getShop()));
    }
}
