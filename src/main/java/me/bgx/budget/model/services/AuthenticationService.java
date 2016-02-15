package me.bgx.budget.model.services;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.Setter;
import me.bgx.budget.security.User;

@Service
public class AuthenticationService {
    @Setter
    @Autowired
    HttpServletRequest req;

    public User getUser() {
        return (User) req.getUserPrincipal();
    }

    public String getUserId() {
        return getUser().getEmail();
    }
}
