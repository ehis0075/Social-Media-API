package com.social.app.user.dto;

import com.social.app.user.model.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class SignInResponse {
    private String username;
    private UserRole userRole;
    private String accessToken;
}
