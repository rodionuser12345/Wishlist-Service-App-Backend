package com.endava.wishlist.web.controller;

import com.endava.wishlist.domain.dto.AuthRequest;
import com.endava.wishlist.domain.dto.AuthResponse;
import com.endava.wishlist.domain.dto.RegistrationRequest;
import com.endava.wishlist.exception.CustomUserServiceException;
import com.endava.wishlist.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> registerUser(HttpServletRequest request, @Valid @RequestBody RegistrationRequest registrationRequest) throws CustomUserServiceException, ServletException {

        String email = registrationRequest.getEmail();
        String password = registrationRequest.getPassword();

        AuthRequest authRequest = new AuthRequest(email, password);

        userService.register(registrationRequest);

        request.login(email, password);

        return this.login(authRequest);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        return ResponseEntity.ok(userService.login(request));
    }
}
