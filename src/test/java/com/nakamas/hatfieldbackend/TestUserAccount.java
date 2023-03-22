package com.nakamas.hatfieldbackend;

import com.nakamas.hatfieldbackend.config.exception.CustomException;
import com.nakamas.hatfieldbackend.models.entities.Photo;
import com.nakamas.hatfieldbackend.models.entities.User;
import com.nakamas.hatfieldbackend.models.entities.shop.Shop;
import com.nakamas.hatfieldbackend.models.enums.UserRole;
import com.nakamas.hatfieldbackend.models.views.incoming.CreateUser;
import com.nakamas.hatfieldbackend.models.views.incoming.filters.UserFilter;
import com.nakamas.hatfieldbackend.models.views.outgoing.user.CreatedClientInfo;
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
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;

import static com.nakamas.hatfieldbackend.TestData.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
        shopRepository.deleteAll();
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
                () -> createSecondUser(correctUsername, "exmaple@email.com"));
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
        UserFilter filter = new UserFilter();
        filter.setSearchBy("new username");
        assertEquals(0, userService.getAllWorkers(filter).size());
    }

    @Test
    void get_workers_should_succeed() {
        createClient("new username", "newEmail@gmail.com");
        createSecondUser("new username 2", "newEmail2@gmail.com");
        UserFilter filter = new UserFilter();
        filter.setSearchBy("username");

        assertEquals(1, userService.getAllWorkers(filter).size());
    }

    @Test
    @Transactional
    void ban_client_and_filter() {
        CreatedClientInfo client = createClient("new username 2", "newEmail2@gmail.com");

        userService.updateUserBan(client.profile().userId(), true);
        UserFilter filter = new UserFilter();
        filter.setBanned(true);
        List<User> all = userService.getAllClients(filter);

        Assertions.assertTrue(all.stream().anyMatch(user ->
                Objects.equals(user.getId(), client.profile().userId())));
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

    @Test
    public void testGetUserImage() throws Exception {
        byte[] imageData = "test".getBytes();
        registeredUser.setImage(new Photo(imageData, false));
        userService.updateUserImage(registeredUser, new MockMultipartFile("image", imageData));

        MockHttpServletResponse response = new MockHttpServletResponse();
        userService.getUserImage(registeredUser.getId(), response);
        InputStream responseStream = new ByteArrayInputStream(response.getContentAsByteArray());
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = responseStream.read(buffer)) > 0) {
            outputStream.write(buffer, 0, length);
        }
        assertArrayEquals(imageData, outputStream.toByteArray());
    }

    @Test
    public void testUpdateUserImage() throws Exception {
        // Create a mock image file
        byte[] imageData = "test".getBytes();
        MultipartFile imageFile = mock(MultipartFile.class);
        when(imageFile.getBytes()).thenReturn(imageData);

        // Call the updateUserImage method and assert that the registeredUser's image was updated
        userService.updateUserImage(registeredUser, imageFile);

        assertNotNull(registeredUser.getImage());
        assertArrayEquals(imageData, registeredUser.getImage().getData());
    }

    private User createSecondUser(String username, String email) {
        return userService.createUser(new CreateUser(null, username, "new user",
                correctPassword, UserRole.ENGINEER, email, null, registeredUser.getShop().getId()));
    }

    private CreatedClientInfo createClient(String username, String email) {
        return userService.createClient(getTestUser(username, email, UserRole.CLIENT, registeredUser.getShop()));
    }
}
