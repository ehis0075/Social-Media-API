package com.social.app.user.service;


import com.social.app.general.dto.Response;
import com.social.app.user.dto.AppUserDTO;
import com.social.app.user.dto.SignUpRequest;
import com.social.app.user.model.ApplicationUser;

public interface UserService {
    Response signIn(String username, String password);

    Response signup(SignUpRequest request);

    Response updateUser(SignUpRequest request, Long userId, String username);

    void deleteUser(Long userId, String username);

    AppUserDTO getUserDTO(ApplicationUser request);

    ApplicationUser getUserByUsername(String username);

    Response followAfriend(String followerUsername, String username);

    Response unFollow(String followerUsername, String username);

    void saveUser(ApplicationUser user);
}
