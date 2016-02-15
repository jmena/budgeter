package me.bgx.budget.controllers;

import java.util.Collection;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import lombok.extern.slf4j.Slf4j;
import me.bgx.budget.model.services.RulesStorageService;
import me.bgx.budget.model.data.rules.Rule;

@Slf4j
@Controller
@RequestMapping(value = "/app/su")
public class SuperUserController {
    @Autowired
    RulesStorageService rulesStorageService;

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView list() {
        return new ModelAndView("su/list");
    }

    @RequestMapping(value = "/export", method = RequestMethod.GET)
    public ModelAndView export(HttpServletRequest req) {
        // TODO: maybe fix this method
//        Collection<Rule> rules = rulesStorageService.list();
//
//        Gson gson = new GsonBuilder().setPrettyPrinting().create();
//        String json = gson.toJson(rules);
//
//        return new ModelAndView("su/export-rules")
//                .addObject("rulesJson", json);
        return null;
    }
}
