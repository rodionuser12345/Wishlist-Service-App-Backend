package com.endava.wishlist.service;

import com.endava.wishlist.domain.dto.AuthRequest;
import com.endava.wishlist.domain.dto.AuthResponse;
import com.endava.wishlist.domain.dto.RegistrationRequest;
import com.endava.wishlist.domain.model.User;
import com.endava.wishlist.exception.CustomUserServiceException;

import java.util.List;

public interface UserService {

     User register(RegistrationRequest registrationRequest) throws CustomUserServiceException;

    AuthResponse login(AuthRequest request);

    List<User> getAll();

    void delete(Long id);

    User findById(Long id);

    User findByEmail(String email);

    User findByFullName(String name);

    User findByEmailAndPassword(String email, String password);
}
