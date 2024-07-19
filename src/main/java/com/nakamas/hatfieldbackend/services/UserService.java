package com.nakamas.hatfieldbackend.services;

import com.nakamas.hatfieldbackend.config.exception.CustomException;
import com.nakamas.hatfieldbackend.models.entities.Log;
import com.nakamas.hatfieldbackend.models.entities.Photo;
import com.nakamas.hatfieldbackend.models.entities.User;
import com.nakamas.hatfieldbackend.models.entities.UserPhone;
import com.nakamas.hatfieldbackend.models.enums.LogType;
import com.nakamas.hatfieldbackend.models.enums.UserRole;
import com.nakamas.hatfieldbackend.models.views.incoming.CreateUser;
import com.nakamas.hatfieldbackend.models.views.incoming.filters.UserFilter;
import com.nakamas.hatfieldbackend.models.views.outgoing.ResponseMessage;
import com.nakamas.hatfieldbackend.models.views.outgoing.user.UserAndPhone;
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
import org.springframework.mail.MailAuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService, UserDetailsPasswordService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final PhotoService photoService;
    private final ShopRepository shopRepository;
    private final LoggerService loggerService;
    private final EmailService emailService;
    private final SmsService smsService;
    private final TemplateEngine templateEngine;
    @Value(value = "${fe-host:http://localhost:5173}")
    private String frontendHost;
    private final JwtUtil jwtUtil;

    public User getUser(UUID id) {
        return userRepository.findById(id).orElseThrow(() -> new CustomException("User does not exist"));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userByUsernameOrEmail = userRepository.findUser(username);
        if (userByUsernameOrEmail.isPresent())
            return userByUsernameOrEmail.get();

        String phone = UserPhone.extractPhoneNumber(username);
        return userRepository.findUserByPhone(phone).orElseThrow(() -> new UsernameNotFoundException("User does not exist"));
    }


    @Override
    public UserDetails updatePassword(UserDetails userDetails, String newPassword) {
        User user = (User) userDetails;
        user.setPassword(newPassword);
        userRepository.save(user);
        return user;
    }

    public void clientUpdatePassword(User user, String newPassword) {
        validatePassword(newPassword);
        updatePassword(user, passwordEncoder.encode(newPassword));
    }

    public void changePassword(User user, String oldPassword, String newPassword) {
        if (!passwordEncoder.matches(oldPassword, user.getPassword()))
            throw new CustomException("Incorrect old password!");
        validatePassword(newPassword);
        User userFromDb = getUser(user.getId());
        updatePassword(userFromDb, passwordEncoder.encode(newPassword));
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
            validatePassword(userInfo.password());
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setEmailPermission(false);
            user.setSmsPermission(false);
            return validateAndSave(user);
        }
    }

    public User createClient(CreateUser client, User loggedUser) {
        CreateUser newUser = new CreateUser(client.fullName(),UserRole.CLIENT,client.email(),client.phones(), loggedUser.getShop().getId());
        return createClient(newUser);
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
        if (userInfo.password() != null && !userInfo.password().isBlank()) {
            validatePassword(userInfo.password());
            user.setPassword(passwordEncoder.encode(userInfo.password()));
        }
        User endUser = validateAndSave(user);
        loggerService.createLog(new Log(LogType.UPDATED_USER), user.getFullName(), updateInfo);
        return endUser;
    }

    /**
     * Allows the user to make changes to itself.
     */
    public User updateUser(User user, CreateUser update) {
        String updateInfo = loggerService.userUpdateCheck(user, update);
        User userById = getUser(user.getId());
        userById.update(update);
        User endUser = validateAndSave(userById);
        loggerService.createLog(new Log(LogType.UPDATED_USER), userById.getFullName(), updateInfo);
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

    public List<User> getFilteredClients(UserFilter filter) {
        List<User> users = userRepository.findAll(filter);

        return users.stream().filter((user) -> filterByValue(filter.getFullName(), user.getFullName()))
                .filter((user) -> filterByValue(filter.getEmail(), user.getEmail()))
                .filter((user) -> user.getPhones().stream()
                        .anyMatch((phone) -> filterPhoneByValue(filter.getPhone(), phone)))
                .toList();
    }

    public static boolean filterPhoneByValue(String phoneFilter, UserPhone phone) {
        if (phoneFilter == null || phoneFilter.isEmpty())
            return true;
        if (phone == null || phone.getPhone() == null || phone.getPhone().isEmpty())
            return false;
        
        String formattedFilter = new UserPhone(phoneFilter).getComparableString();
        return filterByValue(formattedFilter, phone.getComparableString());
    }

    public static boolean filterByValue(String filterValue, String field) {
        if (filterValue == null || filterValue.isEmpty())
            return true;
        if (field == null || field.isEmpty())
            return false;

        Pattern mypattern = Pattern.compile(filterValue, Pattern.CASE_INSENSITIVE);
        return mypattern.matcher(field).find();
    }

    public void getUserImage(UUID id, HttpServletResponse response) {
        User user = getUser(id);
        if (user.getImage() == null) return;
        photoService.writeToResponse(response, user.getImage());
    }

    @Transactional
    public void updateUserImage(User user, MultipartFile image) {
        Photo photo = photoService.saveProfileImage(user.getUsername(), image);
        user.setImage(photo);
        loggerService.createLog(new Log(LogType.UPDATED_USER), user.getFullName(), "User updated their photo.");
        userRepository.save(user);
    }

    private User validateAndSave(User user) {
        validateUser(user);
        if (user.isNew()) {
            if (Objects.equals(user.getRole(), UserRole.CLIENT)) {
                loggerService.createLog(new Log(LogType.CREATED_CLIENT), user.getFullName());
            } else {
                loggerService.createLog(new Log(LogType.CREATED_WORKER), user.getFullName());
            }
        }
        return userRepository.save(user);
    }

    private void validateUser(User user) {
        if (user.getShop() == null) throw new CustomException("User must be attached to a shop!");
        List<User> existingUsers = userRepository.uniqueUserExists(user.getUsername(), user.getEmail());
        if (user.isNew() && !existingUsers.isEmpty() ||
                existingUsers.stream().anyMatch(profile -> !Objects.equals(profile.getId(), user.getId())))
            throw new CustomException("Username or email already taken!");
        validateUniquePhones(user);
    }

    private void validateUniquePhones(User user) {
        if (user.getPhones() == null || user.getPhones().isEmpty()) return;
        List<String> phones = user.getPhones().stream().map((UserPhone phone) -> UserPhone.extractPhoneNumber(phone.getPhoneWithCode())).toList();
        List<UserAndPhone> uniquePhones = userRepository.findUniquePhones(phones);

        String message = "Phone numbers already exist on a different user:" + uniquePhones.stream().map(UserAndPhone::phone).collect(Collectors.joining(","));

        if (!uniquePhones.isEmpty() && user.getId() == null || uniquePhones.stream().anyMatch(u -> !Objects.equals(u.user().getId(), user.getId())))
            throw new CustomException(message);
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
        final String NO_REQUEST_SENT_TO_USER_ERROR = "Your account or our shop do not allow email or sms communication. Please contact us on %s or %s or come visit us in person at %s".formatted(user.getShop().getEmail(), user.getShop().getPhone(), user.getShop().getAddress());
        try {
            if (emailService.isEmailEnabled(user)) {
                String messageBody = templateEngine.process("email/forgotPassword", getUserForgotPasswordContext(user));
                emailService.sendMail(user, messageBody, "Forgot password");
                return new ResponseMessage("email");
            }
            if (smsService.isSmsEnabled(user)) {
                smsService.sendSms(user, "forgotPassword.txt", getUserForgotPasswordContext(user));
                return new ResponseMessage("phone");
            }
        } catch (MailAuthenticationException e) {
            log.error("Cannot authenticate in Gmail using shop {}'s settings. Gmail's response: {}", user.getShop().getShopName(), e.getMessage());
            throw new CustomException(NO_REQUEST_SENT_TO_USER_ERROR);
        }
        throw new CustomException(NO_REQUEST_SENT_TO_USER_ERROR);
    }

    private Context getUserForgotPasswordContext(User user) {
        Context context = new Context();
        context.setVariables(Map.of("clientName", user.getFullName(),
                "shopName", user.getShop().getShopName(),
                "resetLink", "%s/login/update-password?token=%s".formatted(frontendHost, jwtUtil.encode(user))
        ));
        return context;
    }

    private static void validatePassword(String newPassword) {
        if (newPassword.length() < 5) throw new CustomException("Password must be at least 5 symbols");
    }

}
