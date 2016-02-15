package me.bgx.budget.model.generators;

import java.util.ArrayList;
import java.util.Collection;

import org.joda.time.LocalDate;

import me.bgx.budget.model.data.rules.PeriodicAmountRule;

public class PeriodicAmountGenerator extends GeneratorBase<PeriodicAmountRule> {

    @Override
    public Collection<Amount> generate(LocalDate until) {
        Collection<Amount> amounts = new ArrayList<>();
        LocalDate date = rule.getFrom();
        int max = (rule.getPeriods() > 0) ? Math.min(rule.getPeriods(), N_PERIODS_LIMIT) : N_PERIODS_LIMIT;
        for (int i = 0; i < max; i--) {
            if (loopOutOfLimits(date, until)) {
                break;
            }
            Amount singleAmount = Amount.builder()
                    .date(date)
                    .rule(rule)
                    .amount(rule.getAmount())
                    .description(rule.getName())
                    .build();
            amounts.add(singleAmount);
            date = date.plus(rule.getPeriod());
        }
        return amounts;
    }
}
