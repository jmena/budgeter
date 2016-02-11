package me.bgx.budget.web;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.joda.time.LocalDate;
import org.joda.time.YearMonth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.bgx.budget.model.v1.Amount;
import me.bgx.budget.model.v1.Rule;
import me.bgx.budget.service.RulesStorageService;

@Slf4j
@Controller
@RequestMapping(value = "/app/simulations")
public class SimulationsController {

    private static final Double ZERO = 0.0;
    private static final YearMonth MIN_DATE = new YearMonth(1, 1);
    private static final YearMonth MAX_DATE = new YearMonth(3000, 1);
    private static final DefaultValueProvider<Double> ZERO_DEFAULT = new DefaultValueProvider<Double>() {
        @Override
        public Double get() {
            return ZERO;
        }
    };

    private static final DefaultValueProvider<Map<String, Double>> DEFAULT_MAP = new DefaultValueProvider<Map<String, Double>>() {
        @Override
        public Map<String, Double> get() {
            return new MapWithDefault<>(ZERO_DEFAULT);
        }
    };

    private interface DefaultValueProvider<V> {
        V get();
    }

    @AllArgsConstructor
    private static class MapWithDefault<K, V> extends HashMap<K, V> {
        DefaultValueProvider<V> defaultValueProvider;

        V getDefaultValue() {
            return defaultValueProvider.get();
        }

        @Override
        public V get(Object k) {
            if (!containsKey(k)) {
                put((K) k, getDefaultValue());
            }
            return super.get(k);
        }
    }

    @Autowired
    RulesStorageService rulesStorageService;

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView list() {
        // Collection<Amount> amounts = allAmounts().stream().filter(between(from, to)).collect(Collectors.toList());
        Collection<Amount> amounts = allAmounts();

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
        Map<YearMonth, Map<String, Double>> calendar = new MapWithDefault<>(DEFAULT_MAP);
        Map<String, Rule> rulesByDescription = new LinkedHashMap<>();
        getCalendar(amounts, calendar, rulesByDescription);

        // other fields
        List<YearMonth> months = getMonths(minMonth, maxMonth);
        Map<YearMonth, Double> totalsByMonth = getTotalsByMonth(calendar, minMonth, maxMonth);
        Map<YearMonth, Double> balanceByMonth = getBalanceByMonth(minMonth, maxMonth, totalsByMonth);
        Map<YearMonth, Double> maximumSavingsByMonth = getMaximumSavingsByMonth(minMonth, maxMonth, balanceByMonth);

        return new ModelAndView("simulations/list")
                .addObject("months", months)
                .addObject("descriptions", getSortedCollection(rulesByDescription.keySet()))
                .addObject("calendar", calendar)
                .addObject("rulesByDescription", rulesByDescription)
                .addObject("maximumSavingsByMonth", maximumSavingsByMonth)
                .addObject("totalsByMonth", totalsByMonth)
                .addObject("balanceByMonth", balanceByMonth);
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
                             Map<String, Rule> rulesByDescription)
    {
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
        Map<YearMonth, Double> totalsByMonth = new MapWithDefault<>(ZERO_DEFAULT);
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
        Map<YearMonth, Double> balanceByMonth = new MapWithDefault<>(ZERO_DEFAULT);
        double totalAcum = 0.0;
        for (YearMonth yearMonth = minMonth; yearMonth.compareTo(maxMonth) <= 0; yearMonth = yearMonth.plusMonths(1)) {
            double totalByMonth = totalsByMonth.get(yearMonth);
            totalAcum += totalByMonth;
            balanceByMonth.put(yearMonth, totalAcum);
        }
        return balanceByMonth;
    }

    private Map<YearMonth, Double> getMaximumSavingsByMonth(YearMonth minMonth, YearMonth maxMonth, Map<YearMonth, Double> balanceByMonth) {
        Map<YearMonth, Double> maximumSavingsByMonth = new MapWithDefault<>(ZERO_DEFAULT);
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

    private Collection<Amount> allAmounts() {
        Collection<Amount> amounts = new ArrayList<>();
        for (Rule rule : rulesStorageService.list()) {
            amounts.addAll(rule.generate());
        }
        return amounts;
    }

    private YearMonth convertLocalDateToYearMonth(final LocalDate date) {
        return new YearMonth(date.getYear(), date.getMonthOfYear());
    }

}
