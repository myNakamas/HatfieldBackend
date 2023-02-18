package com.nakamas.hatfieldbackend.services;

import com.nakamas.hatfieldbackend.models.entities.User;
import com.nakamas.hatfieldbackend.models.views.incoming.CreateUser;
import com.nakamas.hatfieldbackend.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Configuration
@RequiredArgsConstructor
public class UserService implements UserDetailsService, UserDetailsPasswordService {

    private final UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findUserByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User does not exist"));
    }

    @Override
    public UserDetails updatePassword(UserDetails userDetails, String newPassword) {
        User user = (User)userDetails;
        user.setPassword(newPassword);
        userRepository.save(user);
        return user;
    }

    public User createUser(CreateUser userInfo){
        return userRepository.save(new User(userInfo));
    }
}
