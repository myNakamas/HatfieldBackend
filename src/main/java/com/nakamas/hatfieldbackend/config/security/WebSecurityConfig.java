package com.nakamas.hatfieldbackend.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nakamas.hatfieldbackend.models.entities.User;
import com.nakamas.hatfieldbackend.models.enums.UserRole;
import com.nakamas.hatfieldbackend.models.views.outgoing.user.UserLogin;
import com.nakamas.hatfieldbackend.services.UserService;
import com.nakamas.hatfieldbackend.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.io.IOException;
import java.util.List;

@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final ObjectMapper mapper;
    private final JwtTokenFilter jwtFilter;
    private final JwtUtil jwtUtil;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .cors().configurationSource(corsConfigurationSource()).and()
                .formLogin()
                .failureHandler(this::loginFailureHandler)
                .successHandler(this::loginSuccessHandler)
                .loginProcessingUrl("/api/login").and()
                .logout().logoutUrl("/api/logout").and()
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .authenticationProvider(authenticationProvider())
                .authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers("/admin/**").hasAuthority(UserRole.ADMIN.getRole())
                        .requestMatchers("/public/**", "/ws").permitAll()
                        .anyRequest().authenticated().and())
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .requiresChannel().anyRequest().requiresSecure().and()
                .exceptionHandling().authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED));
        return http.build();
    }

    private void loginFailureHandler(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }

    private void loginSuccessHandler(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        User user = (User) authentication.getPrincipal();
        UserLogin userProfile = new UserLogin(user);
        log.info("User successfully logged in :[%s,%s,%s]".formatted(userProfile.username(), userProfile.fullName(), userProfile.role()));
        String token = jwtUtil.encode(user);
        response.getWriter().write(mapper.writeValueAsString(userProfile));
        response.setHeader("Authorization", token);
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_OK);
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userService);
        authenticationProvider.setUserDetailsPasswordService(userService);
        authenticationProvider.setPasswordEncoder(passwordEncoder);
        return authenticationProvider;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowedMethods(List.of("*"));
        corsConfiguration.setAllowedHeaders(List.of("*"));
        corsConfiguration.setExposedHeaders(List.of("*"));
        corsConfiguration.setAllowedOriginPatterns(List.of("*"));
        corsConfiguration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);
        return source;
    }
}
