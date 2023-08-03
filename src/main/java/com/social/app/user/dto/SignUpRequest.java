package com.social.app.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
@Builder
@AllArgsConstructor
public class SignUpRequest {

    @NotBlank(message = "email is required")
    @Pattern(regexp = "^(.+)@(.+)$", message = "username must be a valid email address ")
    private String email;

    @NotBlank(message = "username is required")
    private String username;

    @NotBlank(message = "password is required")
    private String password;
}
