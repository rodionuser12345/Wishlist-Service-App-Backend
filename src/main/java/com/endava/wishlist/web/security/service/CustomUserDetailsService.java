package com.endava.wishlist.web.security.service;

import com.endava.wishlist.domain.model.User;
import com.endava.wishlist.service.UserService;
import com.endava.wishlist.web.security.jwt.exception.EmailNotFoundException;
import com.endava.wishlist.web.security.model.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserService userService;

    @Override
    public CustomUserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userService.findByEmail(email);

        if (user == null){
            throw new UsernameNotFoundException("User with email: " + email + "not found");
        }

        log.info("IN loadUserByUsername - user with email: {} successfully loaded", email);
        return CustomUserDetails.fromUserToCustomUserDetails(user);
    }
}
