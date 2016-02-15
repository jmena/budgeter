package me.bgx.budget.controllers;

import java.beans.PropertyEditorSupport;
import java.util.ListIterator;
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
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.VoidWork;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import me.bgx.budget.model.data.Project;
import me.bgx.budget.model.data.RegisteredUser;
import me.bgx.budget.model.data.rules.Rule;
import me.bgx.budget.model.services.ProjectsStorageService;
import me.bgx.budget.model.services.RegisteredUsersStorageService;
import me.bgx.budget.model.services.RulesMetadataService;
import me.bgx.budget.model.services.RulesStorageService;
import me.bgx.budget.util.OfyUtils;
import me.bgx.budget.util.editors.LocalDatePropertyEditor;
import me.bgx.budget.util.editors.NumberOfPeriodsEditor;
import me.bgx.budget.util.editors.PercentagePropertyEditor;
import me.bgx.budget.util.editors.PeriodPropertyEditor;
import static com.googlecode.objectify.ObjectifyService.ofy;

@Slf4j
@Controller
@RequestMapping(value = "/app/projects")
public class ProjectsController {

    @Setter
    @Autowired
    RegisteredUsersStorageService registeredUsersStorageService;

    @Setter
    @Autowired
    ProjectsStorageService projectsStorageService;

    @Autowired
    RulesStorageService rulesStorageService;

    @Autowired
    RulesMetadataService rulesMetadataService;

    private static final Pattern RULES_PATTERN = Pattern.compile("/app/projects/[^/]+/rules/([^/]+)/.*");

    private static final PropertyEditorSupport PERCENTAGE_EDITOR = new PercentagePropertyEditor();
    private static final PropertyEditorSupport NUMBER_OF_PERIODS_EDITOR = new NumberOfPeriodsEditor();

    @InitBinder
    public void initBinder(WebDataBinder binder, HttpServletRequest request) {
        binder.registerCustomEditor(LocalDate.class, new LocalDatePropertyEditor());
        binder.registerCustomEditor(Period.class, new PeriodPropertyEditor());

        Matcher m = RULES_PATTERN.matcher(request.getRequestURI());
        if (m.matches()) {
            String type = m.group(1);

            for (String property : rulesMetadataService.getProperties(type, "percentage")) {
                binder.registerCustomEditor(double.class, property, PERCENTAGE_EDITOR);
            }
            for (String property : rulesMetadataService.getProperties(type, "nperiods")) {
                binder.registerCustomEditor(int.class, property, NUMBER_OF_PERIODS_EDITOR);
            }
        }
    }

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView listProjects() {
        RegisteredUser registeredUser = registeredUsersStorageService.load();
        OfyUtils.preload(registeredUser.getProjects());
        return new ModelAndView("projects/list")
                .addObject("registeredUser", registeredUser);
    }

    @RequestMapping(value = "/{projectId}", method = RequestMethod.GET)
    public ModelAndView viewProject(@PathVariable String projectId) {
        final Project project = projectsStorageService.load(projectId);
        OfyUtils.preload(project.getRules());
        return new ModelAndView("projects/view")
                .addObject("project", project);

    }

    @RequestMapping(value = "/edit/{projectId}", method = RequestMethod.GET)
    public ModelAndView editProject(@PathVariable String projectId) {
        final Project project;
        if ("new".equals(projectId)) {
            // project doesn't exist, create a new one
            project = new Project();
        } else {
            project = projectsStorageService.load(projectId);
        }

        if (project == null) {
            // project doesn't exists
            return new ModelAndView("redirect:/app/projects/new");
        }

        // project found, let's edit it
        return new ModelAndView("projects/edit")
                .addObject("project", project);
    }

    @RequestMapping(value = "/edit/{projectId}", method = RequestMethod.POST)
    public ModelAndView saveProject(
            @PathVariable String projectId,
            @ModelAttribute("project") Project project,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ModelAndView("projects/edit")
                    .addObject("project", project)
                    .addObject("hasErrors", bindingResult.hasErrors());
        } else {
            projectsStorageService.save(project);
            return new ModelAndView("redirect:/app/projects/?saved=" + project.getId());
        }
    }


    @RequestMapping(value = "/{projectId}/rules/{type}/{ruleId}", method = RequestMethod.GET)
    public ModelAndView editRule(
            @PathVariable String projectId,
            @PathVariable String type,
            @PathVariable String ruleId) {
        final Rule rule;
        if ("new".equals(ruleId)) {
            // rule doesn't exist, create a new one
            rule = rulesMetadataService.newRuleFromType(type);
        } else {
            rule = rulesStorageService.load(ruleId);
        }

        if (rule == null) {
            // rule doesn't exists
            return new ModelAndView("redirect:/app/projects/" + projectId + "/rules/new");
        }

        // rule found, let's edit it
        return new ModelAndView("projects/rules/edit")
                .addObject("projectId", projectId)
                .addObject("rule", rule)
                .addObject("fields", rulesMetadataService.getFields(type));
    }

    @RequestMapping(value = "/{projectId}/rules/{type}/{ruleId}", method = RequestMethod.POST)
    public ModelAndView saveRule(
            @PathVariable final String projectId,
            @PathVariable String type,
            @PathVariable String ruleId,
            @ModelAttribute("rule") final Rule rule,
            BindingResult bindingResult,
            HttpServletRequest req) {
        if (bindingResult.hasErrors()) {
            return new ModelAndView("projects/rules/edit")
                    .addObject("rule", rule)
                    .addObject("hasErrors", bindingResult.hasErrors())
                    .addObject("fields", rulesMetadataService.getFields(type));
        } else {
            Key<Rule> ruleKey = rulesStorageService.save(rule);

            Project project = projectsStorageService.load(projectId);
            project.getRules().add(Ref.create(ruleKey));
            projectsStorageService.save(project);

            return new ModelAndView("redirect:/app/projects/" + projectId + "?saved=" + rule.getId());
        }
    }

    @RequestMapping(value = "/{projectId}/rules/{ruleId}", method = RequestMethod.DELETE, produces = "application/json")
    @ResponseBody
    public String delete(@PathVariable String projectId, @PathVariable String ruleId) {

        Project project = projectsStorageService.load(projectId);
        for (ListIterator<Ref<Rule>> it = project.getRules().listIterator(); it.hasNext(); ) {
            Ref<Rule> ruleRef = it.next();
            Rule rule = ruleRef.get();
            // delete current rule or nonexistent rules
            if (rule == null || ruleId.equals(rule.getId())) {
                it.remove();
            }
        }
        projectsStorageService.save(project);
        rulesStorageService.delete(ruleId);

        Map<String, String> map = new ImmutableMap.Builder<String, String>()
                .put("response", "ok")
                .build();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(map);
    }


    @RequestMapping(value = "/{projectId}/rules/new", method = RequestMethod.GET)
    public ModelAndView newRule(@PathVariable String projectId) {
        return new ModelAndView("projects/rules/new")
                .addObject("projectId", projectId)
                .addObject("editors", rulesMetadataService.getEditors());
    }

    @RequestMapping(value = "/{projectId}/rules/new", method = RequestMethod.POST)
    public ModelAndView newRule(@PathVariable String projectId, @RequestParam("type") String type) {
        Rule rule = rulesMetadataService.newRuleFromType(type);
        if (rule == null) {
            // couldn't find editor for this rule. Let's try again
            return new ModelAndView("redirect:/app/projects/" + projectId + "/rules/new");
        }
        return new ModelAndView("redirect:/app/projects/" + projectId + "/rules/" + rule.getType() + "/new");
    }


    @ModelAttribute("rule")
    public Rule getRule(HttpServletRequest request) {
        String uri = request.getRequestURI();

        Matcher m = RULES_PATTERN.matcher(uri);
        if (m.matches()) {
            String type = m.group(1);
            return rulesMetadataService.newRuleFromType(type);
        }
        return null;
    }
}
