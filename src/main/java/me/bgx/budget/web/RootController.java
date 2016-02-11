package me.bgx.budget.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping(value = "/")
public class RootController {

    @RequestMapping(method = RequestMethod.GET)
    public String welcome() {
        return "root/welcome";
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public ModelAndView login() {
        return processLogInOut(true, "http://localhost:8080/app/rules");
    }

    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public ModelAndView logout() {
        return processLogInOut(false, "http://localhost:8080/");
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
