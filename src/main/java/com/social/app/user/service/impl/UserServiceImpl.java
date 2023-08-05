package com.social.app.user.service.impl;

import com.social.app.exception.GeneralException;
import com.social.app.general.dto.Response;
import com.social.app.general.enums.ResponseCodeAndMessage;
import com.social.app.general.service.GeneralService;
import com.social.app.image.service.ImageService;
import com.social.app.security.JwtTokenProvider;
import com.social.app.user.dto.*;
import com.social.app.user.model.ApplicationUser;
import com.social.app.user.model.UserRole;
import com.social.app.user.repository.UserRepository;
import com.social.app.user.service.UserService;
import com.social.app.util.GeneralUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final ImageService imageService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final GeneralService generalService;
    private final AuthenticationManager authenticationManager;

    @Override
    public AppUserDTO signup(SignUpRequest request) {
        log.info("Request to create user with payload ={}", request);

        //check if user exists
        validateEmailAndUserName(request);

        //save user
        ApplicationUser user = new ApplicationUser();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setUserRole(UserRole.ROLE_USER);

        String fieldId = addProfileImage(request);

        user.setImageUrl(fieldId);

        saveUser(user);

        return getUserDTO(user);
    }

    @Override
    public Response signIn(String username, String password) {
        log.info("Request to login with username {}", username);

        try {
            // attempt authentication
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));

            //if successful, set authentication object in the security context
            SecurityContextHolder.getContext().setAuthentication(authentication);

            UserRole userRole = userRepository.findByUsername(username).getUserRole();

            //generate jwt token
            String token = jwtTokenProvider.generateToken(username, userRole);

            Response response = new Response();
            response.setResponseCode(ResponseCodeAndMessage.SUCCESSFUL_0.responseCode);
            response.setResponseMessage(ResponseCodeAndMessage.SUCCESSFUL_0.responseMessage);
            response.setData(SignInResponse.builder().username(username).userRole(userRole).accessToken(token).build());

            log.info("Successfully logged-in user {}", username);

            return response;

        } catch (AuthenticationException e) {
            log.info("Incorrect User credentials");
            throw new GeneralException(ResponseCodeAndMessage.AUTHENTICATION_FAILED_95);
        }
    }

    @Override
    public AppUserDTO updateUser(SignUpRequest request, Long userId, String username) {
        log.info("Request to update user {} with payload ={}", userId, request);

        //check if user exists
        validateEmailAndUserName(request);

        //save user
        ApplicationUser user = getUserByUsername(username);
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        String fieldId = addProfileImage(request);
        user.setImageUrl(fieldId);

        saveUser(user);

        return getUserDTO(user);
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
        appUserDTO.setFollowerCount(request.getNumberOfFollowers());

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
    public void follow(String userToFollowUsername, String loggedInUserUsername) {
        log.info("Request to follow a friend with loggedInUserUsername {} by loggedInUserUsername {}", userToFollowUsername, loggedInUserUsername);

        if (GeneralUtil.stringIsNullOrEmpty(userToFollowUsername)) {
            throw new GeneralException(ResponseCodeAndMessage.INCOMPLETE_PARAMETERS_91.responseCode, "the username of the user cannot be null or empty!");
        }

        ApplicationUser loggedInUser = getUserByUsername(loggedInUserUsername);

        //find the follower in the db
        ApplicationUser userToFollowEntity = getUserByUsername(userToFollowUsername);

        // check
        if (loggedInUser.getId().equals(userToFollowEntity.getId())) {
            throw new GeneralException(ResponseCodeAndMessage.ALREADY_EXIST_86.responseCode, "You cannot follow yourself");
        }

        //follow the selected user
        addUserToFollowingList(loggedInUser, userToFollowEntity);

        //for follower
        addToFollower(loggedInUser, userToFollowEntity);
    }

    @Override
    public void unFollow(String userToUnFollowUsername, String loggedInUserUsername) {
        log.info("Request to unFollow a friend with loggedInUserUsername {} by loggedInUserUsername {}", userToUnFollowUsername, loggedInUserUsername);

        if (GeneralUtil.stringIsNullOrEmpty(userToUnFollowUsername)) {
            throw new GeneralException(ResponseCodeAndMessage.INCOMPLETE_PARAMETERS_91.responseCode, "the username of the user cannot be null or empty!");
        }

        ApplicationUser loggedInUser = getUserByUsername(loggedInUserUsername);

        //find the follower in the db
        ApplicationUser userToUnFollowEntity = getUserByUsername(userToUnFollowUsername);

        //follow the selected user
        removeUserFromFollowingList(loggedInUser, userToUnFollowEntity);

        //for follower
        removeFollower(loggedInUser, userToUnFollowEntity);
    }

    @Override
    public ApplicationUser saveUser(ApplicationUser user) {

        user = userRepository.save(user);
        log.info("successfully saved user to db");
        return user;
    }

    @Override
    public UserListDTO getAllUsers(UserRequestDTO request) {
        log.info("Getting Users List");

        Pageable paged = generalService.getPageableObject(request.getSize(), request.getPage());
        Page<ApplicationUser> applicationUsers = userRepository.findAll(paged);
        log.info("user list {}", applicationUsers);

        UserListDTO userListDTO = new UserListDTO();

        List<ApplicationUser> userList = applicationUsers.getContent();
        if (applicationUsers.getContent().size() > 0) {
            userListDTO.setHasNextRecord(applicationUsers.hasNext());
            userListDTO.setTotalCount((int) applicationUsers.getTotalElements());
        }

        List<AppUserDTO> userDTOList = convertToUserDTOList(userList);
        userListDTO.setUserDTOList(userDTOList);

        return userListDTO;
    }

    private void addToFollower(ApplicationUser loggedInUser, ApplicationUser userToFollowEntity) {
        Set<ApplicationUser> followers = userToFollowEntity.getFollowers();

        int numberOfFollowers = incrementCount(userToFollowEntity.getNumberOfFollowers());
        addAndIncrementNoOfFollowers(loggedInUser, userToFollowEntity, followers, numberOfFollowers);
    }

    private void removeFollower(ApplicationUser loggedInUser, ApplicationUser userToUnFollowEntity) {
        Set<ApplicationUser> followers = userToUnFollowEntity.getFollowers();

        int numberOfFollowers = decrementCount(userToUnFollowEntity.getNumberOfFollowers());
        removeAndDecrementNoOfFollowers(loggedInUser, userToUnFollowEntity, followers, numberOfFollowers);
    }

    private void addUserToFollowingList(ApplicationUser loggedInUser, ApplicationUser userToFollow) {
        String userToFollowUsername = userToFollow.getUsername();

        Set<ApplicationUser> loggedInUserFollowing = loggedInUser.getFollowing();

        int numberOfFollowing = incrementCount(loggedInUser.getNumberOfFollowing());

        if (Objects.nonNull(loggedInUserFollowing) && !loggedInUserFollowing.isEmpty()) {

            // check if the loggedInUser is not folllowing this person
            boolean isAlreadyFollowing = loggedInUserFollowing.stream().anyMatch(follower -> follower.getUsername().equals(userToFollowUsername));

            if (isAlreadyFollowing) {
                log.info("You have previously followed this user");
                throw new GeneralException(ResponseCodeAndMessage.OPERATION_NOT_SUPPORTED_93.responseCode, "You have previously followed this user!");
            }

            addAndIncrementNoOfFollowing(loggedInUser, userToFollow, loggedInUserFollowing, numberOfFollowing);

        } else {
            addAndIncrementNoOfFollowing(loggedInUser, userToFollow, loggedInUserFollowing, numberOfFollowing);
        }
    }

    private void removeUserFromFollowingList(ApplicationUser loggedInUser, ApplicationUser userToUnFollow) {
        log.info("Request to remove user {} from the following list by user {}", userToUnFollow, loggedInUser);
        String userToUnFollowUsername = userToUnFollow.getUsername();

        Set<ApplicationUser> loggedInUserFollowing = loggedInUser.getFollowing();

        int numberOfFollowing = decrementCount(loggedInUser.getNumberOfFollowing());

        if (Objects.nonNull(loggedInUserFollowing) && !loggedInUserFollowing.isEmpty()) {

            // check if the loggedInUser is folllowing this person
            boolean isAlreadyFollowing = loggedInUserFollowing.stream().anyMatch(follower -> follower.getUsername().equals(userToUnFollowUsername));

            if (!isAlreadyFollowing) {
                log.info("You are not following this user");
                throw new GeneralException(ResponseCodeAndMessage.OPERATION_NOT_SUPPORTED_93.responseCode, "You are not following this user!");
            }

            removeAndDecrementNoOfFollowing(loggedInUser, userToUnFollow, loggedInUserFollowing, numberOfFollowing);

        } else {
            removeAndDecrementNoOfFollowing(loggedInUser, userToUnFollow, loggedInUserFollowing, numberOfFollowing);
        }
    }

    private int incrementCount(int count) {
        return count == 0 ? 1 : count + 1;
    }

    private int decrementCount(int count) {
        return count == 0 ? 1 : count - 1;
    }

    private void addAndIncrementNoOfFollowers(ApplicationUser loggedInUser, ApplicationUser followingUser, Set<ApplicationUser> followers, int numberOfFollowers) {
        followers.add(loggedInUser);
        followingUser.setFollowers(followers);
        followingUser.setNumberOfFollowers(numberOfFollowers);
        saveUser(followingUser);
    }

    private void removeAndDecrementNoOfFollowers(ApplicationUser loggedInUser, ApplicationUser followingUser, Set<ApplicationUser> followers, int numberOfFollowers) {
        followers.remove(loggedInUser);
        followingUser.setFollowers(followers);
        followingUser.setNumberOfFollowers(numberOfFollowers);
        saveUser(followingUser);
    }

    private void addAndIncrementNoOfFollowing(ApplicationUser loggedInUser, ApplicationUser followingUser, Set<ApplicationUser> loggedInUserFollowing, int numberOfFollowing) {
        loggedInUserFollowing.add(followingUser);
        loggedInUser.setFollowing(loggedInUserFollowing);
        // Increment the followerCount for the loggedInUser
        loggedInUser.setNumberOfFollowing(numberOfFollowing);
        saveUser(loggedInUser);
    }

    private void removeAndDecrementNoOfFollowing(ApplicationUser loggedInUser, ApplicationUser followingUser, Set<ApplicationUser> loggedInUserFollowing, int numberOfFollowing) {
        log.info("Removing and decrementing the number of following");

        loggedInUserFollowing.remove(followingUser);
        loggedInUser.setFollowing(loggedInUserFollowing);

        // Decrement the followerCount for the loggedInUser
        loggedInUser.setNumberOfFollowing(numberOfFollowing);
        saveUser(loggedInUser);
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

    private List<AppUserDTO> convertToUserDTOList(List<ApplicationUser> adminUserList) {
        log.info("Converting User List to User DTO List");

        return adminUserList.stream().map(this::getUserDTO).collect(Collectors.toList());
    }

    private String addProfileImage(SignUpRequest request) {
        Map<String, String> fileIdAndImage = imageService.uploadImage(request.getImageUrl(), request.getUsername());
        if (Objects.nonNull(fileIdAndImage)) {
            log.info("uploading Profile image");

            String fieldId = fileIdAndImage.get("fileId");
            String url = fileIdAndImage.get("url");

            request.setImageUrl(url);

            return fieldId;
        } else {
            if (Objects.nonNull(request.getImageUrl()) && request.getImageUrl().length() > 200) {
                request.setImageUrl(null);
            }
            return null;
        }
    }


}
