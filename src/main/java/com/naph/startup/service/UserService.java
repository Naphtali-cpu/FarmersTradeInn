package com.naph.startup.service;

import com.naph.startup.config.MessageStrings;
import com.naph.startup.dto.SignInDto;
import com.naph.startup.dto.SignInResponseDto;
import com.naph.startup.dto.SignUpResponseDto;
import com.naph.startup.dto.SignupDto;
import com.naph.startup.exceptions.AuthenticationFailException;
import com.naph.startup.exceptions.CustomException;
import com.naph.startup.exceptions.ProductNotExistException;
import com.naph.startup.model.AuthenticationToken;
import com.naph.startup.model.Product;
import com.naph.startup.model.User;
import com.naph.startup.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    AuthenticationService authenticationService;

    Logger logger = LoggerFactory.getLogger(UserService.class);

//    Get user details logic


    public SignUpResponseDto signUp(SignupDto signupDto) throws CustomException {

        // Check to see if the current phone number has already been registered.

        if (Objects.nonNull(userRepository.findByPhoneNumber(signupDto.getPhoneNumber()))) {
            // If the email address has been registered then throw an exception.
            throw new CustomException("Phone Number already exists");
        }
        // first encrypt the password
        String encryptedPassword = signupDto.getPassword();
        try {
            encryptedPassword = hashPassword(signupDto.getPassword());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            logger.error("hashing password failed {}", e.getMessage());
        }


        User user = new User(signupDto.getUserName(), signupDto.getPhoneNumber(), encryptedPassword );
        try {
            // save the User
            userRepository.save(user);
            // success in creating

//          Generate token for User
            final AuthenticationToken authenticationToken = new AuthenticationToken(user);

//          Save User's Token to Database
            authenticationService.saveConfirmionToke(authenticationToken);

            return new SignUpResponseDto("success", "user created successfully");
        } catch (Exception e) {
            // handle signup error
            throw new CustomException(e.getMessage());
        }
    }

//    Sign in Response exception handling
    public SignInResponseDto signIn(SignInDto signInDto) throws AuthenticationFailException, CustomException {
//        Find user's email from DB
        User user = userRepository.findByPhoneNumber(signInDto.getPhoneNumber());
        if (!Objects.nonNull(user)) {
            throw new AuthenticationFailException("Phone Number is not registered");
        }
        try {
//            Check password
            if (!user.getPassword().equals(hashPassword(signInDto.getPassword()))) {
                throw new AuthenticationFailException(MessageStrings.WRONG_PASSWORD);
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            logger.error("hashing password failed {}", e.getMessage());
            throw new CustomException(e.getMessage());
        }

        AuthenticationToken token = authenticationService.getToken(user);

        if(!Objects.nonNull(token)) {
            // token not present
            throw new CustomException(MessageStrings.AUTH_TOEKN_NOT_PRESENT);
        }

        return new SignInResponseDto ("success", token.getToken());
    }

//    Getting our user profile data

    public User getUserDetails(int profileId, User user) {
        User newUser = userRepository.findByPhoneNumber(user.getPhoneNumber());
        return newUser;
    }

//    Hashing our password

    String hashPassword(String password) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(password.getBytes());
        byte[] digest = md.digest();
        String myHash = DatatypeConverter.printHexBinary(digest).toUpperCase();
        return myHash;
    }
}
