package me.bgx.budget.security;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

public class SecurityFilter extends FilterAdapter {

    @Override
    public void doFilter(ServletRequest rawReq, ServletResponse rawRes, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) rawReq;
        HttpServletResponse res = (HttpServletResponse) rawRes;
        if (isGoogleLogin(req, res)) {
            chain.doFilter(req, res);
        }
    }

    private boolean isGoogleLogin(HttpServletRequest req, HttpServletResponse res) throws IOException {
        UserService userService = UserServiceFactory.getUserService();
        String path = req.getRequestURI();
        boolean superAdminPath = path.startsWith("/app/su/");
        boolean protectedPath = path.startsWith("/app/");
        boolean isLoggedIn = userService.isUserLoggedIn();

        if (protectedPath && !isLoggedIn) {
            // go to login screen
            res.sendRedirect("/login");
            return false;
        }

        boolean isAdmin = isLoggedIn && userService.isUserAdmin();
        if (superAdminPath && !isAdmin) {
            // 403: forbidden
            res.sendError(HttpServletResponse.SC_FORBIDDEN);
            return false;
        }

        User user = userService.getCurrentUser();
        req.setAttribute("user", user);
        req.setAttribute("isAdmin", isAdmin);
        return true;
    }
}
