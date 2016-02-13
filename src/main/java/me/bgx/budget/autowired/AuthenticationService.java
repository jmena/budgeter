package me.bgx.budget.autowired;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import me.bgx.budget.security.User;

@Service
public class AuthenticationService {
    @Autowired
    HttpServletRequest req;

    public User getUser() {
        return (User) req.getUserPrincipal();
    }

    public String getUserId() {
        return getUser().getEmail();
    }
}
