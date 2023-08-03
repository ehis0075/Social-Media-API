package com.social.app.user.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class SignInRequest {

    @NotBlank(message = "username is required")
    @Pattern(regexp = "^(.+)@(.+)$", message = "username must be a valid email address ")
    private String username;
    @NotBlank(message = "password is required")
    private String password;
}
