package com.social.app.user.controller;


import com.social.app.general.dto.Response;
import com.social.app.general.enums.ResponseCodeAndMessage;
import com.social.app.user.dto.SignInRequest;
import com.social.app.user.dto.SignUpRequest;
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

    @PostMapping("sign-up")
    public ResponseEntity<Response> signUp(@Valid @RequestBody SignUpRequest signUpRequest) {
        Response data = userService.signup(signUpRequest);
        if (data.getResponseCode().equals(ResponseCodeAndMessage.SUCCESSFUL_0.responseCode))
            return new ResponseEntity<>(data, HttpStatus.CREATED);
        else return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }

    @PostMapping("sign-in")
    public ResponseEntity<Response> signIn(@RequestBody SignInRequest request) {
        Response data = userService.signIn(request.getUsername(), request.getPassword());
        if (data.getResponseCode().equals(ResponseCodeAndMessage.SUCCESSFUL_0.responseCode))
            return new ResponseEntity<>(data, HttpStatus.OK);
        else return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/update/{userId}")
    public ResponseEntity<Response> updateUser(@Valid @RequestBody SignUpRequest signUpRequest, @PathVariable Long userId, Principal principal) {
        Response data = userService.updateUser(signUpRequest, userId, principal.getName());
        if (data.getResponseCode().equals(ResponseCodeAndMessage.SUCCESSFUL_0.responseCode))
            return new ResponseEntity<>(data, HttpStatus.OK);
        else return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/delete/{userId}")
    public ResponseEntity<Response> updateUser(@PathVariable Long userId, Principal principal) {
        userService.deleteUser(userId, principal.getName());
        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    @PostMapping("/follow")
    public ResponseEntity<Response> follow(@Valid @RequestBody String followerUserName, Principal principal) {
        Response data = userService.followAfriend(followerUserName, principal.getName());
        if (data.getResponseCode().equals(ResponseCodeAndMessage.SUCCESSFUL_0.responseCode))
            return new ResponseEntity<>(data, HttpStatus.OK);
        else return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/unFollow")
    public ResponseEntity<Response> unFollow(@Valid @RequestBody String followerUserName, Principal principal) {
        Response data = userService.unFollow(followerUserName, principal.getName());
        if (data.getResponseCode().equals(ResponseCodeAndMessage.SUCCESSFUL_0.responseCode))
            return new ResponseEntity<>(data, HttpStatus.OK);
        else return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }

}
