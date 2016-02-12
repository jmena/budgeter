package me.bgx.budget.util;

import javax.servlet.ServletRequest;

import com.google.appengine.api.users.User;

public class RequestHelper {
    public static User getUser(ServletRequest req) {
        return (User) req.getAttribute("user");
    }
}
