package com.social.app.user.controller;


import com.social.app.general.dto.Response;
import com.social.app.general.enums.ResponseCodeAndMessage;
import com.social.app.general.service.GeneralService;
import com.social.app.user.dto.*;
import com.social.app.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;


@RestController
@RequestMapping("api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    private final GeneralService generalService;

    @PostMapping("sign-up")
    public Response signUp(@Valid @RequestBody SignUpRequest signUpRequest) {
        AppUserDTO data = userService.signup(signUpRequest);
        return generalService.prepareResponse(ResponseCodeAndMessage.SUCCESSFUL_0, data);
    }

    @PostMapping("sign-in")
    public ResponseEntity<Response> signIn(@RequestBody SignInRequest request) {
        Response data = userService.signIn(request.getUsername(), request.getPassword());
        if (data.getResponseCode().equals(ResponseCodeAndMessage.SUCCESSFUL_0.responseCode))
            return new ResponseEntity<>(data, HttpStatus.OK);
        else return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/update/{userId}")
    public Response updateUser(@Valid @RequestBody SignUpRequest signUpRequest, @PathVariable Long userId, Principal principal) {
        AppUserDTO data = userService.updateUser(signUpRequest, userId, principal.getName());
        return generalService.prepareResponse(ResponseCodeAndMessage.SUCCESSFUL_0, data);
    }

    @PostMapping("/delete/{userId}")
    public ResponseEntity<Response> updateUser(@PathVariable Long userId, Principal principal) {
        userService.deleteUser(userId, principal.getName());
        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    @PostMapping("/follow/{followerUserName}")
    public ResponseEntity<String> follow(@PathVariable String followerUserName, Principal principal) {
        userService.follow(followerUserName, principal.getName());
        return new ResponseEntity<>("Successfully followed the user", HttpStatus.OK);
    }

    @PostMapping("/unFollow/{followerUserName}")
    public ResponseEntity<String> unFollow(@PathVariable String followerUserName, Principal principal) {
        userService.unFollow(followerUserName, principal.getName());
        return new ResponseEntity<>("Successfully UnFollowed the user", HttpStatus.OK);
    }

    @PostMapping()
    public Response getAll(@Valid @RequestBody UserRequestDTO request) {
        UserListDTO data = userService.getAllUsers(request);
        return generalService.prepareResponse(ResponseCodeAndMessage.SUCCESSFUL_0, data);
    }

    //create api to get followers of a particular user
    @PostMapping("/getAllFollowers")
    public Response getAllFollowersOfAUser(@Valid @RequestBody UserRequestDTO request) {
        UserListDTO data = userService.getAllUsers(request);
        return generalService.prepareResponse(ResponseCodeAndMessage.SUCCESSFUL_0, data);
    }

}
