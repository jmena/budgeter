package me.bgx.budget.web;


import java.beans.PropertyEditorSupport;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;

import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import lombok.extern.slf4j.Slf4j;
import me.bgx.budget.autowired.RulesStorageService;
import me.bgx.budget.model.v1.Rule;
import me.bgx.budget.util.EditorDescription;
import me.bgx.budget.util.editors.LocalDatePropertyEditor;
import me.bgx.budget.util.editors.NumberOfPeriodsEditor;
import me.bgx.budget.util.editors.PercentagePropertyEditor;
import me.bgx.budget.util.editors.PeriodPropertyEditor;

@Slf4j
@Controller
@RequestMapping(value = "/app/rules")
public class RulesController {

    @Autowired
    RulesStorageService rulesStorageService;

    private static final Pattern RULES_PATTERN = Pattern.compile("/app/rules/([^/]*)/.*");

    private static final PropertyEditorSupport PERCENTAGE_EDITOR = new PercentagePropertyEditor();
    private static final PropertyEditorSupport NUMBER_OF_PERIODS_EDITOR = new NumberOfPeriodsEditor();

    @InitBinder
    public void initBinder(WebDataBinder binder, HttpServletRequest request) {
        binder.registerCustomEditor(LocalDate.class, new LocalDatePropertyEditor());
        binder.registerCustomEditor(Period.class, new PeriodPropertyEditor());

        Matcher m = RULES_PATTERN.matcher(request.getRequestURI());
        if (m.matches()) {
            String type = m.group(1);
            EditorDescription editorDescription = Rule.EDITORS.get(type);
            if (editorDescription == null) {
                throw new RuntimeException("Editor doesn't exist: " + type);
            }

            Map<String, Collection<String>> fieldsByType = editorDescription.getFieldsByType();
            for (String property : def(fieldsByType.get("percentage"))) {
                binder.registerCustomEditor(double.class, property, PERCENTAGE_EDITOR);
            }
            for (String property : def(fieldsByType.get("nperiods"))) {
                binder.registerCustomEditor(int.class, property, NUMBER_OF_PERIODS_EDITOR);
            }
        }
    }

    private <T> Collection<T> def(Collection<T> lst) {
        return (lst != null) ? lst : Collections.<T>emptyList();
    }

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView list() {
        return new ModelAndView("rules/list")
                .addObject("rules", rulesStorageService.list());
    }

    @RequestMapping(value = "/{type}/{id}", method = RequestMethod.GET)
    public ModelAndView newOrEditRule(@PathVariable String type, @PathVariable String id) {

        final Rule rule;
        if ("new".equals(id)) {
            // rule doesn't exist, create a new one
            rule = Rule.newInstanceFromType(type);
        } else {
            rule = rulesStorageService.load(id);
        }

        if (rule == null) {
            // rule doesn't exists
            return new ModelAndView("redirect:/app/rules/new");
        }

        // rule found, let's edit it
        return new ModelAndView("rules/generic/edit")
                .addObject("rule", rule)
                .addObject("fields", Rule.EDITORS.get(type).getFields());
    }

    @RequestMapping(value = "/{type}/{reqId}", method = RequestMethod.POST)
    public ModelAndView saveOrUpdateRule(
            @PathVariable String type,
            @PathVariable String reqId,
            @ModelAttribute("rule") Rule rule,
            BindingResult bindingResult,
            HttpServletRequest req) {
        if (bindingResult.hasErrors()) {
            return new ModelAndView("rules/generic/edit")
                    .addObject("rule", rule)
                    .addObject("hasErrors", bindingResult.hasErrors())
                    .addObject("fields", Rule.EDITORS.get(type).getFields());
        } else {
            rulesStorageService.save(rule);
            return new ModelAndView("redirect:/app/rules/?saved=" + rule.getId());
        }
    }

    @RequestMapping(value = "/{ruleId}", method = RequestMethod.DELETE, produces = "application/json")
    @ResponseBody
    public String delete(@PathVariable String ruleId) {
        rulesStorageService.delete(ruleId);

        Map<String, String> map = new ImmutableMap.Builder<String, String>()
                .put("response", "ok")
                .build();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(map);
    }

    @RequestMapping(value = "/new", method = RequestMethod.GET)
    public ModelAndView newRule() {
        return new ModelAndView("rules/new")
                .addObject("editors", Rule.EDITORS);
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public ModelAndView newRule(@RequestParam("type") String type) {
        Rule rule = Rule.newInstanceFromType(type);
        if (rule == null) {
            // couldn't find editor for this rule. Let's try again
            return new ModelAndView("redirect:/app/rules/new");
        }
        return new ModelAndView("redirect:/app/rules/" + rule.getType() + "/new");
    }

    @ModelAttribute("rule")
    public Rule getRule(HttpServletRequest request) {
        String uri = request.getRequestURI();

        Matcher m = RULES_PATTERN.matcher(uri);
        if (m.matches()) {
            String type = m.group(1);
            return Rule.newInstanceFromType(type);
        }
        return null;
    }


}
