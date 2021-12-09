package com.endava.wishlist.domain.dto;

import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@Data
@AllArgsConstructor
@Builder
public class AuthRequest {

    @NotEmpty
    @Email
    private String email;

    @NotEmpty
    private String password;
}
