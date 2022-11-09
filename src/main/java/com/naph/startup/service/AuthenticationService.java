package com.naph.startup.service;

import com.naph.startup.config.MessageStrings;
import com.naph.startup.exceptions.AuthenticationFailException;
import com.naph.startup.model.AuthenticationToken;
import com.naph.startup.model.User;
import com.naph.startup.repository.TokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class AuthenticationService {

    @Autowired
    TokenRepository repository;

//    Save confirmed tokens
    public void saveConfirmionToke(AuthenticationToken authenticationToken) {
        repository.save(authenticationToken);
    }

//    Get User's token
    public AuthenticationToken getToken(User user) {
        return repository.findTokenByUser(user);
    }

//    Get User from the token
    public User getUser(String token) {
        AuthenticationToken authenticationToken = repository.findTokenByToken(token);
        if (Objects.nonNull(authenticationToken)) {
            if (Objects.nonNull(authenticationToken.getUser())) {
                return authenticationToken.getUser();
            }
        }
        return null;
    }

//    Check if token is valid
    public void authenticate(String token) throws AuthenticationFailException {
        if (!Objects.nonNull(token)) {
            throw new AuthenticationFailException(MessageStrings.AUTH_TOEKN_NOT_PRESENT);
        }
        if (!Objects.nonNull(getUser(token))) {
            throw new AuthenticationFailException((MessageStrings.AUTH_TOEKN_NOT_VALID));
        }
    }
}
