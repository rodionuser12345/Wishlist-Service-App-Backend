package com.endava.wishlist.service.impl;

import com.endava.wishlist.domain.dto.AuthRequest;
import com.endava.wishlist.domain.dto.AuthResponse;
import com.endava.wishlist.domain.dto.RegistrationRequest;
import com.endava.wishlist.domain.model.User;
import com.endava.wishlist.exception.CustomUserServiceException;
import com.endava.wishlist.repository.UserRepository;
import com.endava.wishlist.service.UserService;
import com.endava.wishlist.web.security.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository repository;

    private final PasswordEncoder passwordEncoder;

    private final JwtProvider jwtProvider;

    @Override
    @Transactional
    public User register(RegistrationRequest registrationRequest) throws CustomUserServiceException {
        User user = User.builder()
                .role("ROLE_USER")
                .password(passwordEncoder.encode(registrationRequest.getPassword()))
                .fullName(registrationRequest.getFullName())
                .build();
        if (repository.findByEmail(registrationRequest.getEmail()) != null) {
            throw new CustomUserServiceException("Email is already taken", HttpStatus.BAD_REQUEST);
        }
        user.setEmail(registrationRequest.getEmail());

        User registeredUser = repository.save(user);

        log.info("IN register - user: {} successfully registered", registeredUser);

        return registeredUser;
    }

    @Override
    public AuthResponse login(AuthRequest request) {

        User user = findByEmailAndPassword(request.getEmail(), request.getPassword());

        if (user == null) {
            throw new BadCredentialsException("Invalid email or password");
        }

        return new AuthResponse(jwtProvider.generateToken(user.getEmail(), user.getFullName()));
    }

    @Override
    public List<User> getAll() {
        List<User> result = repository.findAll();

        log.info("IN getAll - {} users found", result.size());

        return result;
    }

    @Override
    @Transactional
    public void delete(Long id) {
        User result = repository.findById(id).orElse(null);

        if (result == null) {
            log.warn("IN delete - no user found by id: {}", id);
            return;
        }

        repository.deleteById(id);

        log.info("IN delete - user with id: {} successfully deleted", result);

    }

    @Override
    public User findById(Long id) {
        User result = repository.findById(id).orElse(null);

        if (result == null) {
            log.warn("IN findById - no user found by id: {}", id);
            return null;
        }

        log.info("IN findById - user: {} found by id: {}", result, id);

        return result;
    }

    @Override
    public User findByEmail(String email) {
        User result = repository.findByEmail(email);

        log.info("IN findByEmail - user: {} found by email: {}", result, email);

        return result;
    }

    @Override
    public User findByFullName(String name) {
        User result = repository.findByFullName(name);

        log.info("IN findByFullName - user: {} found by full_name: {}", result, name);

        return result;
    }

    @Override
    public User findByEmailAndPassword(String email, String password) {
        User result = repository.findByEmail(email);

        if (result != null) {
            if (passwordEncoder.matches(password, result.getPassword())) {
                log.info("IN findByEmailAndPassword - user: {} found", result);

                return result;
            }
        }

        return null;
    }
}