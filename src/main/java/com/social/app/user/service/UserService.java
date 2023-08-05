package com.social.app.user.service;


import com.social.app.general.dto.Response;
import com.social.app.user.dto.AppUserDTO;
import com.social.app.user.dto.SignUpRequest;
import com.social.app.user.dto.UserListDTO;
import com.social.app.user.dto.UserRequestDTO;
import com.social.app.user.model.ApplicationUser;

public interface UserService {
    Response signIn(String username, String password);

    AppUserDTO signup(SignUpRequest request);

    AppUserDTO updateUser(SignUpRequest request, Long userId, String username);

    void deleteUser(Long userId, String username);

    AppUserDTO getUserDTO(ApplicationUser request);

    ApplicationUser getUserByUsername(String username);

    void follow(String followerUsername, String username);

    void unFollow(String followerUsername, String username);

    ApplicationUser saveUser(ApplicationUser user);

    UserListDTO getAllUsers(UserRequestDTO request);

    void addToFollower(ApplicationUser loggedInUser, ApplicationUser userToFollowEntity);

    void removeFollower(ApplicationUser loggedInUser, ApplicationUser userToUnFollowEntity);

    void addUserToFollowingList(ApplicationUser loggedInUser, ApplicationUser userToFollow);

    void removeUserFromFollowingList(ApplicationUser loggedInUser, ApplicationUser userToUnFollow);
}
