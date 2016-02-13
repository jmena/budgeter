package me.bgx.budget.security;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Determines if the user has access to the pages
 */
public class SecurityFilter extends SimpleFilterAdapter {

    @Override
    public void doFilter(ServletRequest rawReq, ServletResponse rawRes, FilterChain chain) throws IOException, ServletException {
        BudgetHttpServletRequest req = new BudgetHttpServletRequest((HttpServletRequest) rawReq);
        HttpServletResponse res = (HttpServletResponse) rawRes;

        if (grantAccess(req, res)) {
            chain.doFilter(req, res);
        }
    }

    private boolean grantAccess(BudgetHttpServletRequest req, HttpServletResponse res) throws IOException {
        User user = req.getUser();
        String path = req.getRequestURI();

        boolean superAdminPath = path.startsWith("/app/su/");
        boolean protectedPath = path.startsWith("/app/");
        boolean isLoggedIn = user != null;

        if (protectedPath && !isLoggedIn) {
            // go to login screen
            res.sendRedirect("/login");
            return false;
        }

        boolean isAdmin = isLoggedIn && user.isAdmin();
        if (superAdminPath && !isAdmin) {
            // 403: forbidden
            res.sendError(HttpServletResponse.SC_FORBIDDEN);
            return false;
        }

        return true;
    }
}
