package com.social.app.user.service.impl;

import com.social.app.general.dto.Response;
import com.social.app.general.enums.ResponseCodeAndMessage;
import com.social.app.image.service.ImageService;
import com.social.app.security.JwtTokenProvider;
import com.social.app.user.dto.AppUserDTO;
import com.social.app.user.dto.SignInResponse;
import com.social.app.user.dto.SignUpRequest;
import com.social.app.user.model.ApplicationUser;
import com.social.app.user.model.UserRole;
import com.social.app.user.repository.UserRepository;
import com.social.app.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest()
class UserServiceImplTest {

    @Autowired
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ImageService imageService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Test
    public void testSignup() {
        // Create a SignUpRequest with sample data
        SignUpRequest request = new SignUpRequest();
        request.setUsername("ola");
        request.setEmail("ola@gmail.com");
        request.setPassword("ola123");
        request.setImageUrl("sampleBase64Image");

        // Mock the behavior of the userRepository to return null when checking if the user exists
        when(userRepository.findByUsername(request.getUsername())).thenReturn(null);

        // Mock the behavior of the passwordEncoder to return a hashed password
        String hashedPassword = "hashedPassword";
        when(passwordEncoder.encode(request.getPassword())).thenReturn(hashedPassword);

        // Mock the behavior of the imageService to return a fieldId
        String fieldId = "sampleFieldId";
        when(imageService.uploadImage(anyString(), anyString())).thenReturn(Map.of("fileId", fieldId, "url", "sampleImageUrl"));

        // Perform the signup operation
        AppUserDTO userDTO = userService.signup(request);

        // Verify that the userRepository.save() method was called
        verify(userRepository, times(1)).save(any(ApplicationUser.class));

        // Verify that the passwordEncoder.encode() method was called
        verify(passwordEncoder, times(1)).encode(request.getPassword());

        // Verify that the imageService.uploadImage() method was called
        verify(imageService, times(1)).uploadImage(eq(request.getImageUrl()), eq(request.getUsername()));

        // Assert the returned AppUserDTO
        assertNotNull(userDTO);
        assertEquals(request.getUsername(), userDTO.getUsername());
        assertEquals(request.getEmail(), userDTO.getEmail());
        assertEquals("sampleImageUrl", userDTO.getImageUrl());
    }

    @Test
    public void testSignIn_Success() {
        // Create sample input data
        String username = "testUser";
        String password = "testPassword";
        UserRole userRole = UserRole.ROLE_USER;

        // Mock the behavior of authenticationManager.authenticate()
        Authentication authentication = new UsernamePasswordAuthenticationToken(username, password);
        when(authenticationManager.authenticate(authentication)).thenReturn(authentication);

        // Mock the behavior of userRepository.findByUsername()
        ApplicationUser user = new ApplicationUser();
        user.setUsername(username);
        user.setUserRole(userRole);
        when(userRepository.findByUsername(username)).thenReturn(user);

        // Mock the behavior of jwtTokenProvider.generateToken()
        String token = "sampleToken";
        when(jwtTokenProvider.generateToken(username, userRole)).thenReturn(token);

        // Perform the signIn operation
        Response response = userService.signIn(username, password);

        // Verify that authenticationManager.authenticate() was called
        verify(authenticationManager, times(1)).authenticate(authentication);

        // Verify that userRepository.findByUsername() was called
        verify(userRepository, times(1)).findByUsername(username);

        // Verify that jwtTokenProvider.generateToken() was called
        verify(jwtTokenProvider, times(1)).generateToken(username, userRole);

        // Assert the Response object
        assertNotNull(response);
        assertEquals(ResponseCodeAndMessage.SUCCESSFUL_0.responseCode, response.getResponseCode());
        assertEquals(ResponseCodeAndMessage.SUCCESSFUL_0.responseMessage, response.getResponseMessage());

        SignInResponse signInResponse = (SignInResponse) response.getData();
        assertNotNull(signInResponse);
        assertEquals(username, signInResponse.getUsername());
        assertEquals(userRole, signInResponse.getUserRole());
        assertEquals(token, signInResponse.getAccessToken());
    }


    @Test
    public void testUpdateUser_Success() {
        // Create sample input data
        Long userId = 1L;
        String username = "ola";
        SignUpRequest request = new SignUpRequest();
        request.setUsername("ola125");
        request.setEmail("ola123@example.com");
        request.setPassword("ola123");
        request.setImageUrl("base64EncodedImage");

        ApplicationUser existingUser = new ApplicationUser();
        existingUser.setId(userId);
        existingUser.setUsername(username);
        existingUser.setEmail("ola@example.com");
        existingUser.setPassword("ola123");
        existingUser.setNumberOfFollowers(10);

        // Mock the behavior of userRepository.findByUsername()
        when(userRepository.findByUsername(username)).thenReturn(existingUser);

        // Mock the behavior of passwordEncoder.encode()
        String encodedPassword = "encodedPassword";
        when(passwordEncoder.encode(request.getPassword())).thenReturn(encodedPassword);

        // Mock the behavior of imageService.uploadImage()
        String fieldId = "fieldId";
        when(imageService.uploadImage(request.getImageUrl(), request.getUsername())).thenReturn(Map.of("fileId", fieldId, "url", "imageUrl"));

        // Perform the updateUser operation
        AppUserDTO updatedUserDTO = userService.updateUser(request, userId, username);

        // Verify that userRepository.findByUsername() was called
        verify(userRepository, times(1)).findByUsername(username);

        // Verify that passwordEncoder.encode() was called
        verify(passwordEncoder, times(1)).encode(request.getPassword());

        // Verify that imageService.uploadImage() was called
        verify(imageService, times(1)).uploadImage(request.getImageUrl(), request.getUsername());

        // Assert the updatedUserDTO
        assertNotNull(updatedUserDTO);
        assertEquals(request.getUsername(), updatedUserDTO.getUsername());
        assertEquals(request.getEmail(), updatedUserDTO.getEmail());
        assertEquals(UserRole.ROLE_USER, updatedUserDTO.getRole());
        assertEquals(existingUser.getNumberOfFollowers(), updatedUserDTO.getFollowerCount());
    }

    @Test
    public void testDeleteUser_Success() {
        // Create sample input data
        Long userId = 1L;
        String username = "ola";

        ApplicationUser existingUser = new ApplicationUser();
        existingUser.setId(userId);
        existingUser.setUsername(username);

        // Mock the behavior of userRepository.findByUsername()
        when(userRepository.findByUsername(username)).thenReturn(existingUser);

        // Perform the deleteUser operation
        userService.deleteUser(userId, username);

        // Verify that userRepository.findByUsername() was called
        verify(userRepository, times(1)).findByUsername(username);

        // Verify that userRepository.delete() was called
        verify(userRepository, times(1)).delete(existingUser);
    }

    @Test
    public void testGetUserByUsername_UserNotFound() {
        // Create sample input data
        String username = "dapo";

        // Mock the behavior of userRepository.findByUsername() to return null
        when(userRepository.findByUsername(username)).thenReturn(null);

        // Perform the getUserByUsername operation, expecting an exception
        userService.getUserByUsername(username);

        // Verify that userRepository.findByUsername() was called
        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    public void testGetUserDTO_Success() {
        // Create sample input data
        ApplicationUser user = new ApplicationUser();
        user.setUsername("ola");
        user.setEmail("ola@gmail.com");
        user.setNumberOfFollowers(10);

        // Perform the getUserDTO operation
        AppUserDTO userDTO = userService.getUserDTO(user);

        // Assert the userDTO
        assertNotNull(userDTO);
        assertEquals(user.getUsername(), userDTO.getUsername());
        assertEquals(user.getEmail(), userDTO.getEmail());
        assertEquals(UserRole.ROLE_USER, userDTO.getRole());
        assertEquals(user.getNumberOfFollowers(), userDTO.getFollowerCount());
    }

    @Test
    public void testFollow_Success() {
        // Create sample input data
        String userToFollowUsername = "ola";
        String loggedInUserUsername = "dami";

        ApplicationUser userToFollow = new ApplicationUser();
        userToFollow.setUsername(userToFollowUsername);
        ApplicationUser loggedInUser = new ApplicationUser();
        loggedInUser.setUsername(loggedInUserUsername);

        // Mock the behavior of getUserByUsername()
        when(userRepository.findByUsername(loggedInUserUsername)).thenReturn(loggedInUser);
        when(userRepository.findByUsername(userToFollowUsername)).thenReturn(userToFollow);

        // Perform the follow operation
        userService.follow(userToFollowUsername, loggedInUserUsername);

        // Verify that userRepository.findByUsername() was called twice
        verify(userRepository, times(2)).findByUsername(anyString());

        // Verify the interactions for adding to followers and following
        verify(userService, times(1)).addToFollower(loggedInUser, userToFollow);
        verify(userService, times(1)).addUserToFollowingList(loggedInUser, userToFollow);
    }

    @Test
    public void testFollow_SameUser_ThrowsException() {
        // Create sample input data
        String userToFollowUsername = "ola";
        String loggedInUserUsername = "ola"; // Same as userToFollowUsername

        ApplicationUser userToFollow = new ApplicationUser();
        userToFollow.setUsername(userToFollowUsername);

        // Mock the behavior of getUserByUsername()
        when(userRepository.findByUsername(userToFollowUsername)).thenReturn(userToFollow);

        // Perform the follow operation, expecting an exception
        userService.follow(userToFollowUsername, loggedInUserUsername);

        // Verify that userRepository.findByUsername() was called once
        verify(userRepository, times(1)).findByUsername(anyString());
    }

    @Test
    public void testUnFollow_Success() {
        // Create sample input data
        String userToUnFollowUsername = "ola";
        String loggedInUserUsername = "dami";

        ApplicationUser userToUnFollow = new ApplicationUser();
        userToUnFollow.setUsername(userToUnFollowUsername);
        ApplicationUser loggedInUser = new ApplicationUser();
        loggedInUser.setUsername(loggedInUserUsername);

        // Mock the behavior of getUserByUsername()
        when(userRepository.findByUsername(loggedInUserUsername)).thenReturn(loggedInUser);
        when(userRepository.findByUsername(userToUnFollowUsername)).thenReturn(userToUnFollow);

        // Perform the unFollow operation
        userService.unFollow(userToUnFollowUsername, loggedInUserUsername);

        // Verify that userRepository.findByUsername() was called twice
        verify(userRepository, times(2)).findByUsername(anyString());

        // Verify the interactions for removing from followers and following
        verify(userService, times(1)).removeFollower(loggedInUser, userToUnFollow);
        verify(userService, times(1)).removeUserFromFollowingList(loggedInUser, userToUnFollow);
    }

    @Test
    public void testUnFollow_NotFollowing_ThrowsException() {
        // Create sample input data
        String userToUnFollowUsername = "ola";
        String loggedInUserUsername = "dami";

        ApplicationUser userToUnFollow = new ApplicationUser();
        userToUnFollow.setUsername(userToUnFollowUsername);
        ApplicationUser loggedInUser = new ApplicationUser();
        loggedInUser.setUsername(loggedInUserUsername);

        // Mock the behavior of getUserByUsername()
        when(userRepository.findByUsername(loggedInUserUsername)).thenReturn(loggedInUser);
        when(userRepository.findByUsername(userToUnFollowUsername)).thenReturn(userToUnFollow);

        // Perform the unFollow operation, expecting an exception
        userService.unFollow(userToUnFollowUsername, loggedInUserUsername);

        // Verify that userRepository.findByUsername() was called twice
        verify(userRepository, times(2)).findByUsername(anyString());
    }


}