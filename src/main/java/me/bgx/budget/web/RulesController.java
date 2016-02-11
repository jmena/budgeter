package me.bgx.budget.web;


import java.beans.PropertyEditorSupport;
import java.util.Collection;
import java.util.Collections;
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
import org.springframework.web.servlet.ModelAndView;

import com.google.common.base.Strings;

import lombok.extern.slf4j.Slf4j;
import me.bgx.budget.model.v1.Rule;
import me.bgx.budget.service.RulesStorageService;
import me.bgx.budget.util.EditorDescription;
import me.bgx.budget.util.LocalDateEditor;
import me.bgx.budget.util.PeriodEditor;

@Slf4j
@Controller
@RequestMapping(value = "/app/rules")
public class RulesController {

    @Autowired
    RulesStorageService rulesStorageService;

    private static final Pattern RULES_PATTERN = Pattern.compile("/app/rules/([^/]*)/.*");

    private static final PropertyEditorSupport PERCENTAGE_EDITOR = new PropertyEditorSupport() {
        @Override
        public String getAsText() {
            Double value = (Double) getValue();
            if (getValue() == null) {
                return "0%";
            }
            String v = String.format("%f", value * 100);

            if (v.endsWith(".")) {
                v = v.substring(0, v.length() - 1);
            }
            return  v + "%";
        }

        @Override
        public void setAsText(String text) throws IllegalArgumentException {
            if (text == null) {
                setValue(0.0);
                return;
            }
            text = text.trim();
            try {
                if (text.endsWith("%")) {
                    text = text.substring(0, text.length() - 1);
                    setValue(Double.parseDouble(text) / 100);
                } else if (text.startsWith("%")) {
                    text = text.substring(1, text.length());
                    setValue(Double.parseDouble(text) / 100);
                } else {
                    setValue(Double.parseDouble(text));
                }
            } catch (NumberFormatException nfe) {
                throw new RuntimeException("Invalid percentage: " + text, nfe);
            }
        }
    };

    @InitBinder
    public void initBinder(WebDataBinder binder, HttpServletRequest request) {
        binder.registerCustomEditor(LocalDate.class, new LocalDateEditor());
        binder.registerCustomEditor(Period.class, new PeriodEditor());

        Matcher m = RULES_PATTERN.matcher(request.getRequestURI());
        if (m.matches()) {
            String type = m.group(1);
            EditorDescription editorDescription = Rule.EDITORS.get(type);
            if (editorDescription == null) {
                throw new RuntimeException("Editor doesn't exist: " + type);
            }

            Collection<String> percentageProps = editorDescription.getFieldsByType().get("percentage");
            if (percentageProps == null) {
                percentageProps = Collections.emptyList();
            }
            for (String property : percentageProps) {
                binder.registerCustomEditor(double.class, property, PERCENTAGE_EDITOR);
            }
        }
    }

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView list() {
        return new ModelAndView("rules/list").addObject("rules", rulesStorageService.list());
    }

    @RequestMapping(value = "/{type}/{id}", method = RequestMethod.GET)
    public ModelAndView newOrEditRule(@PathVariable String type, @PathVariable String id) {

        Rule rule = null;
        if (id != null && !"new".equals(id) && !Strings.isNullOrEmpty(id)) {
            rule = rulesStorageService.get(id);
        }
        if (rule == null) {
            // rule doesn't exist, create a new one
            rule = Rule.newInstanceFromType(type);
        }
        if (rule == null) {
            // couldn't find any rule
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
            BindingResult bindingResult) {
        rulesStorageService.save(rule);
        return new ModelAndView("redirect:/app/rules/" + rule.getType() + "/" + rule.getId());
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
