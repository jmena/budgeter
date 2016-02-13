package me.bgx.budget.security;

import java.security.Principal;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

import lombok.Getter;

public class BudgetHttpServletRequest extends HttpServletRequestWrapper {

    @Getter
    User user;

    public BudgetHttpServletRequest(HttpServletRequest request) {
        super(request);
        UserService userService = UserServiceFactory.getUserService();

        com.google.appengine.api.users.User googleUser = userService.getCurrentUser();
        if (googleUser != null) {
            this.user = User.builder()
                    .source(User.SourceLogin.GOOGLE)
                    .email(googleUser.getEmail())
                    .admin(userService.isUserAdmin())
                    .build();
        }
    }

    @Override
    public Principal getUserPrincipal() {
        return user;
    }
}
