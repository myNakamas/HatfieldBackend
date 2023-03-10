package com.nakamas.hatfieldbackend.services;

import com.nakamas.hatfieldbackend.config.exception.CustomException;
import com.nakamas.hatfieldbackend.models.entities.Photo;
import com.nakamas.hatfieldbackend.models.entities.User;
import com.nakamas.hatfieldbackend.models.enums.UserRole;
import com.nakamas.hatfieldbackend.models.views.incoming.CreateUser;
import com.nakamas.hatfieldbackend.models.views.outgoing.user.UserProfile;
import com.nakamas.hatfieldbackend.repositories.ShopRepository;
import com.nakamas.hatfieldbackend.repositories.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService, UserDetailsPasswordService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final ShopRepository shopRepository;

    public User getUser(UUID id) {
        return userRepository.findById(id).orElseThrow(() -> new CustomException("User does not exist"));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findUserByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User does not exist"));
    }

    @Override
    public UserDetails updatePassword(UserDetails userDetails, String newPassword) {
        User user = (User) userDetails;
        user.setPassword(newPassword);
        validateAndSave(user);
        return user;
    }

    public void changePassword(User user, String oldPassword, String newPassword) {
        if (!passwordEncoder.matches(oldPassword, user.getPassword()))
            throw new CustomException("Incorrect old password!");
        updatePassword(user, passwordEncoder.encode(newPassword));
    }

    public User createUser(CreateUser userInfo) {
        User user = new User(userInfo, shopRepository.findById(userInfo.shopId()).orElse(null));
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return validateAndSave(user);
    }

    public User createClient(CreateUser userInfo) {
        User user = new User(userInfo, shopRepository.findById(userInfo.shopId()).orElse(null));
        user.setRole(UserRole.CLIENT);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return validateAndSave(user);
    }

    /**
     * Allows the admin to change whoever he decides.
     */
    public User updateUser(CreateUser userInfo) {
        User user = userRepository.getReferenceById(userInfo.userId());
        user.updateAsAdmin(userInfo, shopRepository.findById(userInfo.shopId()).orElse(user.getShop()));
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return validateAndSave(user);
    }

    /**
     * Allows the user to make changes to itself.
     */
    public User updateUser(User user, CreateUser update) {
        user.update(update);
        return validateAndSave(user);
    }

    public List<UserProfile> getAllWorkers(String searchBy) {
        return userRepository.findAllWorkers(searchBy);
    }

    @Transactional
    public void getUserImage(UUID id, HttpServletResponse response) {
        User user = getUser(id);
        try(InputStream userImage = new ByteArrayInputStream(user.getImage().getData())){
            userImage.transferTo(response.getOutputStream());
        }
        catch (IOException e){
            log.info(e.getMessage());
            throw new CustomException("Could not load profile image");
        }
    }

    public void updateUserImage(User user, MultipartFile image) {
        try {
            user.setImage(new Photo(image.getBytes(),false));
            userRepository.save(user);
        } catch (IOException e) {
            e.printStackTrace();
            throw new CustomException("Could not save image to the server.");
        }
    }

    private User validateAndSave(User user){
        List<User> existingUsers = userRepository.uniqueUserExists(user.getUsername(), user.getEmail());
        if(user.getId() == null && existingUsers.size() > 0 ||
                existingUsers.stream().anyMatch(profile -> !Objects.equals(profile.getId(), user.getId())))
            throw new CustomException("Username or email already taken!");
        return userRepository.save(user);
    }

    public UserProfile getUserProfile(User user) {
        return new UserProfile(user);
    }
}
