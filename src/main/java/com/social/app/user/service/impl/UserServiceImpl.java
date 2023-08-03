package com.social.app.user.service.impl;

import com.social.app.exception.GeneralException;
import com.social.app.general.dto.Response;
import com.social.app.general.enums.ResponseCodeAndMessage;
import com.social.app.security.JwtTokenProvider;
import com.social.app.user.dto.AppUserDTO;
import com.social.app.user.dto.SignInResponse;
import com.social.app.user.dto.SignUpRequest;
import com.social.app.user.model.ApplicationUser;
import com.social.app.user.model.UserRole;
import com.social.app.user.repository.UserRepository;
import com.social.app.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    @Override
    public Response signup(SignUpRequest request) {
        log.info("Request to create user with payload ={}", request);

        //check if user exists
        validateEmailAndUserName(request);

        //save user
        ApplicationUser user = new ApplicationUser();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setUserRole(UserRole.ROLE_USER);

        saveUser(user);

        AppUserDTO appUserDTO = getUserDTO(user);

        Response response = new Response();
        response.setResponseCode(ResponseCodeAndMessage.SUCCESSFUL_0.responseCode);
        response.setResponseMessage(ResponseCodeAndMessage.SUCCESSFUL_0.responseMessage);
        response.setData(appUserDTO);

        return response;
    }

    @Override
    public Response signIn(String username, String password) {
        log.info("Request to login with username {}", username);

        try {
            // attempt authentication
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));

            //if successful, set authentication object in the security context
            SecurityContextHolder.getContext().setAuthentication(authentication);

            UserRole userRoles = userRepository.findByUsername(username).getUserRole();

            //generate jwt token
            String token = jwtTokenProvider.generateToken(username, userRoles);

            Response response = new Response();
            response.setResponseCode(ResponseCodeAndMessage.SUCCESSFUL_0.responseCode);
            response.setResponseMessage(ResponseCodeAndMessage.SUCCESSFUL_0.responseMessage);
            response.setData(SignInResponse.builder().username(username).userRole(userRoles).accessToken(token).build());

            log.info("Successfully logged-in user {}", username);

            return response;

        } catch (AuthenticationException e) {
            log.info("Incorrect User credentials");
            throw new GeneralException(ResponseCodeAndMessage.AUTHENTICATION_FAILED_95);
        }
    }

    @Override
    public Response updateUser(SignUpRequest request, Long userId, String username) {
        log.info("Request to update user {} with payload ={}", userId, request);

        //check if user exists
        validateEmailAndUserName(request);

        //save user
        ApplicationUser user = getUserByUsername(username);
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        saveUser(user);

        AppUserDTO appUserDTO = getUserDTO(user);

        Response response = new Response();
        response.setResponseCode(ResponseCodeAndMessage.SUCCESSFUL_0.responseCode);
        response.setResponseMessage(ResponseCodeAndMessage.SUCCESSFUL_0.responseMessage);
        response.setData(appUserDTO);

        return response;
    }

    @Override
    public void deleteUser(Long userId, String username) {
        log.info("Request to delete user {}", userId);

        //save user
        ApplicationUser user = getUserByUsername(username);
        userRepository.delete(user);
    }

    @Override
    public AppUserDTO getUserDTO(ApplicationUser request) {
        log.info("Getting User DTO");
        AppUserDTO appUserDTO = new AppUserDTO();
        appUserDTO.setUsername(request.getUsername());
        appUserDTO.setEmail(request.getEmail());
        appUserDTO.setRole(UserRole.ROLE_USER);
        appUserDTO.setNumberOfFollowers(request.getNumberOfFollowers());

        return appUserDTO;
    }

    @Override
    public ApplicationUser getUserByUsername(String username) {
        log.info("Request to get a user by username {}", username);

        ApplicationUser user = userRepository.findByUsername(username);

        if (Objects.isNull(user)) {
            throw new GeneralException(ResponseCodeAndMessage.RECORD_NOT_FOUND_88.responseCode, "User does Not exist");
        }
        return user;
    }

    @Override
    public Response followAfriend(String followerUsername, String username) {
        log.info("Request to follow a friend with username {} by username {}", followerUsername, username);

        ApplicationUser user = getUserByUsername(username);

        //find the follower in the db
        ApplicationUser newFollower = getUserByUsername(followerUsername);

        // check that the
        if (Objects.equals(user, newFollower)) {
            throw new GeneralException(ResponseCodeAndMessage.ALREADY_EXIST_86.responseCode, "You cannot follow yourself");
        }

        Set<ApplicationUser> followers = user.getFollowers();

        // check if the user is not folllowing this person
        boolean isFollowerExist = followers.stream().anyMatch(follower -> follower.getUsername().equals(followerUsername));

        Set<ApplicationUser> followerList = new HashSet<>();
        followerList.add(newFollower);

        Response response = new Response();

        if (!isFollowerExist) {

            // add the new follower to the follower list
            user.setFollowers(followerList);

            // Increment the followerCount for the user
            user.setNumberOfFollowers(user.getNumberOfFollowers() + 1);
            saveUser(user);

            AppUserDTO appUserDTO = getUserDTO(user);

            response.setResponseCode(ResponseCodeAndMessage.SUCCESSFUL_0.responseCode);
            response.setResponseMessage(ResponseCodeAndMessage.SUCCESSFUL_0.responseMessage);
            response.setData(appUserDTO);
        } else {
            throw new GeneralException(ResponseCodeAndMessage.ALREADY_EXIST_86.responseCode, "You are already following this user");
        }
        return response;
    }

    @Override
    public Response unFollow(String followerUsername, String username) {
        log.info("Request to unFollow a friend with username {} by username {}", followerUsername, username);

        ApplicationUser user = getUserByUsername(username);

        //find the follower in the db
        ApplicationUser newFollower = getUserByUsername(followerUsername);

        Set<ApplicationUser> followerList = user.getFollowers();

        // check if the user is not folllowing this person
        boolean isFollowerExist = followerList.stream().anyMatch(follower -> follower.getUsername().equals(followerUsername));

        Response response = new Response();

        if (isFollowerExist) {

            // remove the new follower to the follower list
            followerList.remove(newFollower);

            // decrement the followerCount
            user.setNumberOfFollowers(user.getNumberOfFollowers() - 1);
            saveUser(user);

            AppUserDTO appUserDTO = getUserDTO(user);

            response.setResponseCode(ResponseCodeAndMessage.SUCCESSFUL_0.responseCode);
            response.setResponseMessage(ResponseCodeAndMessage.SUCCESSFUL_0.responseMessage);
            response.setData(appUserDTO);
        } else {
            throw new GeneralException(ResponseCodeAndMessage.ALREADY_EXIST_86.responseCode, "You are not following this user");
        }
        return response;
    }

    @Override
    public void saveUser(ApplicationUser user) {
        userRepository.save(user);
        log.info("successfully saved user to db");
    }

    private void validateEmailAndUserName(SignUpRequest request) {

        boolean isExist = userRepository.existsByEmail(request.getEmail());

        boolean isExist2 = userRepository.existsByUsername(request.getUsername());

        if (isExist) {
            throw new GeneralException(ResponseCodeAndMessage.ALREADY_EXIST_86.responseCode, "Email already exist");
        }

        if (isExist2) {
            throw new GeneralException(ResponseCodeAndMessage.ALREADY_EXIST_86.responseCode, "Username already exist");
        }
    }
}
