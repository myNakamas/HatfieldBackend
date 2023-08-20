package com.nakamas.hatfieldbackend.services;

import com.nakamas.hatfieldbackend.config.exception.CustomException;
import com.nakamas.hatfieldbackend.models.entities.Log;
import com.nakamas.hatfieldbackend.models.entities.Photo;
import com.nakamas.hatfieldbackend.models.entities.User;
import com.nakamas.hatfieldbackend.models.entities.shop.ShopSettings;
import com.nakamas.hatfieldbackend.models.enums.LogType;
import com.nakamas.hatfieldbackend.models.enums.UserRole;
import com.nakamas.hatfieldbackend.models.views.incoming.CreateUser;
import com.nakamas.hatfieldbackend.models.views.incoming.filters.UserFilter;
import com.nakamas.hatfieldbackend.models.views.outgoing.ResponseMessage;
import com.nakamas.hatfieldbackend.repositories.PhotoRepository;
import com.nakamas.hatfieldbackend.repositories.ShopRepository;
import com.nakamas.hatfieldbackend.repositories.UserRepository;
import com.nakamas.hatfieldbackend.util.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService, UserDetailsPasswordService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final PhotoRepository photoRepository;
    private final ShopRepository shopRepository;
    private final LoggerService loggerService;
    private final EmailService emailService;
    private final TemplateEngine templateEngine;
    @Value(value = "${fe-host:http://localhost:5173}")
    private String frontendHost;
    private final JwtUtil jwtUtil;

    public User getUser(UUID id) {
        return userRepository.findById(id).orElseThrow(() -> new CustomException("User does not exist"));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findUser(username).orElseThrow(() -> new UsernameNotFoundException("User does not exist"));
    }

    @Override
    public UserDetails updatePassword(UserDetails userDetails, String newPassword) {
        User user = (User) userDetails;
        user.setPassword(newPassword);
        validateAndSave(user);
        return user;
    }

    public void clientUpdatePassword(User user, String newPassword) {
        user.setPassword(passwordEncoder.encode(newPassword));
        validateAndSave(user);
    }

    public void changePassword(User user, String oldPassword, String newPassword) {
        if (!passwordEncoder.matches(oldPassword, user.getPassword()))
            throw new CustomException("Incorrect old password!");
        updatePassword(user, passwordEncoder.encode(newPassword));
    }

    // admin changing the settings of other users
    @Transactional
    public void updateUserBan(UUID id, Boolean status) {
        if (status) {
            loggerService.createLog(new Log(LogType.BANNED_USER), id.toString());
        } else {
            loggerService.createLog(new Log(LogType.UNBANNED_USER), id.toString());
        }
        userRepository.setBanned(id, status);
    }

    public void updateUserActivity(UUID id, Boolean status) {
        updateUserActivity(getUser(id), status);
    }

    //user "deleting" his account
    public void updateUserActivity(User user, Boolean status) {
        if (!status) {
            loggerService.createLog(new Log(LogType.DELETED_USER), user.getFullName());
        } else {
            loggerService.createLog(new Log(LogType.RESTORED_USER), user.getFullName());
        }
        user.setIsActive(status);
        userRepository.save(user);
    }

    public User createUser(CreateUser userInfo) {
        User user = new User(userInfo, shopRepository.findById(userInfo.shopId()).orElse(null));
        if (Objects.equals(userInfo.role(), UserRole.CLIENT)) {
            return createClient(userInfo);
        } else {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setEmailPermission(false);
            user.setSmsPermission(false);
            return validateAndSave(user);
        }
    }

    public User createClient(CreateUser userInfo) {
        User user = new User(userInfo, shopRepository.findById(userInfo.shopId()).orElse(null));
        user.setRole(UserRole.CLIENT);
        user.setUsername(generateUsername());
        String newPass = generatePass();
        user.setFirstPassword(newPass);
        user.setPassword(passwordEncoder.encode(newPass));
        return validateAndSave(user);
    }

    /**
     * Allows the admin to change whoever he decides.
     */
    public User updateUser(CreateUser userInfo) {
        User user = getUser(userInfo.userId());
        String updateInfo = loggerService.userUpdateCheck(user, userInfo);
        user.updateAsAdmin(userInfo, shopRepository.findById(userInfo.shopId()).orElse(user.getShop()));
        if (!userInfo.password().isBlank()) user.setPassword(passwordEncoder.encode(userInfo.password()));
        User endUser = validateAndSave(user);
        loggerService.createLog(new Log(LogType.UPDATED_USER), user.getFullName(), updateInfo);
        return endUser;
    }

    /**
     * Allows the user to make changes to itself.
     */
    public User updateUser(User user, CreateUser update) {
        String updateInfo = loggerService.userUpdateCheck(user, update);
        user.update(update);
        User endUser = validateAndSave(user);
        loggerService.createLog(new Log(LogType.UPDATED_USER), user.getFullName(), updateInfo);
        return endUser;
    }

    public List<User> getAll(UserFilter filter) {
        return userRepository.findAll(filter);
    }

    public List<User> getAllClients(UserFilter filter) {
        filter.setRoles(List.of(UserRole.CLIENT));
        return userRepository.findAll(filter);
    }

    public Page<User> getAllClients(UserFilter filter, PageRequest pageRequest) {
        filter.setRoles(List.of(UserRole.CLIENT));
        return userRepository.findAll(filter, pageRequest);
    }

    public List<User> getAllWorkers(UserFilter filter) {
        filter.setRoles(List.of(UserRole.ENGINEER, UserRole.SALESMAN));
        return userRepository.findAll(filter);
    }

    @Transactional
    public void getUserImage(UUID id, HttpServletResponse response) {
        User user = getUser(id);
        if (user.getImage() == null) return;
        try (InputStream userImage = new ByteArrayInputStream(user.getImage().getData())) {
            userImage.transferTo(response.getOutputStream());
        } catch (IOException e) {
            log.info(e.getMessage());
            throw new CustomException("Could not load profile image");
        }
    }

    @Transactional
    public void updateUserImage(User user, MultipartFile image) {
        try {
            Photo photo = photoRepository.save(new Photo(image.getBytes(), false));
            user.setImage(photo);
            loggerService.createLog(new Log(LogType.UPDATED_USER), user.getFullName(), "User updated their photo.");
            userRepository.save(user);
        } catch (IOException e) {
            e.printStackTrace();
            throw new CustomException("Could not save image to the server.");
        }
    }

    private User validateAndSave(User user) {
        if (user.getShop() == null) throw new CustomException("User must be attached to a shop!");
        List<User> existingUsers = userRepository.uniqueUserExists(user.getUsername(), user.getEmail());
        if (user.isNew() && existingUsers.size() > 0 ||
                existingUsers.stream().anyMatch(profile -> !Objects.equals(profile.getId(), user.getId())))
            throw new CustomException("Username or email already taken!");
        if (user.isNew()) {
            if (Objects.equals(user.getRole(), UserRole.CLIENT)) {
                loggerService.createLog(new Log(LogType.CREATED_CLIENT), user.getFullName());
            } else {
                loggerService.createLog(new Log(LogType.CREATED_WORKER), user.getFullName());
            }
        }
        return userRepository.save(user);
    }

    public String generateUsername() {
        String answer = UUID.randomUUID().toString().substring(24, 36).toUpperCase(Locale.ROOT);
        if (userRepository.findUser(answer).isPresent())
            answer = generateUsername();
        return answer;
    }

    public String generatePass() {
        return UUID.randomUUID().toString().substring(0, 7);
    }

    public ResponseMessage forgotPassword(String userInfo) {
        User user = userRepository.findUser(userInfo).orElseThrow(() -> new CustomException("There is no user with such username, email or phone!"));
        ShopSettings shopSettings = user.getShop().getSettings();
        if (user.isEmailEnabled()) {
            String messageBody = templateEngine.process("email/forgotPassword", getUserForgotPasswordContext(user));
            emailService.sendMail(user, messageBody, "Forgot password");
            return new ResponseMessage("email");
        }
        if (user.isSMSEnabled() && shopSettings.isSmsEnabled()) {
            return new ResponseMessage("phone");
        }
        throw new CustomException("The user or shop do not allow email and sms communication. Please contact us on " + user.getShop().getEmail() + " or " + user.getShop().getPhone() + " or come visit us in person at " + user.getShop().getAddress());
    }

    private Context getUserForgotPasswordContext(User user) {
        Context context = new Context();
        context.setVariables(Map.of("clientName", user.getFullName(),
                "shopName", user.getShop().getShopName(),
                "resetLink", "%s/login/update-password?token=%s".formatted(frontendHost, jwtUtil.encode(user))
        ));
        return context;
    }
}
