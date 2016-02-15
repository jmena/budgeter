package me.bgx.budget.controllers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;

import org.joda.time.LocalDate;
import org.joda.time.YearMonth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import lombok.extern.slf4j.Slf4j;
import me.bgx.budget.model.data.rules.Rule;
import me.bgx.budget.model.generators.Amount;
import me.bgx.budget.model.generators.Generator;
import me.bgx.budget.model.services.RulesMetadataService;
import me.bgx.budget.model.services.RulesStorageService;
import me.bgx.budget.util.MapWithDefaultValue;

@Slf4j
@Controller
@RequestMapping(value = "/app/simulations")
public class SimulationsController {

    private static final YearMonth MIN_DATE = new YearMonth(1, 1);
    private static final YearMonth MAX_DATE = new YearMonth(3000, 1);

    @Autowired
    RulesStorageService rulesStorageService;

    @Autowired
    RulesMetadataService rulesMetadataService;

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView list(HttpServletRequest req) {

        Collection<Rule> rules = null; // TODO: fix this rulesStorageService.list();
        List<String> tags = getTags(rules);

        // TODO: this must be read from the user
        LocalDate until = new LocalDate().plusYears(4);
        Collection<Amount> amounts = allAmounts(rules, until);

        YearMonth minMonth = MAX_DATE;
        YearMonth maxMonth = MIN_DATE;

        // get min/max date
        for (Amount amount : amounts) {
            LocalDate date = amount.getDate();
            YearMonth yearMonth = convertLocalDateToYearMonth(date);
            minMonth = (yearMonth.compareTo(minMonth) < 0) ? yearMonth : minMonth;
            maxMonth = (yearMonth.compareTo(maxMonth) > 0) ? yearMonth : maxMonth;
        }

        // generate calendar
        Map<YearMonth, Map<String, Double>> calendar = new MapWithDefaultValue<>(MapWithDefaultValue.MAP_ZERO_DOUBLE_VALUE_PROVIDER);
        Map<String, Rule> rulesByDescription = new LinkedHashMap<>();
        getCalendar(amounts, calendar, rulesByDescription);

        // other fields
        List<YearMonth> months = getMonths(minMonth, maxMonth);
        Map<YearMonth, Double> totalsByMonth = getTotalsByMonth(calendar, minMonth, maxMonth);
        Map<YearMonth, Double> balanceByMonth = getBalanceByMonth(minMonth, maxMonth, totalsByMonth);
        Map<YearMonth, Double> maximumSavingsByMonth = getMaximumSavingsByMonth(minMonth, maxMonth, balanceByMonth);

        return new ModelAndView("simulations/list")
                .addObject("months", months)
                .addObject("tags", tags)
                .addObject("descriptions", getSortedCollection(rulesByDescription.keySet()))
                .addObject("calendar", calendar)
                .addObject("rulesByDescription", rulesByDescription)
                .addObject("maximumSavingsByMonth", maximumSavingsByMonth)
                .addObject("totalsByMonth", totalsByMonth)
                .addObject("balanceByMonth", balanceByMonth);
    }

    private List<String> getTags(Collection<Rule> rules) {
        Map<String, Integer> tagsTmp = new MapWithDefaultValue<>(MapWithDefaultValue.ZERO_INT_VALUE_PROVIDER);
        for (Rule rule : rules) {
            for (String tag : rule.getTags()) {
                tagsTmp.put(tag, tagsTmp.get(tag) + 1);
            }
        }
        List<String> tags = new ArrayList<>();
        for (Map.Entry<String, Integer> e : tagsTmp.entrySet()) {
            tags.add(e.getKey() + " (" + e.getValue() + ")");
        }
        Collections.sort(tags);
        return tags;
    }

    private List<YearMonth> getMonths(YearMonth minMonth, YearMonth maxMonth) {
        List<YearMonth> months = new ArrayList<>();
        for (YearMonth yearMonth = minMonth; yearMonth.compareTo(maxMonth) <= 0; yearMonth = yearMonth.plusMonths(1)) {
            months.add(yearMonth);
        }
        return months;
    }

    private void getCalendar(Collection<Amount> amounts,
                             Map<YearMonth, Map<String, Double>> calendar,
                             Map<String, Rule> rulesByDescription) {
        for (Amount amount : amounts) {
            LocalDate date = amount.getDate();
            String description = amount.getDescription();
            YearMonth yearMonth = convertLocalDateToYearMonth(date);
            Map<String, Double> day = calendar.get(yearMonth);
            Double currentSum = day.get(description) + amount.getAmount();
            day.put(description, currentSum);
            rulesByDescription.put(description, amount.getRule());
        }
    }

    private Map<YearMonth, Double> getTotalsByMonth(Map<YearMonth, Map<String, Double>> calendar, YearMonth minMonth, YearMonth maxMonth) {
        Map<YearMonth, Double> totalsByMonth = new MapWithDefaultValue<>(MapWithDefaultValue.ZERO_DOUBLE_VALUE_PROVIDER);
        for (YearMonth yearMonth = minMonth; yearMonth.compareTo(maxMonth) <= 0; yearMonth = yearMonth.plusMonths(1)) {
            double totalMonth = 0.0;
            for (double v : calendar.get(yearMonth).values()) {
                totalMonth += v;
            }
            totalsByMonth.put(yearMonth, totalMonth);
        }
        return totalsByMonth;
    }

    private Map<YearMonth, Double> getBalanceByMonth(YearMonth minMonth, YearMonth maxMonth, Map<YearMonth, Double> totalsByMonth) {
        Map<YearMonth, Double> balanceByMonth = new MapWithDefaultValue<>(MapWithDefaultValue.ZERO_DOUBLE_VALUE_PROVIDER);
        double totalAcum = 0.0;
        for (YearMonth yearMonth = minMonth; yearMonth.compareTo(maxMonth) <= 0; yearMonth = yearMonth.plusMonths(1)) {
            double totalByMonth = totalsByMonth.get(yearMonth);
            totalAcum += totalByMonth;
            balanceByMonth.put(yearMonth, totalAcum);
        }
        return balanceByMonth;
    }

    private Map<YearMonth, Double> getMaximumSavingsByMonth(YearMonth minMonth, YearMonth maxMonth, Map<YearMonth, Double> balanceByMonth) {
        Map<YearMonth, Double> maximumSavingsByMonth = new MapWithDefaultValue<>(MapWithDefaultValue.ZERO_DOUBLE_VALUE_PROVIDER);
        double maxSavings = Double.POSITIVE_INFINITY;

        for (YearMonth yearMonth = maxMonth; yearMonth.compareTo(minMonth) >= 0; yearMonth = yearMonth.minusMonths(1)) {
            double v = balanceByMonth.get(yearMonth);
            maxSavings = Math.max(0.0, Math.min(maxSavings, v));

            maximumSavingsByMonth.put(yearMonth, maxSavings);
        }
        return maximumSavingsByMonth;
    }

    private Collection<String> getSortedCollection(Set<String> inputSet) {
        Collection<String> strs = new ArrayList<>();
        for (String str : inputSet) {
            strs.add(str);
        }
        return strs;
    }

    private Collection<Amount> allAmounts(Collection<Rule> rules, LocalDate until) {
        Collection<Amount> amounts = new ArrayList<>();
        for (Rule rule : rules) {
            Generator generator = rulesMetadataService.getGeneratorFor(rule);
            amounts.addAll(generator.generate(until));
        }
        return amounts;
    }

    private YearMonth convertLocalDateToYearMonth(final LocalDate date) {
        return new YearMonth(date.getYear(), date.getMonthOfYear());
    }

}
