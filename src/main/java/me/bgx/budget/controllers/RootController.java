package me.bgx.budget.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.api.utils.SystemProperty;
import com.google.apphosting.api.ApiProxy;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping(value = "/")
public class RootController {

    private static final String LOGIN_URL;
    private static final String LOGOUT_URL;
    static {
        String hostName = "localhost";
        if (SystemProperty.environment.value() == SystemProperty.Environment.Value.Production) {
            ApiProxy.Environment env = ApiProxy.getCurrentEnvironment();
            hostName = (String) env.getAttributes().get("com.google.appengine.runtime.default_version_hostname");
        } else if (SystemProperty.environment.value() == SystemProperty.Environment.Value.Development) {
            hostName = "localhost:8080";
        }
        LOGIN_URL = "http://" + hostName + "/app/rules";
        LOGOUT_URL = "http://" + hostName + "/";
    }

    @RequestMapping(method = RequestMethod.GET)
    public String welcome() {
        return "root/welcome";
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public ModelAndView login() {
        return processLogInOut(true, LOGIN_URL);
    }

    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public ModelAndView logout() {
        return processLogInOut(false, LOGOUT_URL);
    }

    private ModelAndView processLogInOut(final boolean isLogin, final String gotoUrl) {
        UserService userService = UserServiceFactory.getUserService();
        final String href;
        if (isLogin) {
            href = userService.createLoginURL(gotoUrl);
        } else {
            href = userService.createLogoutURL(gotoUrl);
        }
        return new ModelAndView("root/loginout")
                .addObject("href", href)
                .addObject("isLogin", isLogin);
    }
}
