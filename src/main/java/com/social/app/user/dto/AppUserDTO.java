package com.social.app.user.dto;

import com.social.app.user.model.ApplicationUser;
import com.social.app.user.model.UserRole;
import lombok.Data;

import java.util.Set;

@Data
public class AppUserDTO {

    private String username;

    private String email;

    private UserRole role;

    private int numberOfFollowers;

    private Set<ApplicationUser> followers;

}
