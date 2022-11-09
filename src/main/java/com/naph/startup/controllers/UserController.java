package com.naph.startup.controllers;

import com.naph.startup.config.ApiResponse;
import com.naph.startup.dto.SignInDto;
import com.naph.startup.dto.SignInResponseDto;
import com.naph.startup.dto.SignUpResponseDto;
import com.naph.startup.dto.SignupDto;
import com.naph.startup.exceptions.AuthenticationFailException;
import com.naph.startup.exceptions.CustomException;
import com.naph.startup.model.User;
import com.naph.startup.service.AuthenticationService;
import com.naph.startup.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("user")
@RestController
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    AuthenticationService authenticationService;

//    Register new user

    @PostMapping("/signup")
    public SignUpResponseDto Signup(@RequestBody SignupDto signupDto) throws CustomException {
        return userService.signUp(signupDto);
    }

//    Sign in/ Login user Implementation

    @PostMapping("/signIn")
    public SignInResponseDto Signup(@RequestBody SignInDto signInDto) throws CustomException, AuthenticationFailException {
        return userService.signIn(signInDto);
    }

    @GetMapping("profile/{userId}")
    public ResponseEntity<ApiResponse> getUserDetails(
            @PathVariable("userId") int profileId,
            @RequestParam("token") String token
    ) throws AuthenticationFailException {
        authenticationService.authenticate(token);
        User user = authenticationService.getUser(token);

        userService.getUserDetails(profileId, user);
        return new ResponseEntity<>(new ApiResponse(true, "User Profile Data Works Successfully!"), HttpStatus.OK);
    }


}
