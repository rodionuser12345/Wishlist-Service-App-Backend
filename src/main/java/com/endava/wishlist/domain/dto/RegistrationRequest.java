package com.endava.wishlist.domain.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@Builder
public class RegistrationRequest {

    @NotEmpty(message = "Full name is empty!")
    private String fullName;

    @NotNull(message = "Email is empty!")
    @Email(message = "Email is incorrect!")
    private String email;

    @NotEmpty(message = "password is empty!")
    private String password;
}
